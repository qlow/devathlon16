package net.laby.devathlon.listener;

import net.laby.devathlon.Devathlon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Class created by qlow | Jan
 */
public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( !(event.getWhoClicked() instanceof Player) )
            return;

        if ( !Devathlon.getInstance().getArenaManager().isIngame( ( Player ) event.getWhoClicked() ) )
            return;

        event.setCancelled( true );
    }

}
