package net.laby.devathlon.wand;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Class created by LabyStudio
 */
public class MagicLaserWand extends Wand {

    private Player player;

    public MagicLaserWand( Player player ) {
        super( player, Material.TIPPED_ARROW, ( byte ) 0, "Magic Laser Wand", "My first wand!" );
        this.player = player;
    }

    public MagicLaserWand( ) {
        this( null );
    }

    @Override
    public void onEnable( ) {  }

    @Override
    public void onTick( ) {
        //this.player.getWorld().playEffect( this.player.getLocation().clone().add( 0, 1, 0 ), Effect.MOBSPAWNER_FLAMES, 0 );

        if(isRightClicking()) {
            shoot();
        }
    }

    private void shoot() {

    }
}
