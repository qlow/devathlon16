package net.laby.game.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Class created by LabyStudio
 */
public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryInteract( InventoryClickEvent event) {
        if(event.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled( true );
        }
    }
}
