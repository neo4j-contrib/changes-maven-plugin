package org.neo4j.maven.changes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CombiningChangelogWriter {

    class Pair<FIRST,SECOND> {
        public FIRST first;
        public SECOND second;
        public Pair(FIRST first, SECOND second) {
            this.first = first;
            this.second = second;
        }
    }
    
    private Object version;
    private Date date;
    private List<Pair<String,File>> changelogs = new ArrayList<Pair<String,File>>();

    public CombiningChangelogWriter(Date date,
            String version)
    {
        this.version = version;
        this.date = date;
    }

    public void addChangelog(String title, File changelog)
    {
        changelogs.add(new Pair<String,File>(title, changelog));
    }

    public void writeTo(File combinedOutputFile) throws IOException
    {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(combinedOutputFile, true));
            
            writeTitleTo(out);
            for (Pair<String, File> changelogMeta : changelogs)
            {
                Changelog changelog = new Changelog(changelogMeta.second);
                List<String> lines = changelog.extractAllEntriesWithoutHeadlines();
                
                out.write(changelogMeta.first + ":\n");
                
                for(String line : lines) {
                    out.write(line + "\n");
                }
            }
        } finally {
            if(out != null) {
                out.close();
            }
        }
    }

    private void writeTitleTo(Writer out) throws IOException
    {
        @SuppressWarnings("deprecation")
        String dateStr = (date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + date.getDate();
        String headline = version + " (" + dateStr + ")\n";
        
        out.write(headline);
        for(int i=0;i<headline.length();i++) {
            out.write("-");
        }
        out.write("\n\n");
    }

}
