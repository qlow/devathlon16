package net.laby.devathlon.listener;

import net.laby.devathlon.Devathlon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * Class created by qlow | Jan
 */
public class PlayerDropItemListener implements Listener {

    @EventHandler
    public void onDropItem( PlayerDropItemEvent event ) {
        // Cancelling event if the player is ingame
        if ( !Devathlon.getInstance().getArenaManager().isIngame( event.getPlayer() ) )
            return;

        event.setCancelled( true );
    }

}
