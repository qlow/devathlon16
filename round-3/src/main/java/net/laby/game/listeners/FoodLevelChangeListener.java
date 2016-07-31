package net.laby.game.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Class created by qlow | Jan
 */
public class FoodLevelChangeListener implements Listener {

    @EventHandler
    public void onFoodLevelChange( FoodLevelChangeEvent event ) {
        event.setCancelled( true );
    }

}
