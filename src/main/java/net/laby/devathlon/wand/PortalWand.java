package net.laby.devathlon.wand;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import net.laby.devathlon.Devathlon;
import net.laby.devathlon.utils.Portal;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Class created by qlow | Jan
 */
public class PortalWand extends Wand {

    private Player player;

    private Portal firstPortal;
    private Portal secondPortal;
    private int tickCounter = 0;

    public PortalWand( Player player ) {
        super( player, Material.STICK, 0, "§9Portal§7-§cStab", "", "§7-> Mit Rechts & Linksklick kannst du", "   §7Portale setzen, durch die man", "   §7laufen kann.", "" );

        this.player = player;
    }

    public PortalWand() {
        this( null );
    }

    @Override
    public void onDisable() {
        sendDefaultBlocks( true );
        sendDefaultBlocks( false );
    }

    @Override
    public void onRightClick() {
        // Making second-portal
        makePortal( false );
    }

    @Override
    public void onLeftClick() {
        // Making first-portal
        makePortal( true );
    }

    @Override
    public void onTick() {
        if ( firstPortal == null || secondPortal == null )
            return;

        // Checking every 5 ticks whether the player is in the portal
        if ( tickCounter++ % 5 != 0 ) {
            return;
        }

        if ( firstPortal.isInPortal( player ) ) {
            // Teleporting player to second portal if he is in the first portal
            secondPortal.teleport( player );
        } else if ( secondPortal.isInPortal( player ) ) {
            // Teleporting player to first portal if he is in the second portal
            firstPortal.teleport( player );
        }

        tickCounter = 0;
    }

    /**
     * Makes the portal
     * @param left true if it is the first portal
     */
    private void makePortal( boolean left ) {
        // Getting line of sight (up to 50 blocks)
        List<Block> blocks = player.getLineOfSight( ( HashSet<Byte> ) null, 50 );
        Block target = null;

        for ( Block block : blocks ) {
            if ( block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER ) {
                continue;
            }

            // Setting target-block
            target = block;
            break;
        }

        // Returning if there is no target
        if ( target == null )
            return;

        // Last block before the target
        Location lastBlock = player.getLocation();

        // Iterating through the line of sight
        for ( Block block : blocks ) {
            Location location = block.getLocation();

            if ( block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER ) {
                // Playing effect if it is air
                player.playEffect( location, Effect.MOBSPAWNER_FLAMES, 0 );
                lastBlock = block.getLocation();
                continue;
            }

            // Breaking if the target is reached
            if ( block == target )
                break;
        }

        // List of blocks that will be replaced
        List<Location> portalBlocks = new ArrayList<>();

        // Instance of portal
        Portal portal = null;

        if ( target.getLocation().clone().add( 0, 1, 0 ).getBlock().getType() != Material.AIR ) {
            // Adding blocks to portal-blocks-list
            portalBlocks.add( target.getLocation() );
            portalBlocks.add( target.getLocation().add( 0, 1, 0 ) );

            // Initializing portal
            portal = new Portal( Portal.getBlockFaceFromTo( target.getLocation(), lastBlock ), portalBlocks.get( 0 ).getBlock(), portalBlocks.get( 1 ).getBlock() );
        } else {
            // Getting valid block near the target-block
            Location secondBlock = getSecondBlockLocationOnFloor( target.getLocation() );

            // Returning if there is no valid block near the target
            if ( secondBlock == null )
                return;

            // Adding blocks to portal-blocks-list
            portalBlocks.add( target.getLocation() );
            portalBlocks.add( secondBlock );

            // Initializing portal
            portal = new Portal( Portal.PortalType.FLOOR, portalBlocks.get( 0 ).getBlock(), portalBlocks.get( 1 ).getBlock() );
        }

        // Sending default-blocks of the selected portal
        sendDefaultBlocks( left );

        // Setting private portal-variable
        if ( left ) {
            firstPortal = portal;
        } else {
            secondPortal = portal;
        }


        new BukkitRunnable(){
            public void run( ) {
                // Iterating through the portal-blocks
                for ( Location portalBlock : portalBlocks ) {
                    byte data = 6;

                    if ( left ) {
                        data = 11;
                    }

                    // Creating block-change-packet
                    PacketContainer packetContainer = ProtocolLibrary.getProtocolManager().createPacket( PacketType.Play.Server.BLOCK_CHANGE );

                    // Writing data into the packet
                    packetContainer.getBlockPositionModifier().write( 0, new BlockPosition( portalBlock.getBlockX(), portalBlock.getBlockY(), portalBlock.getBlockZ() ) );
                    packetContainer.getBlockData().write( 0, WrappedBlockData.createData( Material.STAINED_CLAY, data ) );

                    // Sending packet to player
                    try {
                        ProtocolLibrary.getProtocolManager().sendServerPacket( player, packetContainer );
                    } catch ( InvocationTargetException e ) {
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskLater( Devathlon.getInstance(), 1L );
    }

    /**
     * Sends the blockchange-packets so the player can see the old blocks again
     *
     * @param left
     */
    private void sendDefaultBlocks( boolean left ) {
        // List of blocks that should be reset
        List<Block> reset = new ArrayList<>();

        // Adding to reset-list
        if ( left && firstPortal != null ) reset.addAll( firstPortal.getPortalBlocks() );
        if ( !left && secondPortal != null ) reset.addAll( secondPortal.getPortalBlocks() );

        // Iterating through reset-list
        for ( Block resetBlocks : reset ) {
            Location loc = resetBlocks.getLocation();

            // Creating PacketContainer for Block-Change
            PacketContainer packetContainer = ProtocolLibrary.getProtocolManager().createPacket( PacketType.Play.Server.BLOCK_CHANGE );

            // Writing data to the packet
            packetContainer.getBlockPositionModifier().write( 0, new BlockPosition( loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() ) );
            packetContainer.getBlockData().write( 0, WrappedBlockData.createData( resetBlocks.getType(), resetBlocks.getData() ) );

            // Sending packet
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket( player, packetContainer );
            } catch ( InvocationTargetException e ) {
                e.printStackTrace();
            }
        }

        // Clearing portal
        if ( left ) firstPortal = null;
        if ( !left ) secondPortal = null;
    }

    /**
     * A valid portal-location near the given location
     *
     * @param firstBlock given location
     * @return valid location near the given location
     */
    public Location getSecondBlockLocationOnFloor( Location firstBlock ) {
        // Array of all blocks near the given block
        Location[] nearbyBlocks = new Location[]{
                firstBlock.clone().add( 1, 0, 0 ),
                firstBlock.clone().add( 0, 0, 1 ),
                firstBlock.clone().subtract( 1, 0, 0 ),
                firstBlock.clone().subtract( 0, 0, 1 )
        };

        // Iterating through all blocks near the given block
        for ( Location nearbyBlock : nearbyBlocks ) {
            // Checking if there is something above the current block
            if ( nearbyBlock.clone().add( 0, 1, 0 ).getBlock().getType() != Material.AIR )
                continue;

            // Returning that block if there is space over it
            return nearbyBlock;
        }

        // Returning null if there is no valid block
        return null;
    }

}
