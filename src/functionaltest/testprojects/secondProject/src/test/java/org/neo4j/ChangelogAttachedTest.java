package org.neo4j;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

public class ChangelogAttachedTest  {

    private static final String OUTPUT_CHANGELOG = "CHANGES.txt";
    private static final String EXPECTED_CHANGELOG_CONTENT = 
            "1.0 (2012-01-19)\n"+
            "----------------\n"+
            "o Fixes issues #173, #118, #138, #103\n\n";
    
    private File output = new File("target/classes");

    @Test
    @Ignore
    public void outputFilesShouldExist() throws Exception 
    {
        assertExists(new File(output, OUTPUT_CHANGELOG));
    }
    
    @Test
    @Ignore
    public void outputFileShouldContainExpectedContent() throws Exception 
    {
        File changelog = new File(output, OUTPUT_CHANGELOG);
        assertContains(changelog, EXPECTED_CHANGELOG_CONTENT);
    }

    private static void assertExists(File file) throws Exception 
    {
        assertThat( file.exists(), is( true ) );
    }
    
    private static void assertContains(File file, String substr) throws IOException 
    {
        assertThat(FileUtils.readFileToString( file ), (is( substr )));
    }
}
