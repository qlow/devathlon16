package net.laby.game.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Class created by qlow | Jan
 */
public class DamageListener implements Listener {

    @EventHandler
    public void onDamage( EntityDamageEvent event ) {
        event.setCancelled( true );
    }

}
