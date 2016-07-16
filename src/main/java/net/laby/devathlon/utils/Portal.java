package net.laby.devathlon.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

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
    private BlockFace blockFace;

    public Portal( PortalType portalType, Block firstBlock, Block secondBlock ) {
        this.portalType = portalType;
        this.firstBlock = firstBlock;
        this.secondBlock = secondBlock;
    }

    public Portal( BlockFace blockFace, Block firstBlock, Block secondBlock ) {
        this( PortalType.WALL, firstBlock, secondBlock );

        this.blockFace = blockFace;
    }

    public boolean isInPortal( Player player ) {
        Location loc = player.getLocation();

        switch ( portalType ) {
            case FLOOR:
                return loc.getBlockY() == (firstBlock.getY() + 1) && ((loc.getBlockX() == firstBlock.getLocation().getBlockX() && loc.getBlockZ() == firstBlock.getLocation().getBlockZ())
                        || (loc.getBlockX() == secondBlock.getLocation().getBlockX() && loc.getBlockZ() == secondBlock.getLocation().getBlockZ()));
            case WALL:
                boolean onXAxis = (loc.getBlockX() == firstBlock.getLocation().getBlockX() &&
                        ((loc.getBlockZ() - 1) == firstBlock.getLocation().getBlockZ() || (loc.getBlockZ() + 1) == firstBlock.getLocation().getBlockZ()));
                boolean onZAxis = (loc.getBlockZ() == firstBlock.getLocation().getBlockZ() &&
                        ((loc.getBlockX() - 1) == firstBlock.getLocation().getBlockX() || (loc.getBlockX() + 1) == firstBlock.getLocation().getBlockX()));
                boolean onY = loc.getBlockY() == firstBlock.getLocation().getBlockY() || loc.getBlockY() == secondBlock.getLocation().getBlockY();

                return onXAxis && onZAxis && onY;
        }

        return false;
    }

    public void teleport( Player player ) {
        switch ( portalType ) {
            case FLOOR:
                player.teleport( firstBlock.getLocation().clone().add( 0, 3, 0 ) );
                player.setVelocity( new Vector( 0, 1, 0 ) );
                break;
            case WALL:
                player.teleport( firstBlock.getRelative( getBlockFace(), 5 ).getLocation() );
                break;
        }
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

    public BlockFace getBlockFace() {
        return blockFace;
    }

    public static enum PortalType {
        FLOOR, WALL;
    }

    public static BlockFace getBlockFaceFromTo( Location from, Location to ) {
        int x = from.getBlockX() < to.getBlockX() ? -1 : 1;
        int z = from.getBlockZ() < to.getBlockZ() ? -1 : 1;

        if ( from.getBlockX() == to.getBlockX() ) {
            x = 0;
        }

        if ( from.getBlockZ() == to.getBlockZ() ) {
            z = 0;
        }

        for ( BlockFace blockFaces : BlockFace.values() ) {
            if ( blockFaces.getModY() != 0 )
                continue;

            if ( blockFaces.getModX() != x )
                continue;

            if ( blockFaces.getModZ() != z )
                continue;

            Bukkit.broadcastMessage( blockFaces.getOppositeFace().name() );
            return blockFaces.getOppositeFace();
        }

        return BlockFace.NORTH;
    }

}
