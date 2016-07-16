package net.laby.devathlon.wand;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
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
    private List<Block> firstPortalBlocks = new ArrayList<>();
    private List<Block> secondPortalBlocks = new ArrayList<>();

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
        sendDefaultBlocks();
    }

    @Override
    public void onRightClick() {
        makePortal( false );
    }

    @Override
    public void onLeftClick() {
        makePortal( true );
    }

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

        for ( Block block : blocks ) {
            Location location = block.getLocation();

            if ( block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER ) {
                player.playEffect( location, Effect.MOBSPAWNER_FLAMES, 0 );
                continue;
            }

            if ( block == target )
                break;
        }

        List<Location> portalBlocks = new ArrayList<>();

        if ( target.getLocation().clone().add( 0, 1, 0 ).getBlock().getType() != Material.AIR ) {
            portalBlocks.add( target.getLocation() );
            portalBlocks.add( target.getLocation().add( 0, 1, 0 ) );
        } else {
            //TODO CHANGE THIS
            return;
        }

        for ( Location portalBlock : portalBlocks ) {
            byte data = 6;

            if ( left ) {
                firstPortalBlocks.add( portalBlock.getBlock() );
                data = 11;
            } else {
                secondPortalBlocks.add( portalBlock.getBlock() );
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

    private void sendDefaultBlocks() {
        List<Block> reset = new ArrayList<>();
        reset.addAll( firstPortalBlocks );
        reset.addAll( secondPortalBlocks );

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

    }

}
