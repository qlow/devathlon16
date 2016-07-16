package net.laby.devathlon.listener;

import net.laby.devathlon.Devathlon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Class created by qlow | Jan
 */
public class QuitListener implements Listener {

    @EventHandler
    public void onQuit( PlayerQuitEvent event ) {
        event.setQuitMessage( Devathlon.PREFIX + "§6" + event.getPlayer().getName() + " §7hat das Spiel §cverlassen!" );
    }

    @EventHandler
    public void onKick( PlayerKickEvent event ) {
        event.setLeaveMessage( null );
    }

}
