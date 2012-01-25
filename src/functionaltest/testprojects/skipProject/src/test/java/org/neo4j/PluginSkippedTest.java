package org.neo4j;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class PluginSkippedTest  {

    private File extracted = new File("target/classes/CHANGES.txt");
    private File attached = new File("target/CHANGES.txt");

    @Test
    public void outputFilesShouldExist() throws Exception 
    {
        assertDoesntExist(attached);
        assertDoesntExist(extracted);
    }

    private static void assertDoesntExist(File file) throws Exception 
    {
        assertThat( file.exists(), is( false ) );
    }
    
    private static void assertContains(File file, String substr) throws IOException 
    {
        assertThat(FileUtils.readFileToString( file ), (is( substr )));
    }
}
