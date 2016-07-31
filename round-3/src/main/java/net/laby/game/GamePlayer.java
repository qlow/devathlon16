package net.laby.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
public class GamePlayer {

    @Getter
    private final static Map<UUID, GamePlayer> players = new HashMap<>();

    @Getter
    private UUID uuid;

    private Player player;

    @Getter
    @Setter
    private int level, killStreak;

    @Getter
    @Setter
    private boolean ingame;

    public GamePlayer( UUID uuid ) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer( uuid );
    }

    public void leaveGame() {
        if(!isIngame())
            return;

        // Teleporting back to lobby-spawn
        player.setScoreboard( Bukkit.getScoreboardManager().getMainScoreboard() );
        player.teleport( Game.getGame().getConfig().getLobbySpawn().getLocationAtMid() );
        setIngame( false );
    }

    public static GamePlayer getPlayer( UUID uuid ) {
        return players.get( uuid );
    }

}
