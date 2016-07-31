package net.laby.devathlon;

import lombok.Getter;
import net.laby.game.Game;
import net.laby.game.command.SchematicCommand;
import net.laby.schematic.SchematicCreator;
import net.laby.schematic.SchematicLoader;
import net.laby.schematic.ShipModel;
import net.laby.ship.ShipModelStarter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

/**
 * Class created by qlow | Jan
 */
public class DevAthlon extends JavaPlugin implements Listener {

    public static File SCHEMATIC_FOLDER;
    private HashMap<String, ShipModel> schematicModels = new HashMap<String, ShipModel>();
    private static DevAthlon instance;

    @Getter private SchematicCreator schematicCreator;

    @Override
    public void onEnable() {
        instance = this;

        SCHEMATIC_FOLDER = new File(getInstance().getDataFolder(), "schematics");

        Bukkit.getPluginManager().registerEvents( this, this );
        Bukkit.getPluginManager().registerEvents( schematicCreator = new SchematicCreator(), this );

        new SchematicLoader(SCHEMATIC_FOLDER).load();

        // Initializing game
        new Game();

        // Registering command
        new SchematicCommand();
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

    public HashMap<String, ShipModel> getSchematicModels( ) {
        return schematicModels;
    }
}
