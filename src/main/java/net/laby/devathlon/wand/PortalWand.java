package net.laby.devathlon.wand;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import net.laby.devathlon.utils.Portal;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

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

    private static int counter = 1337 * 9;

    public PortalWand( Player player ) {
        super( player, Material.STICK, 0, "§9Portal§7-§cStab", "", "§7-> Mit Rechts & Linksklick kannst du", "  §7Portale setzen, durch die", "  §7man laufen kann.", "" );

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
        makePortal( false );
    }

    @Override
    public void onLeftClick() {
        makePortal( true );
    }

    @Override
    public void onTick() {
        if ( firstPortal == null || secondPortal == null )
            return;

        if ( tickCounter++ % 5 != 0 ) {
            return;
        }

        if ( firstPortal.isInPortal( player ) ) {
            secondPortal.teleport( player );
        } else if ( secondPortal.isInPortal( player ) ) {
            firstPortal.teleport( player );
        }

        tickCounter = 0;
    }

    private int tickCounter = 0;

    private void makePortal( boolean left ) {
        List<Block> blocks = player.getLineOfSight( ( HashSet<Byte> ) null, 50 );
        Block target = null;

        for ( Block block : blocks ) {
            if ( block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER ) {
                continue;
            }

            target = block;
            break;
        }

        if ( target == null )
            return;

        Location lastBlock = player.getLocation();

        for ( Block block : blocks ) {
            Location location = block.getLocation();

            if ( block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER ) {
                player.playEffect( location, Effect.MOBSPAWNER_FLAMES, 0 );
                lastBlock = block.getLocation();
                continue;
            }

            if ( block == target )
                break;
        }

        List<Location> portalBlocks = new ArrayList<>();
        Portal portal = null;

        if ( target.getLocation().clone().add( 0, 1, 0 ).getBlock().getType() != Material.AIR ) {
            portalBlocks.add( target.getLocation() );
            portalBlocks.add( target.getLocation().add( 0, 1, 0 ) );

            portal = new Portal( Portal.getBlockFaceFromTo( target.getLocation(), lastBlock ), portalBlocks.get( 0 ).getBlock(), portalBlocks.get( 1 ).getBlock() );
        } else {
            Location secondBlock = getSecondBlockLocationOnFloor( target.getLocation() );

            if ( secondBlock == null )
                return;

            portalBlocks.add( target.getLocation() );
            portalBlocks.add( secondBlock );

            portal = new Portal( Portal.PortalType.FLOOR, portalBlocks.get( 0 ).getBlock(), portalBlocks.get( 1 ).getBlock() );
        }

        sendDefaultBlocks( left );

        if ( left ) {
            firstPortal = portal;
        } else {
            secondPortal = portal;
        }

        for ( Location portalBlock : portalBlocks ) {
            byte data = 6;

            if ( left ) {
                data = 11;
            }

            PacketContainer packetContainer = ProtocolLibrary.getProtocolManager().createPacket( PacketType.Play.Server.BLOCK_CHANGE );

            packetContainer.getBlockPositionModifier().write( 0, new BlockPosition( portalBlock.getBlockX(), portalBlock.getBlockY(), portalBlock.getBlockZ() ) );
            packetContainer.getBlockData().write( 0, WrappedBlockData.createData( Material.STAINED_CLAY, data ) );

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket( player, packetContainer );
            } catch ( InvocationTargetException e ) {
                e.printStackTrace();
            }
        }

    }

    private void sendDefaultBlocks( boolean left ) {
        List<Block> reset = new ArrayList<>();
        if ( left && firstPortal != null ) reset.addAll( firstPortal.getPortalBlocks() );
        if ( !left && secondPortal != null ) reset.addAll( secondPortal.getPortalBlocks() );

        for ( Block resetBlocks : reset ) {
            Location loc = resetBlocks.getLocation();
            PacketContainer packetContainer = ProtocolLibrary.getProtocolManager().createPacket( PacketType.Play.Server.BLOCK_CHANGE );

            packetContainer.getBlockPositionModifier().write( 0, new BlockPosition( loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() ) );
            packetContainer.getBlockData().write( 0, WrappedBlockData.createData( resetBlocks.getType(), resetBlocks.getData() ) );

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket( player, packetContainer );
            } catch ( InvocationTargetException e ) {
                e.printStackTrace();
            }
        }

        if ( left ) firstPortal = null;
        if ( !left ) secondPortal = null;
    }

    public Location getSecondBlockLocationOnFloor( Location firstBlock ) {
        Location[] nearbyBlocks = new Location[]{
                firstBlock.clone().add( 1, 0, 0 ),
                firstBlock.clone().add( 0, 0, 1 ),
                firstBlock.clone().subtract( 1, 0, 0 ),
                firstBlock.clone().subtract( 0, 0, 1 )
        };

        for ( Location nearbyBlock : nearbyBlocks ) {
            if ( nearbyBlock.clone().add( 0, 1, 0 ).getBlock().getType() != Material.AIR )
                continue;

            return nearbyBlock;
        }

        return null;
    }

}
