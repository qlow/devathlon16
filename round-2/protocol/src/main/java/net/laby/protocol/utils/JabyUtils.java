package net.laby.protocol.utils;

/**
 * Class created by qlow | Jan
 */
public class JabyUtils {

    public static String convertToMd5( String convert ) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance( "MD5" );

            byte[] array = md.digest( convert.getBytes() );
            StringBuffer sb = new StringBuffer();

            for ( int i = 0; i < array.length; ++i ) {
                sb.append( Integer.toHexString( (array[i] & 0xFF) | 0x100 ).substring( 1, 3 ) );
            }

            return sb.toString();
        } catch ( java.security.NoSuchAlgorithmException e ) {
        }

        return null;
    }

}
