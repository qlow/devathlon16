package net.laby.game;

import lombok.Getter;
import net.laby.ship.Ship;
import net.laby.ship.ShipModelStarter;
import org.bukkit.entity.Player;

/**
 * Class created by qlow | Jan
 */
public enum Level {

    STARTER( ShipModelStarter.class );

    @Getter
    private static int maxLevels = Level.values().length;

    @Getter
    private Class<? extends Ship> shipModel;

    @Getter
    private int level, neededKillStreak;

    Level( Class<? extends Ship> shipModel ) {
        try {
            shipModel.getConstructor( Player.class );
        } catch ( NoSuchMethodException e ) {
            throw new IllegalArgumentException( "Ship-Model should have a constructor that only has one parameter (Player)!" );
        }

        this.level = ordinal() + 1;
        this.shipModel = shipModel;
        this.neededKillStreak = ((ordinal() + 1) * 15) + ((ordinal() - 1) * 5);
    }

}
