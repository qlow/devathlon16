package net.laby.devathlon.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.world.WorldLoadEvent;

/**
 * Class created by qlow | Jan
 */
public class BlockBurnListener implements Listener {

    @EventHandler
    public void onBlockBurn( BlockBurnEvent event ) {
        event.setCancelled( true );
    }

    @EventHandler
    public void onWorldLoad( WorldLoadEvent event ) {
        event.getWorld().setGameRuleValue( "doFireTick", "false" );
    }

}
