package org.neo4j;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

public class ChangelogAttachedTest  {

    private static final String OUTPUT_CHANGELOG = "CHANGES.txt";
    
    private File output = new File("target/" + OUTPUT_CHANGELOG);

    @Test
    public void outputFilesShouldExist() throws Exception 
    {
        assertExists(output);
    }
    
    @Test
    @Ignore
    public void outputFileShouldContainExpectedContent() throws Exception 
    {
        assertContains(output, "it.sandbox.firstProject:");
        assertContains(output, "it.sandbox.secondProject:");
        assertContains(output, "it.sandbox.combinedProject:");
    }

    private static void assertExists(File file) throws Exception 
    {
        assertThat( file.exists(), is( true ) );
    }
    
    private static void assertContains(File file, String substr) throws IOException 
    {
        assertThat(FileUtils.readFileToString( file ), (containsString( substr )));
    }
}
