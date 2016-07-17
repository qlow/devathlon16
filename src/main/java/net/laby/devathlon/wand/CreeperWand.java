package net.laby.devathlon.wand;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class created by LabyStudio
 */
public class CreeperWand extends Wand {

    // Arraylist for every creeper
    private ArrayList<Creeper> creepers = new ArrayList<Creeper>();

    public CreeperWand( Player player ) {
        super( player, Material.TIPPED_ARROW, 0, "§2Creeper Wand", "", "§7-> Mit Rechtsklick kannst du Creeper", "   §7schießen!", "" );
    }

    public CreeperWand() {
        this( null );
    }

    @Override
    public void onDisable() {
        creepers.clear();
    }

    @Override
    public void onTick() {
        // Rotate all creepers
        Iterator<Creeper> iterator = creepers.iterator();
        while ( iterator.hasNext() ) {
            Creeper creeper = ( Creeper ) iterator.next();
            // Remove creeper from arraylist if dead
            if ( creeper.isDead() ) {
                iterator.remove();
            } else {
                Location rotation = creeper.getLocation();
                // Teleport the creeper every tick
                float r = rotation.getYaw() + 30;
                rotation.setYaw( r );
                creeper.teleport( rotation );
            }
        }
    }

    @Override
    public void onRightClick() {
        // Shoot a creeper in players direction
        Creeper creeper = ( Creeper ) getPlayer().getWorld().spawnEntity( getPlayer().getLocation().clone().add( 0, 1, 0 ), EntityType.CREEPER );
        creeper.setVelocity( getPlayer().getLocation().getDirection().normalize().multiply( 3 ) );
        creeper.setPowered( true );
        // Add creeper to arraylist for rotating
        creepers.add( creeper );
    }
}
