package org.neo4j.build.plugins.changes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 *
 * @goal extract-changelog
 * @phase generate-resources
 *
 */
public class ChangeLogExtractorMojo extends AbstractMojo {

    private static final CharSequence BASEVERSION_TEMPLATE = "$VERSION";
    
    /**
     * Location of the changelog
     *
     * @parameter default-value="${project.basedir}/CHANGES.txt"
     */
    private File changelogPath;
    
    /**
     * Location of the outputted changelog
     *
     * @parameter default-value="${project.build.outputDirectory}/CHANGES.txt"
     */
    private File extractedChangelogOutputFile;
    
    /**
     * Version to extract from the changelog and deploy with this project.
     *
     * @parameter default-value="${project.version}"
     */
    private String projectVersion;
    
    /**
     * List of regexes, version numbers in CHANGELOG headlines that match
     * any of these regexes will be included in the deployed changelog.
     * 
     * By default, this is populated with a matcher for the current project
     * version, with the "-SNAPSHOT" removed, if there was one.
     * 
     * You can expand this to include things like entries for milestone releases
     * or release candidates. You can use the $VERSION template to inject
     * a regex for the current project version. For example:
     * 
     * <ul>
     *   <li>$VERSION</li>
     *   <li>$VERSION\.M\d*</li>
     *   <li>$VERSION\.RC\d*</li>
     * </ul>
     *
     * @parameter
     */
    private List<String> includeVersions;
    
    /**
     * List of strings. Any entry in a change log
     * that contains any of the listed lines will be
     * ignored. 
     *
     * @parameter
     */
    private List<String> excludeLinesContaining;
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {

            Changelog changelog = new Changelog(changelogPath);
            
            List<String> extractedSections = changelog.extractSectionForVersion(createVersionMatcher(), createLineEvaluator());
            
            FileUtils.writeLines(extractedChangelogOutputFile, extractedSections);
           
        } catch (RuntimeException exc) {
            throw exc;
        } catch (Exception exc) {
            throw new MojoExecutionException(exc.getMessage(), exc);
        }
    }

    private VersionMatcher createVersionMatcher()
    {
        return new VersionMatcher(createVersionRegexes());
    }
    
    private LineEvaluator createLineEvaluator()
    {
        LineEvaluator evaluator = new LineEvaluator();
        if(excludeLinesContaining != null) {
            evaluator.excludeLinesContaining(excludeLinesContaining.toArray(new String[excludeLinesContaining.size()]));
        }
        return evaluator;
    }

    private String [] createVersionRegexes()
    {
        String baseVersion = projectVersion.replace(".", "\\.").replace("-SNAPSHOT","");
        if(includeVersions == null) {
            includeVersions = new ArrayList<String>();
            includeVersions.add(baseVersion);
        }
        String [] regexes = new String[includeVersions.size()];
        for(int i=0;i<includeVersions.size();i++) {
            regexes[i] = includeVersions.get(i).replace(BASEVERSION_TEMPLATE, baseVersion);
        }
        return regexes;
    }
}
