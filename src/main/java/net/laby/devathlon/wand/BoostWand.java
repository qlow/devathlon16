package net.laby.devathlon.wand;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Class created by LabyStudio
 */
public class BoostWand extends Wand {

    private Entity clickedEntity;
    private int power = 0;

    public BoostWand( Player player ) {
        super( player, Material.SPECTRAL_ARROW, 0, "§9Boost Wand", "", "§7-> Schießt den Spieler ", "   §7in die Luft");
    }

    public BoostWand() {
        this( null );
    }



    @Override
    public void onRightClick( ) {
        if(clickedEntity == null || power >= 20) {
            List<Entity> nearbyEntities = getPlayer().getNearbyEntities( 2, 2, 2 );
            if ( nearbyEntities != null && !nearbyEntities.isEmpty() ) {
                this.clickedEntity = nearbyEntities.get( 0 );
                this.power = 0;
            }
        }
    }

    @Override
    public void onTick( ) {
        if(clickedEntity == null) {
            return;
        }
        if(isRightClicking()) {

            if(this.clickedEntity.isDead()) {
                this.clickedEntity = null;
                this.power = 0;
                return;
            }

            if(this.clickedEntity.getLocation().distance( getPlayer().getLocation() ) > 2) {
                this.clickedEntity = null;
                this.power = 0;
                return;
            }

            this.power++;

            getPlayer().getWorld().playSound( getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 2, power * 0.1f );
            getPlayer().getWorld().playEffect( getPlayer().getLocation(), Effect.SMALL_SMOKE, 1);

            if(this.power > 10) {

                Vector vec = getPlayer().getLocation().getDirection();
                vec.setY( 1.5 );
                this.clickedEntity.setVelocity( vec );
                getPlayer().getWorld().playEffect( getPlayer().getLocation(), Effect.EXPLOSION_LARGE, 1);

                this.clickedEntity = null;
                this.power = 0;
            }
        }
    }
}
