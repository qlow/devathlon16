package net.laby.game.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Class created by LabyStudio
 */
public class PlayerBlockListener implements Listener {

    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        if ( !event.getPlayer().isOp() ) {
            event.setCancelled( true );
        }
    }

    @EventHandler
    public void onBlockPlace( BlockPlaceEvent event ) {
        if ( !event.getPlayer().isOp() ) {
            event.setCancelled( true );
        }
    }

}
