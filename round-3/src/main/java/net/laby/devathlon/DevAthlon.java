package net.laby.devathlon;

import lombok.Getter;
import net.laby.game.Game;
import net.laby.game.GamePlayer;
import net.laby.game.command.SchematicCommand;
import net.laby.schematic.SchematicCreator;
import net.laby.schematic.SchematicLoader;
import net.laby.schematic.ShipModel;
import net.laby.ship.Ship;
import net.laby.ship.ShipModelStarter;
import net.minecraft.server.v1_10_R1.ChatComponentText;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class created by qlow | Jan
 */
public class DevAthlon extends JavaPlugin implements Listener {

    public static File SCHEMATIC_FOLDER;
    private HashMap<String, ShipModel> schematicModels = new HashMap<String, ShipModel>();
    private static DevAthlon instance;

    @Getter
    private SchematicCreator schematicCreator;

    @Getter
    private List<Ship> ships = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;

        SCHEMATIC_FOLDER = new File( getInstance().getDataFolder(), "schematics" );

        Bukkit.getPluginManager().registerEvents( this, this );
        Bukkit.getPluginManager().registerEvents( schematicCreator = new SchematicCreator(), this );

        new SchematicLoader( SCHEMATIC_FOLDER ).load();

        // Initializing game
        new Game();

        // Registering command
        new SchematicCommand();

        // Starting runnable that sets the action-bar for the players
        new BukkitRunnable() {

            @Override
            public void run() {
                for ( GamePlayer gamePlayer : GamePlayer.getPlayers().values() ) {
                    if ( !gamePlayer.isIngame() )
                        continue;

                    if ( !gamePlayer.getPlayer().isOnline() )
                        continue;

                    (( CraftPlayer ) gamePlayer.getPlayer()).getHandle().playerConnection.sendPacket(
                            new PacketPlayOutChat( new ChatComponentText( "§7Leben:  " + gamePlayer.getHeartString() ), ( byte ) 2 ) );
                }
            }

        }.runTaskTimer( this, 20L, 20L );

    }

    @Override
    public void onDisable() {
        for ( Ship ship : ships ) {
            ship.dismount();
        }
    }

    // TODO: REMOVE
    @EventHandler
    public void onChat( AsyncPlayerChatEvent event ) {
        Player player = event.getPlayer();

        if ( !event.getMessage().equalsIgnoreCase( "spawnship" ) )
            return;

        Bukkit.getScheduler().runTask( this, new Runnable() {
            @Override
            public void run() {
                new ShipModelStarter( player );
            }
        } );
    }

    public static DevAthlon getInstance() {
        return instance;
    }

    public HashMap<String, ShipModel> getSchematicModels() {
        return schematicModels;
    }
}
