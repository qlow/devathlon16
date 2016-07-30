package net.laby.devathlon;

import net.laby.ship.ShipModelStarter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Class created by qlow | Jan
 */
public class DevAthlon extends JavaPlugin implements Listener {

    private static DevAthlon instance;

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getPluginManager().registerEvents( this, this );
    }

    // DEBUGGING
    @EventHandler
    public void onInteractAtEntity( PlayerInteractAtEntityEvent event ) {
        Player player = event.getPlayer();

        if ( !(event.getRightClicked() instanceof ArmorStand) )
            return;


    }

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
}
