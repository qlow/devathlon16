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

    @Getter
    private Player player;

    @Getter
    @Setter
    private int level, killStreak;

    @Getter
    @Setter
    private boolean ingame;

    @Getter
    @Setter
    private long joined;

    @Getter
    @Setter
    private long lastShooted;

    @Getter
    @Setter
    private float life = 20F;

    public GamePlayer( UUID uuid ) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer( uuid );
    }

    public void leaveGame() {
        if ( !isIngame() )
            return;

        // Teleporting back to lobby-spawn
        player.setScoreboard( Bukkit.getScoreboardManager().getMainScoreboard() );
        player.teleport( Game.getGame().getConfig().getLobbySpawn().getLocationAtMid() );
        player.getInventory().clear();
        player.setCompassTarget( player.getLocation() );
        setIngame( false );
    }

    public float getAttackDamage() {
        return Level.values()[level].getAttackDamage();
    }

    public int getRequiredKills() {
        return ((level + 1) == Level.values().length) ? -1 : Level.values()[level].getNeededKillStreak() - getKillStreak();
    }

    public String getHeartString( boolean info ) {
        int hearts = ( int ) (((100F / Level.values()[level].getMaxHearts()) * life) / 10F);

        String heartString = "§a";

        for ( int i = 0; i < 10; i++ ) {
            if ( hearts == 0 && i == 0 ) {
                heartString += "§c";
            }

            heartString += "❤";

            if ( (i + 1) == hearts ) {
                heartString += "§c";
            }
        }

        if ( info ) {
            heartString += " §7[" + (life < 10 ? "0" + life : life) + "/" + Level.values()[level].getMaxHearts() + "]";
        }

        return heartString;
    }

    public static GamePlayer getPlayer( UUID uuid ) {
        return players.get( uuid );
    }


}
