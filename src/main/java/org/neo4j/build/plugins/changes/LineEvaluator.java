package org.neo4j.build.plugins.changes;

public class LineEvaluator {

    public static final LineEvaluator ALL = new LineEvaluator(){
        
    };
    
    private String[] excludes;

    public void excludeLinesContaining(String ... strings)
    {
        this.excludes = strings;
    }

    public boolean include(String prevLine)
    {
        if(excludes != null) {
            for(String exclude : excludes) {
                if(prevLine.contains(exclude)) {
                    return false;
                }
            }
        }
        return true;
    }

}
