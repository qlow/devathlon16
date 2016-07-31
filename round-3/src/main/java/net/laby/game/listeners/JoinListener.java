package net.laby.game.listeners;

import net.laby.game.Game;
import net.laby.game.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Class created by qlow | Jan
 */
public class JoinListener implements Listener {

    @EventHandler
    public void onJoin( PlayerJoinEvent event ) {
        Player player = event.getPlayer();

        // Setting main-scoreboard
        player.setScoreboard( Bukkit.getScoreboardManager().getMainScoreboard() );

        Location lobbySpawn = Game.getGame().getConfig().getLobbySpawn().getLocationAtMid();

        if ( lobbySpawn != null ) {
            // Teleporting player to lobby-spawn
            player.teleport( lobbySpawn );
        }

        // Putting GamePlayer into GamePlayer-map
        GamePlayer.getPlayers().put( player.getUniqueId(), new GamePlayer( player.getUniqueId() ) );

        // Resetting some values
        player.setHealth( 20D );
    }

}
