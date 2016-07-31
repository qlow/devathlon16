package net.laby.game.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Class created by qlow | Jan
 */
public class EntityExplodeListener implements Listener {

    @EventHandler
    public void onExplode( EntityExplodeEvent event ) {
        event.setCancelled( true );
    }

}
