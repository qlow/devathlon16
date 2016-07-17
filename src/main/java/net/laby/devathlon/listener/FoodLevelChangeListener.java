package net.laby.devathlon.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Class created by qlow | Jan
 */
public class FoodLevelChangeListener implements Listener {

    @EventHandler
    public void onFoodLevelChange( FoodLevelChangeEvent event ) {
        // Cancelling FoodLevelChangeEvent
        event.setCancelled( true );
    }

}
