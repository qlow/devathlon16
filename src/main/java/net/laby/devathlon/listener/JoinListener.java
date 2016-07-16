package net.laby.devathlon.listener;

import net.laby.devathlon.Devathlon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Class created by qlow | Jan
 */
public class JoinListener implements Listener {

    @EventHandler
    public void onJoin( PlayerJoinEvent event ) {
        event.setJoinMessage( Devathlon.PREFIX + "§6" + event.getPlayer().getName() + " §7hat das Spiel §abetreten!" );
    }

}
