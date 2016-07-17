package net.laby.devathlon.wand;

import net.laby.devathlon.Devathlon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashSet;

/**
 * Class created by qlow | Jan
 */
public class InvasionWand extends Wand {

    private static long lastUsed;

    public InvasionWand( Player player ) {
        super(player, Material.SPECTRAL_ARROW, 0, "§aZombieInvasion", "", "§7-> Mit Rechtsklick kannst du", "   §7auf dein Ziel eine Zombie", "   §7Invasion spawnen.", "");
    }

    public InvasionWand() {
        this(null);
    }

    @Override
    public void onRightClick() {
        if((System.currentTimeMillis() - lastUsed) <= 20000) {
            getPlayer().sendMessage( Devathlon.PREFIX + "§cDu kannst erst in  " + ((System.currentTimeMillis() - lastUsed) / 1000) + " Sekunden eine Invasion auf ein Ziel spawnen lassen!" );
            return;
        }

        Block targetBlock = getPlayer().getTargetBlock( ( HashSet<Byte> ) null, 50 );

        if(targetBlock == null)
            return;

        lastUsed = System.currentTimeMillis();

        Location zombieLocation = targetBlock.getLocation().clone().add( 0, 1, 0 );

        zombieLocation.getWorld().setTime( 16000 );
        zombieLocation.getWorld().strikeLightning( zombieLocation );

        for(int i = 0; i < 15; i++) {
            zombieLocation.getWorld().spawnEntity( zombieLocation, EntityType.ZOMBIE );
        }
    }
}
