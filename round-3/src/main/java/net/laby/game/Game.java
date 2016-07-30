package net.laby.game;

import lombok.Getter;
import net.laby.devathlon.DevAthlon;
import net.laby.game.config.GameConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Class created by qlow | Jan
 */
public class Game implements Listener {

    @Getter
    private static Game game;

    private GameConfig config;
    private GameScoreboardManager gameScoreboardManager;

    public Game() {
        game = this;

        this.config = new GameConfig();
        this.gameScoreboardManager = new GameScoreboardManager();

        Bukkit.getPluginManager().registerEvents( this, DevAthlon.getInstance() );
    }

    @EventHandler
    public void onJoin( PlayerJoinEvent event ) {
        Player player = event.getPlayer();

        Location lobbySpawn = config.getLobbySpawn().getLocationAtMid();

        if ( lobbySpawn != null ) {
            // Teleporting player to lobby-spawn
            player.teleport( lobbySpawn );
        }

        GamePlayer.getPlayers().put( player.getUniqueId(), new GamePlayer( player.getUniqueId() ) );
    }

    @EventHandler
    public void onQuit( PlayerQuitEvent event ) {
        GamePlayer.getPlayers().remove( event.getPlayer().getUniqueId() );
    }

}
