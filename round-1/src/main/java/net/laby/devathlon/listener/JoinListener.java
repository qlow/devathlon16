package net.laby.devathlon.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Class created by qlow | Jan
 */
public class JoinListener implements Listener {

    @EventHandler
    public void onJoin( PlayerJoinEvent event ) {
        // Setting message & setting health/foodlevel
        event.setJoinMessage( "§6" + event.getPlayer().getName() + " §7hat das Spiel §abetreten!" );

        event.getPlayer().setHealth( 20D );
        event.getPlayer().setFoodLevel( 20 );
    }

}
