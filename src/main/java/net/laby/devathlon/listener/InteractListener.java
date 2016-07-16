package net.laby.devathlon.listener;

import net.laby.devathlon.Devathlon;
import net.laby.devathlon.game.Arena;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
public class InteractListener implements Listener {

    @EventHandler
    public void onInteract( PlayerInteractEvent event ) {
        Player player = event.getPlayer();

        if ( event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.PHYSICAL )
            return;

        if ( !(event.getClickedBlock().getState() instanceof Sign) )
            return;

        if ( Devathlon.getInstance().getArenaManager().isIngame( player ) )
            return;

        Sign sign = ( Sign ) event.getClickedBlock().getState();

        for ( Arena arena : Devathlon.getInstance().getArenaManager().getArenas() ) {
            for ( Location signLocation : arena.getSigns() ) {
                if ( !signLocation.equals( sign.getLocation() ) )
                    continue;

                if ( arena.isIngame() ) {
                    player.sendMessage( Devathlon.PREFIX + "§cDiese Arena ist bereits im Spiel!" );
                    return;
                }

                arena.getJoinedPlayers().add( player.getUniqueId() );
                arena.updateSigns();

                if ( arena.getJoinedPlayers().size() == 2 ) {
                    arena.setIngame( true );

                    arena.updateSigns();

                    int spawnIndex = 0;
                    for ( UUID joinedPlayer : arena.getJoinedPlayers() ) {
                        if ( spawnIndex == arena.getJoinedPlayers().size() ) {
                            spawnIndex = 0;
                        }

                        Player joined = Bukkit.getPlayer( joinedPlayer );

                        // Clearing inventory & adding wands
                        joined.getInventory().clear();
                        Devathlon.getInstance().getWandManager().giveAllWands( joined );

                        // Caching location
                        Devathlon.getInstance().getCachedLocations().put( joinedPlayer, joined.getLocation() );

                        // Sending message & teleporting
                        joined.sendMessage( Devathlon.PREFIX + "§aDer Fight startet jetzt!" );

                        joined.teleport( arena.getSpawns().get( spawnIndex ) );
                        joined.setGameMode( GameMode.SURVIVAL );
                        joined.setHealth( 20D );

                        spawnIndex++;
                    }
                }

                return;
            }
        }
    }

}
