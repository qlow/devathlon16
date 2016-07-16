package net.laby.devathlon.listener;

import net.laby.devathlon.Devathlon;
import net.laby.devathlon.game.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Class created by qlow | Jan
 */
public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath( PlayerDeathEvent event ) {
        Player player = event.getEntity();

        Arena playerArena = Devathlon.getInstance().getArenaManager().getPlayerArena( player );

        if ( playerArena != null
                && playerArena.isIngame() ) {
            playerArena.loosePlayer( player );
        }
    }

}
