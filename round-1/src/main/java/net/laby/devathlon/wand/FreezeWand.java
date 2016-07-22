package net.laby.devathlon.wand;

import net.laby.devathlon.Devathlon;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

/**
 * Class created by LabyStudio
 */
public class FreezeWand extends Wand {

    public static long lastFreeze;

    private HashMap<UUID, Long> freezedPlayers = new HashMap<UUID, Long>();

    public FreezeWand( Player player ) {
        super( player, Material.TIPPED_ARROW, 0, "§bFreeze Wand", "", "§7-> Mit Rechtsklick wirfst du",
                "   §7Schneebälle, die deinen Gegner", "   §7einfrieren.", "", "   §630 §7Sekunden Cooldown", "" );
    }

    public FreezeWand() {
        this( null );
    }

    /**
     * List of freezed players
     *
     * @return
     */
    public HashMap<UUID, Long> getFreezedPlayers() {
        return freezedPlayers;
    }

    @Override
    public void onTick() {
        ArrayList<UUID> list = new ArrayList<UUID>( freezedPlayers.keySet() );
        Iterator<UUID> iterator = list.iterator();
        while ( iterator.hasNext() ) {
            UUID uuid = iterator.next();
            long started = freezedPlayers.get( uuid );

            Player player = Bukkit.getPlayer( uuid );

            if ( player == null ) {
                freezedPlayers.remove( uuid );
                continue;
            }
            if ( started + 1000 * 10 < System.currentTimeMillis() ) {
                player.setWalkSpeed( 0.2f );
                player.removePotionEffect( PotionEffectType.JUMP );
                player.getWorld().playEffect( player.getLocation(), Effect.EXPLOSION_LARGE, 1 );
                player.getWorld().playSound( player.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 5, 1 );
                freezedPlayers.remove( uuid );
            } else {
                player.getWorld().playEffect( player.getLocation(), Effect.FIREWORKS_SPARK, 1 );
            }

        }
    }

    @Override
    public void onDisable() {
        for ( UUID uuid : freezedPlayers.keySet() ) {
            Player player = Bukkit.getPlayer( uuid );
            if ( player == null ) {
                continue;
            }
            player.setWalkSpeed( 0.2f );
            player.removePotionEffect( PotionEffectType.JUMP );
            getPlayer().sendMessage( Devathlon.PREFIX + "§cDer Freeze Effekt von §6" + player.getName() + "§c wurde aufgehoben." );
        }
        freezedPlayers.clear();
    }

    @Override
    public void onRightClick() {
        if ( FreezeWand.lastFreeze < System.currentTimeMillis() ) {
            getPlayer().launchProjectile( Snowball.class );
        } else {
            getPlayer().sendMessage( Devathlon.PREFIX + "§cDu musst noch " + (( int ) ((FreezeWand.lastFreeze - System.currentTimeMillis()) / 1000)) + " Sekunden warten, um diesen Stab zu benutzen!" );
        }
    }
}
