package net.laby.devathlon;

import net.laby.devathlon.commands.ArenaCommand;
import net.laby.devathlon.game.ArenaManager;
import net.laby.devathlon.listener.JoinListener;
import net.laby.devathlon.listener.QuitListener;
import net.laby.devathlon.wand.WandManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Class created by qlow | Jan
 */
public class Devathlon extends JavaPlugin {

    public static final String PREFIX = "§7\u00BB §6MagicalBattle§8: §r";

    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        this.instance = this;

        // Loading WandManager
        new WandManager( this );

        // Loading arenas
        this.arenaManager = new ArenaManager( new File( "arenas" ) );

        // Registering listeners
        Listener[] listeners = new Listener[]{
                new JoinListener(),
                new QuitListener()
        };

        for ( Listener listener : listeners ) {
            Bukkit.getPluginManager().registerEvents( listener, this );
        }

        // Registering commands
        new ArenaCommand();
    }

    @Override
    public void onDisable() {
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    private static Devathlon instance;

    public static Devathlon getInstance() {
        return instance;
    }

}
