package net.laby.utils;

import javax.swing.*;
import java.awt.*;
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

    public static void centreWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }

    public static void showDialog(JFrame frame, String title, String text, Icon icon ) {
        JOptionPane.showMessageDialog( frame, text, title, JOptionPane.INFORMATION_MESSAGE, icon );
    }
}
