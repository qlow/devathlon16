package net.laby.devathlon.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Class created by qlow | Jan
 * Represents a portal
 */
public class Portal {

    private PortalType portalType;
    private Block firstBlock;
    private Block secondBlock;

    public Portal( PortalType portalType, Block firstBlock, Block secondBlock ) {
        this.portalType = portalType;
        this.firstBlock = firstBlock;
        this.secondBlock = secondBlock;
    }

    public boolean isInPortal( Player player ) {
        Location loc = player.getLocation();

        switch ( portalType ) {
            case FLOOR:
                return loc.getBlockY() == (firstBlock.getY() + 1) && ((loc.getBlockX() == firstBlock.getLocation().getBlockX() && loc.getBlockZ() == firstBlock.getLocation().getBlockZ())
                        || (loc.getBlockX() == secondBlock.getLocation().getBlockX() && loc.getBlockZ() == secondBlock.getLocation().getBlockZ()));
            case WALL:
                return (loc.distance( firstBlock.getLocation() ) <= 1) || (loc.distance( secondBlock.getLocation() ) <= 1);
        }

        return false;
    }

    public List<Block> getPortalBlocks() {
        return Arrays.asList( firstBlock, secondBlock );
    }

    public PortalType getPortalType() {
        return portalType;
    }

    public Block getFirstBlock() {
        return firstBlock;
    }

    public Block getSecondBlock() {
        return secondBlock;
    }

    public static enum PortalType {
        FLOOR, WALL;
    }
}
