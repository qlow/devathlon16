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
        // Resetting player & calling Arena#loosePlayer if the player is in an arena
        event.setDeathMessage( null );
        Player player = event.getEntity();

        player.setAllowFlight( false );
        player.setWalkSpeed( 0.2f );

        Arena playerArena = Devathlon.getInstance().getArenaManager().getPlayerArena( player );

        if ( playerArena != null
                && playerArena.isIngame() ) {
            playerArena.loosePlayer( player );
        }


    }

}
