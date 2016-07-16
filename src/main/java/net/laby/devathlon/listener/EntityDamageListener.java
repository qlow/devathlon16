package net.laby.devathlon.listener;

import net.laby.devathlon.Devathlon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Class created by qlow | Jan
 */
public class EntityDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage( EntityDamageEvent event ) {
        if ( !(event.getEntity() instanceof Player) )
            return;

        if ( Devathlon.getInstance().getArenaManager().isIngame( ( Player ) event.getEntity() ) )
            return;

        event.setCancelled( true );
    }

}
