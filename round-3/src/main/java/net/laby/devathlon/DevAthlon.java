package net.laby.devathlon;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Class created by qlow | Jan
 */
public class DevAthlon extends JavaPlugin {

    @Override
    public void onEnable() {
        new TestingClass( this );
    }
}
