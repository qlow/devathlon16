package net.laby.ship;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Class created by LabyStudio
 */
public class ShipModelStarter extends Ship {


    public ShipModelStarter( Player player ) {
        super( player );

    }

    public void buildModel( ) {
        add( 1, 0, 0, Material.WOOD );
        add( 2, 0, 0, Material.WOOD );
        add( 3, 0, 0, Material.WOOD );
        add( 4, 0, 0, Material.WOOD );
        add( 5, 0, 0, Material.WOOD );

        add( 0, 0, 1, Material.WOOD );
        add( 0, 0, 2, Material.WOOD );
        add( 0, 0, 3, Material.WOOD );
        add( 0, 0, 4, Material.WOOD );
        add( 0, 0, 5, Material.WOOD );

        add( 0, 0, -1, Material.WOOD );
        add( 0, 0, -2, Material.WOOD );
        add( 0, 0, -3, Material.WOOD );
        add( 0, 0, -4, Material.WOOD );
        add( 0, 0, -5, Material.WOOD );
    }
}
