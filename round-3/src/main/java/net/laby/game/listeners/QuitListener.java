package net.laby.game.listeners;

import net.laby.game.GamePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Class created by qlow | Jan
 */
public class QuitListener implements Listener {

    @EventHandler
    public void onQuit( PlayerQuitEvent event ) {
        GamePlayer.getPlayer( event.getPlayer().getUniqueId() ).leaveGame();
        GamePlayer.getPlayers().remove( event.getPlayer().getUniqueId() );
    }

}
