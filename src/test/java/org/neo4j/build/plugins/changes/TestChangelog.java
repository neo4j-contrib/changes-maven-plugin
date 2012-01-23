package org.neo4j.build.plugins.changes;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.neo4j.build.plugins.changes.Changelog;
import org.neo4j.build.plugins.changes.VersionMatcher;


public class TestChangelog {

    private static final String EXPECTED_1_6_SECTION = 
        "1.6 (2012-01-19)\n"+
        "----------------\n"+
        "o Fixes issues #173, #118, #138, #103\n\n";
    
    private static final String EXPECTED_1_6_M01_SECTION = 
        "1.6.M01 (2011-11-24)\n"+
        "--------------------\n"+
        "o Cypher console in webadmin is now a Neo4j Shell console with the exact same Cypher support included.\n" +
        "o More documentation and examples.\n\n";
    
    private static final String EXPECTED_0_1_SECTION = 
        "0.1 (2010-11-18)\n"+
        "------------------------\n"+
        "Initial release.\n\n";

    @Test
    public void testExtractSectionForVersion() throws Exception
    {
        InputStream changesInput = this.getClass().getResourceAsStream("/changelogs/BASIC.txt");
        
        Changelog changelog = new Changelog(changesInput);

        // Extract the first entry
        assertThat(StringUtils.join(changelog.extractSectionForVersion("1\\.6"),"\n"), is(EXPECTED_1_6_SECTION));
        
        // Extract an entry in the middle
        assertThat(StringUtils.join(changelog.extractSectionForVersion("1\\.6\\.M01"),"\n"), is(EXPECTED_1_6_M01_SECTION));
        
        // Extract the last entry
        assertThat(StringUtils.join(changelog.extractSectionForVersion("0\\.1"),"\n"), is(EXPECTED_0_1_SECTION));
        
        // Extract all 1.6 entries, including milestones
        VersionMatcher versionMatcher = new VersionMatcher("1\\.6","1\\.6\\.M.*");
        assertThat(StringUtils.join(changelog.extractSectionForVersion(versionMatcher),"\n"), is(EXPECTED_1_6_SECTION + EXPECTED_1_6_M01_SECTION));
        
    }

}
