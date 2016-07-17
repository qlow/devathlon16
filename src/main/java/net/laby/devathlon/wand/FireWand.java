package net.laby.devathlon.wand;

import net.laby.devathlon.Devathlon;
import net.laby.devathlon.game.Arena;
import net.minecraft.server.v1_10_R1.Explosion;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;

/**
 * Class created by LabyStudio
 */
public class FireWand extends Wand {

    // Last tick to modify the speed
    private long lastTick = 0;
    // Current location of the fire
    private Location currentLocation = null;
    // Target location of the fire
    private Location targetLocation = null;
    // Sound pitch level
    private int level;

    private Arena currentArena;

    public FireWand( Player player ) {
        super( player, Material.BLAZE_ROD, 0, "§6Fire Wand", "", "§7-> Mit Rechtsklick kannst du", "   §7auf dein Ziel eine riesen", "   §7Feuerspur schießen.", "" );

        if ( player != null ) {
            currentArena = Devathlon.getInstance().getArenaManager().getPlayerArena( player );
        }
    }

    public FireWand() {
        this( null );
    }

    @Override
    public void onTick() {
        if ( currentLocation != null ) {
            if ( lastTick < System.currentTimeMillis() ) {
                lastTick = ( long ) (System.currentTimeMillis() + (level * 5));

                // Get next block
                Vector add = this.targetLocation.toVector().subtract( this.currentLocation.toVector() ).normalize().multiply( 1.5 );
                currentLocation = this.currentLocation.add( add );

                // Set the fire on top of the world
                this.currentLocation = this.currentLocation.getWorld().getHighestBlockAt( this.currentLocation ).getLocation();

                // Replace the current block to fire if the block type is air
                if ( this.currentLocation.getBlock().getType() == Material.AIR ) {
                    this.currentLocation.getBlock().setType( Material.FIRE );

                    if ( currentArena != null && currentLocation.getBlock().getType() == Material.FIRE ) {
                        currentArena.getFireBlocks().add( this.currentLocation );
                    }
                }

                // Sound and Effects
                this.currentLocation.getBlock().getWorld().playSound( this.currentLocation.getBlock().getLocation(), Sound.ENTITY_BLAZE_SHOOT, 5, level * 0.05f );
                this.currentLocation.getBlock().getWorld().playEffect( this.currentLocation.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 1 );

                // Next pitch level
                level++;

                // Stop the fire
                if ( level > 15 || this.currentLocation.distance( targetLocation ) < 2 ) {
                    //this.currentLocation.getBlock().getWorld().playSound( this.currentLocation, Sound.ENTITY_GENERIC_EXPLODE, 5, 1 );
                    //this.currentLocation.getBlock().getWorld().playEffect( this.currentLocation, Effect.EXPLOSION_LARGE, 1 );
                    createExplosion( this.currentLocation );
                    targetLocation = null;
                    currentLocation = null;
                    level = 0;
                }
            }
        }
    }

    @Override
    public void onRightClick() {
        // Get target block
        Block targetBlock = getPlayer().getTargetBlock( ( HashSet<Byte> ) null, 50 );
        if ( targetBlock != null && currentLocation == null ) {
            // Check nearest player
            Entity target = getNearestEntity( targetBlock.getLocation(), 40 );
            if ( target == null ) {
                // Use the target block location
                this.targetLocation = targetBlock.getWorld().getHighestBlockAt( targetBlock.getLocation() ).getLocation();
            } else {
                // Use nearest players location
                this.targetLocation = target.getLocation();
            }
            // Set startpoint location
            this.currentLocation = getPlayer().getLocation();
            this.level = 0;
        }
    }

    /**
     * Get the nearest entity
     *
     * @param location
     * @param range    search radius
     * @return nearest entity
     */
    public Entity getNearestEntity( Location location, double range ) {
        double distance = range;
        Entity target = null;
        for ( Entity all : location.getWorld().getEntities() ) {
            if ( all.getWorld().equals( location.getWorld() ) ) {
                double entityDistance = all.getLocation().distance( location );
                if ( entityDistance < distance ) {
                    distance = entityDistance;
                    target = all;
                }
            }
        }
        return target;
    }

    /**
     * Creates an explosion at this location
     * @param location explosion-location
     */
    private void createExplosion( Location location ) {
        net.minecraft.server.v1_10_R1.World world = (( CraftWorld ) location.getWorld()).getHandle();
        Explosion explosion = new Explosion( world, null, location.getX(), location.getY(), location.getZ(), 2.8F, true, false );
        explosion.a();
        explosion.a( true );
    }
}
