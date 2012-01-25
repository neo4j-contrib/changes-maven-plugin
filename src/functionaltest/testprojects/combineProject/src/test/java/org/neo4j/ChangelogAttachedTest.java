package org.neo4j;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

public class ChangelogAttachedTest  {
    
    private static final String EXPECTED_CHANGELOG_CONTENT = 
            "1.0-SNAPSHOT (2012-1-25)\n" +
            "-------------------------\n" +
            "\n" +
            "it.sandbox.combineProject:\n" +
            "o Fixes issues #173, #118, #138, #103\n" +
            "\n" +
            "\n" +
            "it.sandbox.firstProject:\n" +
            "o Fixes issues #173, #118, #138, #103\n" +
            // This line is in the firstProject changelog,
            // but we are expecting it to be excluded here.
            //"o Made some changes [minor]\n" + 
            "\n" +
            "\n" +
            "it.sandbox.secondProject:\n" +
            "o Fixes issues #173, #118, #138, #103\n\n\n";
    
    private File output = new File("target/CHANGES.txt");

    @Test
    public void outputFilesShouldExist() throws Exception 
    {
        assertExists(output);
    }
    
    @Test
    public void outputFileShouldContainExpectedContent() throws Exception 
    {
        assertContains(output, EXPECTED_CHANGELOG_CONTENT);
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
