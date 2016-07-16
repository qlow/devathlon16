package net.laby.devathlon;

import net.laby.devathlon.wand.WandManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Class created by qlow | Jan
 */
public class Devathlon extends JavaPlugin implements Listener {

    private static Devathlon instance;

    public static Devathlon getInstance( ) {
        return instance;
    }

    @Override
    public void onEnable() {
        this.instance = this;
        new WandManager( this );
    }

    @Override
    public void onDisable() {
    }

}
