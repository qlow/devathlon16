package net.laby.game;

import lombok.Getter;
import org.bukkit.Location;

import java.util.Random;

/**
 * Class created by qlow | Jan
 */
public class GameRegion {

    private final static Random RANDOM = new Random();

    @Getter
    private int minX, maxX, minZ, maxZ;

    @Getter
    private boolean valid;

    public GameRegion( Location firstLocation, Location secondLocation ) {
        if ( firstLocation == null || secondLocation == null )
            return;

        minX = Math.min( firstLocation.getBlockX(), secondLocation.getBlockX() );
        maxX = Math.max( firstLocation.getBlockX(), secondLocation.getBlockX() );

        minZ = Math.min( firstLocation.getBlockZ(), secondLocation.getBlockZ() );
        maxZ = Math.max( firstLocation.getBlockZ(), secondLocation.getBlockZ() );

        valid = true;
    }

    public int getRandomX() {
        return randomBetween( minX, maxX );
    }

    public int getRandomZ() {
        return randomBetween( Math.abs(maxX), Math.abs(maxZ) );
    }

    private int randomBetween( int min, int max ) {
        boolean reversed = false;

        if(max < min) {
            final int tmpMin = min;
            min = max;
            max = tmpMin;

            reversed = true;
        }

        return RANDOM.nextInt( max - min ) + (min * (reversed ? -1 : 1));
    }

}
