package net.laby.schematic;

import net.laby.game.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Class created by LabyStudio
 */
public class Utils {
    public static float dataToYaw( byte data ) {
        switch ( data ) {
            case 1:
                return 0;
            case 2:
                return -90;
            case 3:
                return 90;
        }
        return 180;
    }


    public static Player getNearestPlayer( Player player ) {
        double distance = Integer.MAX_VALUE;
        Player target = null;
        for ( Player all : Bukkit.getOnlinePlayers() ) {
            if ( !GamePlayer.getPlayer( all.getUniqueId() ).isIngame() || all == player) {
                continue;
            }
            double dis = all.getLocation().distance( player.getLocation() );
            if ( dis < distance ) {
                target = all;
                distance = dis;
            }
        }
        return target;
    }
}
