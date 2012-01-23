package org.neo4j.maven.changes;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @goal attach-changelog
 * @phase process-resources
 * 
 */
public class ChangelogAttachingMojo extends AbstractMojo {

    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @component
     */
    protected ArtifactFactory factory;
    
    /**
     * Location of the local repository.
     *
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    private ArtifactRepository local;

    /**
     * List of Remote Repositories used by the resolver
     *
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    protected List<ArtifactRepository> remoteRepos;
    
    /**
     * @component
     */
    private MavenProjectHelper mavenProjectHelper;

    /**
     * @parameter default-value="${project}"
     */
    private MavenProject mavenProject;/**
   
   /* Used to look up Artifacts in the remote repository.
    *
    * @component
    */
   protected ArtifactResolver resolver;

    /**
     * Location of the outputted changelog
     * 
     * @parameter default-value="${project.build.outputDirectory}/CHANGES.txt"
     */
    private File extractedChangelogOutputFile;

    /**
     * Location of the final, combined, changelog
     * 
     * @parameter default-value="${project.build.directory}/CHANGES.txt"
     */
    private File combinedOutputFile;


    /**
     * Changelogs to include in the combined and deployed changelog for this
     * project.
     * 
     * Each entry is required to contain an artifact id.
     * 
     * Optional attributes are:
     * 
     * <ul>
     * <li>groupId - default is the same as the current project</li>
     * <li>version - default is the same as the current project</li>
     * </ul>
     * 
     * @parameter
     */
    private List<ChangelogArtifact> changelogArtifacts;

    /**
     * Contains the full list of projects in the reactor.
     *
     * @parameter expression="${reactorProjects}"
     * @required
     * @readonly
     */
    protected List<MavenProject> reactorProjects;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {

            // Changelog changelog = new Changelog(changelogPath);
            //
            // String extractedSections =
            // changelog.extractSectionForVersion(createVersionMatcher());
            //
            // FileUtils.writeStringToFile(outputPath, extractedSections);
            //

            // Create if output doesnt exist.
            extractedChangelogOutputFile.getParentFile().mkdirs();
            extractedChangelogOutputFile.createNewFile();
            
            if(combinedOutputFile.exists()) {
                combinedOutputFile.delete();
            }
            
            CombiningChangelogWriter combiner = new CombiningChangelogWriter(new Date(), mavenProject.getVersion());
            
            combiner.addChangelog(mavenProject.getGroupId() + "." + mavenProject.getArtifactId(), extractedChangelogOutputFile);
            
            if (changelogArtifacts != null)
            {
                    for (ChangelogArtifact changelogArtifact : changelogArtifacts)
                    {
                        changelogArtifact.setDefaults(mavenProject);
                        Artifact a = getArtifact(changelogArtifact);
                        
                        combiner.addChangelog(a.getGroupId() + "." + a.getArtifactId(), a.getFile());
                    }
            }
            
            combiner.writeTo(combinedOutputFile);

            mavenProjectHelper.attachArtifact(mavenProject, "txt", "changelog",
                    extractedChangelogOutputFile);

        } catch (RuntimeException exc)
        {
            throw exc;
        } catch (Exception exc)
        {
            throw new MojoExecutionException(exc.getMessage(), exc);
        }
    }

    /**
     * Returns <code>true</code> if the artifact has a file.
     * @param artifact the artifact (may be null)
     * @return <code>true</code> if and only if the artifact is non-null and has a file.
     */
    private static boolean hasFile( Artifact artifact )
    {
        return artifact != null && artifact.getFile() != null && artifact.getFile().isFile();
    }
    
    /**
     * Null-safe compare of two artifacts based on groupId, artifactId, version, type and classifier.
     * @param a the first artifact.
     * @param b the second artifact.
     * @return <code>true</code> if and only if the two artifacts have the same groupId, artifactId, version,
     * type and classifier.
     */
    private static boolean equals( Artifact a, Artifact b )
    {
        return a == b
            || !( a == null || b == null )
            && StringUtils.equals( a.getGroupId(), b.getGroupId() )
            && StringUtils.equals( a.getArtifactId(), b.getArtifactId() )
            && StringUtils.equals( a.getVersion(), b.getVersion() )
            && StringUtils.equals( a.getType(), b.getType() )
            && StringUtils.equals( a.getClassifier(), b.getClassifier() );
    }
    
    /**
     * Resolves the Artifact from the remote repository if necessary. If no version is specified, it will be retrieved
     * from the dependency list or from the DependencyManagement section of the pom.
     * 
     * @param artifactItem containing information about artifact from plugin configuration.
     * @return Artifact object representing the specified file.
     * @throws MojoExecutionException with a message if the version can't be found in DependencyManagement.
     */
    protected Artifact getArtifact( ChangelogArtifact artifactItem )
        throws MojoExecutionException
    {
        Artifact artifact;

        // Map managedVersions = createManagedVersionMap( factory,
        // project.getId(), project.getDependencyManagement() );
        VersionRange vr;
        try
        {
            vr = VersionRange.createFromVersionSpec( artifactItem.getVersion() );
        }
        catch ( InvalidVersionSpecificationException e1 )
        {
            e1.printStackTrace();
            vr = VersionRange.createFromVersion( artifactItem.getVersion() );
        }

        if ( StringUtils.isEmpty( artifactItem.getClassifier() ) )
        {
            artifact =
                factory.createDependencyArtifact( artifactItem.getGroupId(), artifactItem.getArtifactId(), vr,
                                                  artifactItem.getType(), null, Artifact.SCOPE_COMPILE );
        }
        else
        {
            artifact =
                factory.createDependencyArtifact( artifactItem.getGroupId(), artifactItem.getArtifactId(), vr,
                                                  artifactItem.getType(), artifactItem.getClassifier(),
                                                  Artifact.SCOPE_COMPILE );
        }

        // Maven 3 will search the reactor for the artifact but Maven 2 does not
        // to keep consistent behaviour, we search the reactor ourselves.
        Artifact result = getArtifactFomReactor( artifact );
        if ( result != null )
        {
            return result;
        }
        try
        {
            resolver.resolve( artifact, remoteRepos, local );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new MojoExecutionException( "Unable to resolve artifact.", e );
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new MojoExecutionException( "Unable to find artifact.", e );
        }

        return artifact;
    }

    
    /**
     * Checks to see if the specified artifact is available from the reactor.
     * @param artifact The artifact we are looking for.
     * @return The resolved artifact that is the same as the one we were looking for or <code>null</code> if one could
     * not be found.
     */
    @SuppressWarnings("unchecked")
    private Artifact getArtifactFomReactor( Artifact artifact )
    {
        // check project dependencies first off
        for ( Artifact a : (Set<Artifact>) mavenProject.getArtifacts() )
        {
            if ( equals( artifact, a ) && hasFile( a ) )
            {
                return a;
            }
        }
        // check reactor projects
        for ( MavenProject p : reactorProjects == null ? Collections.<MavenProject>emptyList() : reactorProjects )
        {
            // check the main artifact
            if ( equals( artifact, p.getArtifact() ) && hasFile( p.getArtifact() ) )
            {
                return p.getArtifact();
            }
            // check any side artifacts
            for ( Artifact a : (List<Artifact>) p.getAttachedArtifacts() )
            {
                if ( equals( artifact, a ) && hasFile( a ) )
                {
                    return a;
                }
            }
        }
        // not available
        return null;
    }
}
