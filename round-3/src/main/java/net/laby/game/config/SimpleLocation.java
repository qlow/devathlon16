package net.laby.game.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Class created by qlow | Jan
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SimpleLocation {

    private int x, y, z;
    private float yaw, pitch;
    private String worldName;

    public Location getLocation() {
        if ( worldName == null )
            return null;

        return new Location( Bukkit.getWorld( worldName ), x, y, z, yaw, pitch );
    }

    public Location getLocationAtMid() {
        if ( worldName == null )
            return null;

        return new Location( Bukkit.getWorld( worldName ), x + 0.5, y, z + 0.5, yaw, pitch );
    }

    public static SimpleLocation toSimpleLocation( Location location ) {
        return new SimpleLocation( location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                location.getYaw(), location.getPitch(), location.getWorld().getName() );
    }

}
