package net.laby.devathlon.wand;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
public class PortalWand extends Wand {

    private Player player;
    private List<BlockState> oldBlocks = new ArrayList<>();

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
        List<Block> blocks = player.getLineOfSight( new HashSet<Material>(), 50 );

        for ( Block block : blocks ) {
            Location location = block.getLocation();

            if ( block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER ) {
                // Spawning a slime
                PacketContainer packetContainer = ProtocolLibrary.getProtocolManager().createPacket( PacketType.Play.Server.SPAWN_ENTITY_LIVING );
                packetContainer.getIntegers().write( 0, ++counter );                    // Entity-ID
                packetContainer.getSpecificModifier( UUID.class ).write( 0, UUID.randomUUID() );  // Entity-UUID
                packetContainer.getIntegers().write( 1, 55 );                           // Entity's type
                packetContainer.getDoubles().write( 0, location.getX() )                // X-position
                        .write( 1, location.getY() )                                    // Y-position
                        .write( 2, location.getZ() );                                   // Z-position

                packetContainer.getBytes().write( 0, ( byte ) 0 );                      // Angle
                packetContainer.getBytes().write( 1, ( byte ) 0 );                      // Angle
                packetContainer.getBytes().write( 2, ( byte ) 0 );                      // Angle

                packetContainer.getBytes().write( 3, ( byte ) 0 );                      // Vector-X
                packetContainer.getBytes().write( 4, ( byte ) 0 );                      // Vector-Y
                packetContainer.getBytes().write( 5, ( byte ) 0 );                      // Vector-Z

                WrappedDataWatcher dataWatcher = new WrappedDataWatcher();

                // A lot metadata stuff
                dataWatcher.setObject( 0, ( byte ) (0 | 1 << 5) );  // Invisibility
                dataWatcher.setObject( 7, 0 );                    // Potion effect color
                dataWatcher.setObject( 8, ( byte ) 0 );             // Is potion effect active?
                dataWatcher.setObject( 9, ( byte ) 0 );             // Number of Arrows
                dataWatcher.setObject( 6, ( float ) 1.0f );         // Health of entity
                dataWatcher.setObject( 16, ( byte ) 1 );            // Slime size

                packetContainer.getDataWatcherModifier().write( 0, dataWatcher );       // Metadata

                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket( player, packetContainer );
                } catch ( InvocationTargetException e ) {
                    e.printStackTrace();
                }

                // Sending potion-packet
                PacketContainer potionPacketContainer = ProtocolLibrary.getProtocolManager().createPacket( PacketType.Play.Server.ENTITY_EFFECT );

                potionPacketContainer.getIntegers().write( 0, counter );
                potionPacketContainer.getBytes().write( 0, ( byte ) 24 ).write( 1, ( byte ) 1 );
                potionPacketContainer.getIntegers().write( 1, Integer.MAX_VALUE );
                potionPacketContainer.getBooleans().write( 0, true );

                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket( player, potionPacketContainer );
                } catch ( InvocationTargetException e ) {
                    e.printStackTrace();
                }

                continue;
            }
        }
    }

    private void sendDefaultBlocks() {
        for ( BlockState oldBlock : oldBlocks ) {
            oldBlock.update( true );
        }
    }

}
