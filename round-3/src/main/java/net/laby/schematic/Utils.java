package net.laby.schematic;

/**
 * Class created by LabyStudio
 */
public class Utils {
    public static float dataToYaw(byte data) {
        switch(data) {
            case 1:
                return 0;
            case 2:
                return -90;
            case 3:
                return 90;
        }
        return 180;
    }

}
