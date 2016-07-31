package net.laby.game;

import lombok.Getter;
import net.laby.ship.*;
import org.bukkit.entity.Player;

/**
 * Class created by qlow | Jan
 */
public enum Level {

    LEVEL1( ShipModel1.class ),
    LEVEL2( ShipModel2.class ),
    LEVEL3( ShipModel3.class ),
    LEVEL4( ShipModel4.class ),
    LEVEL5( ShipModel5.class ),
    LEVEL6( ShipModel6.class ),
    LEVEL7( ShipModel7.class ),
    LEVEL8( ShipModel8.class ),
    LEVEL9( ShipModel9.class ),
    LEVEL10( ShipModel10.class );

    @Getter
    private static int maxLevels = Level.values().length;

    @Getter
    private Class<? extends Ship> shipModel;

    @Getter
    private int level, neededKillStreak;

    @Getter
    private float maxHearts;

    @Getter
    private float attackDamage;

    Level( Class<? extends Ship> shipModel ) {
        try {
            shipModel.getConstructor( Player.class );
        } catch ( NoSuchMethodException e ) {
            throw new IllegalArgumentException( "Ship-Model should have a constructor that only has one parameter (Player)!" );
        }

        this.level = ordinal() + 1;
        this.shipModel = shipModel;
        this.neededKillStreak = ordinal() + 1;//((ordinal() + 1) * 15) + ((ordinal() - 1) * 5);
        this.maxHearts = 20F + (ordinal() * 5F);
        this.attackDamage = (ordinal() + 1) * 2F;
    }

}
