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
 * Represents a portal (a portal has 2 blocks)
 */
public class Portal {

    private PortalType portalType;
    private Block firstBlock;
    private Block secondBlock;
    private BlockFace blockFace;

    /**
     * @param portalType  portal's type
     * @param firstBlock  portal's first old block
     * @param secondBlock portal's second old block
     */
    public Portal( PortalType portalType, Block firstBlock, Block secondBlock ) {
        this.portalType = portalType;
        this.firstBlock = firstBlock;
        this.secondBlock = secondBlock;
    }

    /**
     * @param blockFace   portal's teleport-blockface
     * @param firstBlock  portal's first old block
     * @param secondBlock portal's second old block
     */
    public Portal( BlockFace blockFace, Block firstBlock, Block secondBlock ) {
        this( PortalType.WALL, firstBlock, secondBlock );

        this.blockFace = blockFace;
    }

    /**
     * State whether the player is in the portal (for teleporting)
     *
     * @param player player the state should get requested from
     * @return true if the player is in the portal
     */
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

        // Can't happen
        return false;
    }

    /**
     * Teleporting player through the portal
     *
     * @param player player should be teleported
     */
    public void teleport( Player player ) {
        switch ( portalType ) {
            case FLOOR:
                // Teleporting
                player.teleport( firstBlock.getLocation().clone().add( 0, 3, 0 ) );

                // Setting velocity
                player.setVelocity( new Vector( 0, 1, 0 ) );
                break;
            case WALL:
                // Teleporting
                player.teleport( firstBlock.getRelative( getBlockFace(), 2 ).getLocation() );

                // Setting velocity
                player.setVelocity( new Vector( getBlockFace().getModX() * 2, 0, getBlockFace().getModZ() * 2 ) );
                break;
        }
    }

    /**
     * List of all portal-blocks
     * @return new list with {@link Portal#firstBlock} and {@link Portal#secondBlock}
     */
    public List<Block> getPortalBlocks() {
        return Arrays.asList( firstBlock, secondBlock );
    }

    public Block getFirstBlock() {
        return firstBlock;
    }

    public Block getSecondBlock() {
        return secondBlock;
    }

    /**
     * Type of portal
     * @return portal's type
     */
    public PortalType getPortalType() {
        return portalType;
    }

    /**
     * Calculates the blockface/direction from
     * @param from
     * @param to
     * @return
     */
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

        // Shouldn't happen...
        return BlockFace.NORTH;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }

    public static enum PortalType {
        FLOOR, WALL;
    }

}
