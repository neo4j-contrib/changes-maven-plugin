package org.neo4j.build.plugins.changes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Changelog {
    
    private List<String> logLines = new ArrayList<String>();

    public Changelog(File changelog) throws IOException
    {
        this(new FileInputStream(changelog));
    }

    protected Changelog(InputStream changesInput) throws IOException
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    changesInput));
            
            String line;
            while ((line = br.readLine()) != null)
            {
                logLines.add(line);
            }
            
            // Add an extra blank line, makes the parser easier to write
            logLines.add("");
        } finally {
            if(changesInput != null) {
                changesInput.close();
            }
        }
    }

    public List<String> extractSectionForVersion(String version)
    {
        return extractSectionForVersion(new VersionMatcher(version), LineEvaluator.ALL);
    }

    public List<String> extractSectionForVersion(VersionMatcher versionMatcher, LineEvaluator lineEvaluator)
    {
        ChangelogExtractor extractor = new ChangelogExtractor(logLines, versionMatcher, lineEvaluator);
        return extractor.runExtraction(true);
    }

    public List<String> extractAllEntriesWithoutHeadlines(VersionMatcher versionMatcher, LineEvaluator lineEvaluator)
    {
        ChangelogExtractor extractor = new ChangelogExtractor(logLines, versionMatcher, lineEvaluator);
        return extractor.runExtraction(false);
    }
}
