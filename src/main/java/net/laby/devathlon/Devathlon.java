package net.laby.devathlon;

import net.laby.devathlon.commands.ArenaCommand;
import net.laby.devathlon.game.ArenaManager;
import net.laby.devathlon.listener.*;
import net.laby.devathlon.wand.WandManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
public class Devathlon extends JavaPlugin {

    public static final String PREFIX = "§7\u00BB §6MagicalBattle§8: §r";

    private WandManager wandManager;
    private ArenaManager arenaManager;
    private Map<UUID, Location> cachedLocations = new HashMap<>();

    @Override
    public void onEnable() {
        this.instance = this;

        // Loading WandManager
        this.wandManager = new WandManager( this );

        // Loading arenas
        this.arenaManager = new ArenaManager( new File( "arenas" ) );

        // Registering listeners
        Listener[] listeners = new Listener[]{
                new JoinListener(),
                new QuitListener(),
                new SignChangeListener(),
                new InteractListener(),
                new FoodLevelChangeListener(),
                new BlockBreakListener(),
                new EntityDamageListener(),
                new PlayerDeathListener(),
                new BlockBurnListener()
        };

        for ( Listener listener : listeners ) {
            Bukkit.getPluginManager().registerEvents( listener, this );
        }

        // Registering commands
        new ArenaCommand();
    }

    @Override
    public void onDisable() {


        // Teleporting ingame players back
        for ( Map.Entry<UUID, Location> cachedLocationEntry : cachedLocations.entrySet() ) {
            Player player = Bukkit.getPlayer( cachedLocationEntry.getKey() );

            if ( player == null )
                continue;

            player.teleport( cachedLocationEntry.getValue() );

            // Resetting some stuff
            player.setHealth( 20D );
            player.setFireTicks( 0 );
            player.getInventory().clear();
            player.getInventory().setArmorContents( null );
        }
    }

    public Map<UUID, Location> getCachedLocations() {
        return cachedLocations;
    }

    public WandManager getWandManager() {
        return wandManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    private static Devathlon instance;

    public static Devathlon getInstance() {
        return instance;
    }

}
