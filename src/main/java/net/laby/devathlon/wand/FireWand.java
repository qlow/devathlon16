package net.laby.devathlon.wand;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Random;

/**
 * Class created by LabyStudio
 */
public class FireWand extends Wand {

    private Random random = new Random();
    private long last5Tick = 0;
    private Location currentLocation = null;
    private Location targetLocation = null;
    private int level;

    public FireWand( Player player ) {
        super( player, Material.BLAZE_ROD, 0, "Fire Wand", "" );
    }

    public FireWand( ) {
        this( null );
    }

    @Override
    public void onTick( ) {
        if ( currentLocation != null ) {
            if ( last5Tick < System.currentTimeMillis() ) {
                last5Tick = ( long ) ( System.currentTimeMillis() + ( level * 5 ) );

                Vector add = this.targetLocation.toVector().subtract( this.currentLocation.toVector() ).normalize().multiply( 1.5 );
                currentLocation = this.currentLocation.add( add );

                this.currentLocation = this.currentLocation.getWorld().getHighestBlockAt( this.currentLocation ).getLocation();

                if ( this.currentLocation.getBlock().getType() == Material.AIR ) {
                    this.currentLocation.getBlock().setType( Material.FIRE );
                }
                this.currentLocation.getBlock().getWorld().playSound( this.currentLocation.getBlock().getLocation(), Sound.ENTITY_BLAZE_SHOOT, 5, level * 0.05f );
                this.currentLocation.getBlock().getWorld().playEffect( this.currentLocation.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 1 );

                level++;

                if ( level > 30 || this.currentLocation.distance( targetLocation ) < 2 ) {
                    this.currentLocation.getBlock().getWorld().createExplosion( this.currentLocation, 2f );
                    targetLocation = null;
                    currentLocation = null;
                    level = 0;
                }
            }
        }
    }

    @Override
    public void onRightClick( ) {
        if ( getPlayer().isOnGround() ) {
            Block targetBlock = getPlayer().getTargetBlock( ( HashSet<Byte> ) null, 50 );
            if ( targetBlock != null && currentLocation == null ) {
                Entity target = getTargetEntity( targetBlock.getLocation(), 40 );
                if ( target == null ) {
                    this.targetLocation = targetBlock.getWorld().getHighestBlockAt( targetBlock.getLocation() ).getLocation();
                } else {
                    this.targetLocation = target.getLocation();
                }
                this.currentLocation = getPlayer().getLocation();
                this.level = 0;
            }
        }
    }


    public Entity getTargetEntity( Location location, double range ) {
        double distance = range;
        Entity target = null;
        for ( Entity all : location.getWorld().getEntities() ) {
            double entityDistance = all.getLocation().distance( location );
            if ( entityDistance < distance ) {
                distance = entityDistance;
                target = all;
            }
        }
        return target;
    }
}
