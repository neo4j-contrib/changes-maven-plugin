package org.neo4j.maven.changes;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

public class ChangelogArtifact {
    /**
     * Group Id of Artifact
     * 
     * @parameter
     * @required
     */
    private String groupId;

    /**
     * Name of Artifact
     * 
     * @parameter
     * @required
     */
    private String artifactId;

    /**
     * Version of Artifact
     * 
     * @parameter
     */
    private String version = null;

    /**
     * Type of Artifact (War,Jar,etc)
     * 
     * @parameter
     * @required
     */
    private String type = "txt";

    /**
     * Classifier for Artifact (tests,sources,etc)
     * 
     * @parameter
     */
    private String classifier = "changelog";

    /**
     * Artifact Item
     */
    private Artifact artifact;

    public ChangelogArtifact()
    {
        // default constructor
    }

    public ChangelogArtifact(Artifact artifact)
    {
        this.setArtifact(artifact);
        this.setArtifactId(artifact.getArtifactId());
        this.setClassifier(artifact.getClassifier());
        this.setGroupId(artifact.getGroupId());
        this.setType(artifact.getType());
        this.setVersion(artifact.getVersion());
    }

    private String filterEmptyString(String in)
    {
        if ("".equals(in))
        {
            return null;
        }
        return in;
    }

    /**
     * @return Returns the artifactId.
     */
    public String getArtifactId()
    {
        return artifactId;
    }

    /**
     * @param artifactId
     *            The artifactId to set.
     */
    public void setArtifactId(String artifact)
    {
        this.artifactId = filterEmptyString(artifact);
    }

    /**
     * @return Returns the groupId.
     */
    public String getGroupId()
    {
        return groupId;
    }

    /**
     * @param groupId
     *            The groupId to set.
     */
    public void setGroupId(String groupId)
    {
        this.groupId = filterEmptyString(groupId);
    }

    /**
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(String type)
    {
        this.type = filterEmptyString(type);
    }

    /**
     * @return Returns the version.
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @param version
     *            The version to set.
     */
    public void setVersion(String version)
    {
        this.version = filterEmptyString(version);
    }

    /**
     * @return Classifier.
     */
    public String getClassifier()
    {
        return classifier;
    }

    /**
     * @param classifier
     *            Classifier.
     */
    public void setClassifier(String classifier)
    {
        this.classifier = filterEmptyString(classifier);
    }

    public String toString()
    {
        if (this.classifier == null)
        {
            return groupId + ":" + artifactId + ":"
                    + StringUtils.defaultString(version, "?") + ":" + type;
        } else
        {
            return groupId + ":" + artifactId + ":" + classifier + ":"
                    + StringUtils.defaultString(version, "?") + ":" + type;
        }
    }

    /**
     * @return Returns the artifact.
     */
    public Artifact getArtifact()
    {
        return this.artifact;
    }

    /**
     * @param artifact
     *            The artifact to set.
     */
    public void setArtifact(Artifact artifact)
    {
        this.artifact = artifact;
    }

    public void setDefaults(MavenProject mavenProject)
    {
        if(groupId == null) {
            groupId = mavenProject.getGroupId();
        }
        
        if(version == null) {
            version = mavenProject.getVersion();
        }
    }
}
