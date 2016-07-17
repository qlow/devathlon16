package net.laby.devathlon.wand;

import net.laby.devathlon.Devathlon;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
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
        super( player, Material.TIPPED_ARROW, 0, "§bFreeze Wand", "" );
    }

    public FreezeWand( ) {
        this( null );
    }

    @Override
    public void onTick( ) {
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
    public void onDisable( ) {
        for ( UUID uuid : freezedPlayers.keySet() ) {
            Player player = Bukkit.getPlayer( uuid );
            if ( player == null ) {
                continue;
            }
            player.setWalkSpeed( 0.2f );
            player.removePotionEffect( PotionEffectType.JUMP );
            getPlayer().sendMessage( Devathlon.PREFIX + "Der Freeze Effekt von §6" + player.getName() + "§r wurde aufgehoben." );
        }
        freezedPlayers.clear();
    }

    @Override
    public void onPlayerInteractEntity( Entity entity ) {
        if ( entity instanceof Player ) {
            if ( lastFreeze < System.currentTimeMillis() && !freezedPlayers.containsKey( entity.getUniqueId() ) ) {
                lastFreeze = System.currentTimeMillis() + 1000 * 30;
                ( ( Player ) entity ).setWalkSpeed( 0.0f );
                freezedPlayers.put( entity.getUniqueId(), System.currentTimeMillis() );
                ( ( Player ) entity ).addPotionEffect( new PotionEffect( PotionEffectType.JUMP, 10000, 128 ) );
                entity.getWorld().playSound( entity.getLocation(), Sound.ENTITY_ENDERDRAGON_DEATH, 5, 1 );
            } else {
                getPlayer().sendMessage( Devathlon.PREFIX + "Dieser Zauberstab hat noch " + ( ( int ) ( ( lastFreeze - System.currentTimeMillis() ) / 1000 ) ) + " Sekunden cooldown!" );
            }
        }
    }
}
