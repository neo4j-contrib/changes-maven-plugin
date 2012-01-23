package org.neo4j.maven.changes;

import java.util.ArrayList;
import java.util.List;

public class ChangelogSectionExtractor {
    
    private enum ParseMode {SCANNING,EXTRACTING,DONE};
    
    private List<String> logLines;
    private VersionMatcher versionMatcher;

    public ChangelogSectionExtractor(List<String> logLines,
            VersionMatcher versionMatcher)
    {
        this.logLines = logLines;
        this.versionMatcher = versionMatcher;
    }

    public List<String> runExtraction(boolean includeHeadlines)
    {
        ParseMode mode = ParseMode.SCANNING;
        List<String> output = new ArrayList<String>();
        String prevLine = "";
        
        for(String line : logLines) {
            mode = processLine(prevLine, line, output, mode, includeHeadlines);
            prevLine = line;
        }        
        
        if(!output.get(output.size() - 1).equals("")) {
            output.add("");
        }
        
        if(!output.get(output.size() - 2).equals("")) {
            output.add("");
        }
        
        return output;
    }


    private ParseMode processLine(String prevLine, String line, List<String> output,
            ParseMode mode, boolean includeHeadlines)
    {
        switch(mode) {
        
        case SCANNING:
            if(isSectionStart(line) && isVersionHeadline(prevLine, versionMatcher)) {
                mode = ParseMode.EXTRACTING;
                if(includeHeadlines) {
                    output.add(prevLine);
                }
            }
            break;
            
        case EXTRACTING:
            if(isSectionStart(line)) { 
                // Hit new section
                output.add("");
                mode = ParseMode.SCANNING;
                return processLine(prevLine, line, output, mode, includeHeadlines);
            } else if(prevLine.trim().length() > 0) {
                if(includeHeadlines || !isSectionStart(prevLine)) {
                    output.add(prevLine);
                }
            }
            break;
        }
        return mode;
    }

    private boolean isVersionHeadline(String line, VersionMatcher versionMatcher)
    {
        return versionMatcher.test(line.trim().split(" ")[0]);
    }

    private boolean isSectionStart(String line)
    {
        return line.startsWith("----");
    }

}
