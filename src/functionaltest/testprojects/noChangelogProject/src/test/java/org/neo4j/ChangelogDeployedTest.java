package org.neo4j;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ChangelogDeployedTest  {
    
    private File output = new File("target/CHANGES.txt");

    @Test
    public void outputFilesShouldExist() throws Exception 
    {
        assertExists(output);
    }
    
    @Test
    public void outputFileShouldContainExpectedContent() throws Exception 
    {
        assertContains(output, expectedChangelogContent());
    }
    
    private String expectedChangelogContent() {
        Date date = new Date();
        
        @SuppressWarnings("deprecation")
        String dateStr = (date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + date.getDate();
        String headline = "1.0 (" + dateStr + ")";
        
        StringBuilder divide = new StringBuilder();
        for(int i=0;i<headline.length();i++) {
            divide.append("-");
        }
        
        return  headline + "\n"+
                divide.toString() + "\n\n"+
                "it.sandbox.singleChangelogTest:\n"+
                "No changes.\n";
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
