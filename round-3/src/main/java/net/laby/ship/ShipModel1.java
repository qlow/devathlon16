package net.laby.ship;

import org.bukkit.entity.Player;

/**
 * Class created by LabyStudio
 */
public class ShipModel1 extends Ship {

    public ShipModel1( Player player ) {
        super( player, "level1" );
        setMaxSpeed( 0.8 );
    }

}
