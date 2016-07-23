package net.laby.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Utils {
    public static String readFile( File file ) throws IOException {
        String content = null;
        FileReader reader = null;
        try {
            reader = new FileReader( file );
            char[] chars = new char[ ( int ) file.length() ];
            reader.read( chars );
            content = new String( chars );
            reader.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( reader != null ) {
                reader.close();
            }
        }
        return content;
    }
}
