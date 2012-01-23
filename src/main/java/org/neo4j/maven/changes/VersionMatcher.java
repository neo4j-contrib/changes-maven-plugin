package org.neo4j.maven.changes;

public class VersionMatcher {

    public static final VersionMatcher ANY = new VersionMatcher() {
        @Override
        public boolean test(String version) {
            return true;
        }
    };
    
    private String[] patterns;

    public VersionMatcher(String ... patterns)
    {
        this.patterns = patterns;
    }

    public boolean test(String version)
    {
        for(String pattern : patterns ) {
            if(version.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

}
