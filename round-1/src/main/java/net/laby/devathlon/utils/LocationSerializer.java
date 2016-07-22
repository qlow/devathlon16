package net.laby.devathlon.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Class created by qlow | Jan
 */
public class LocationSerializer {

    /**
     * Deserializes a string to a location
     *
     * @param string serialized string
     * @return deserialized location
     */
    public static Location fromString( String string ) {
        String[] splittedString = string.split( ";" );

        double x = Double.parseDouble( splittedString[0] );
        double y = Double.parseDouble( splittedString[1] );
        double z = Double.parseDouble( splittedString[2] );

        float yaw = Float.parseFloat( splittedString[3] );
        float pitch = Float.parseFloat( splittedString[4] );

        return new Location( Bukkit.getWorld( splittedString[5] ), x, y, z, yaw, pitch );
    }

    /**
     * Serializes a location to a string
     *
     * @param location not serialized location
     * @return serialized location in string
     */
    public static String toString( Location location ) {
        return location.getX()
                + ";" + location.getY()
                + ";" + location.getZ()
                + ";" + location.getYaw()
                + ";" + location.getPitch()
                + ";" + location.getWorld().getName();
    }

}
