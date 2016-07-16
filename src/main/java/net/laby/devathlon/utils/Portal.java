package net.laby.devathlon.utils;

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

                return (onXAxis || onZAxis) && onY;
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
                AnglesToLocationCalculator anglesToLocationCalculator =
                        new AnglesToLocationCalculator( firstBlock.getRelative( getBlockFace(), 2 ).getLocation(), firstBlock.getLocation() );

                Location teleportTo = firstBlock.getRelative( getBlockFace(), 2 ).getLocation();
                teleportTo.setYaw( ( float ) anglesToLocationCalculator.getYaw() );
                teleportTo.setPitch( ( float ) anglesToLocationCalculator.getPitch() );

                player.teleport( teleportTo );
                player.setVelocity( new Vector( getBlockFace().getModX() * 2, 0, getBlockFace().getModZ() * 2 ) );
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

            return blockFaces.getOppositeFace();
        }

        return BlockFace.NORTH;
    }

    private static class AnglesToLocationCalculator {

        private double yaw;
        private double pitch;

        public AnglesToLocationCalculator( Location from, Location to ) {
            double dx = to.getX() - from.getX();
            double dy = to.getY() - from.getY();
            double dz = to.getZ() - from.getZ();
            double r = Math.sqrt( dx * dx + dy * dy + dz * dz );
            yaw = -Math.atan2( dx, dz ) / Math.PI * 180;
            if ( yaw < 0 )
                yaw = 360 - yaw;

            pitch = -Math.asin( dy / r ) / Math.PI * 180;
        }

        public double getYaw() {
            return yaw;
        }

        public double getPitch() {
            return pitch;
        }
    }

}
