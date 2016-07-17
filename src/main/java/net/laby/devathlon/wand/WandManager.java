package net.laby.devathlon.wand;

import net.laby.devathlon.Devathlon;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
public class WandManager implements Listener {

    private static Wand[] wands = new Wand[]{
            new FireWand(),
            new PortalWand(),
            new CreeperWand(),
            new BoostWand(),
            new FreezeWand(),
            new InvasionWand()
    };

    private Map<UUID, Wand> playerWands = new HashMap<>();
    private Devathlon devathlon;

    public WandManager( Devathlon devathlon ) {
        // Registering events
        Bukkit.getPluginManager().registerEvents( this, devathlon );

        // Running task for calling release-methods

        Bukkit.getScheduler().runTaskTimer( devathlon, new Runnable() {
            @Override
            public void run() {
                for ( Map.Entry<UUID, Wand> playerWandEntry : playerWands.entrySet() ) {
                    Wand wand = playerWandEntry.getValue();

                    wand.onTick();

                    if ( !wand.isRightClicking() )
                        continue;

                    if ( (System.currentTimeMillis() - wand.getLastInteract()) <= 100 )
                        continue;

                    wand.onRightClickRelease();
                    wand.setRightClicking( false );
                }
            }
        }, 1L, 1L );
    }

    @EventHandler
    public void onPlayerInteractEntity( PlayerInteractEntityEvent event ) {
        Entity entity = event.getRightClicked();
        if ( entity != null ) {
            Wand wand = getWandByItemStack( event.getPlayer(), event.getPlayer().getItemInHand() );
            if ( wand != null ) {
                wand.onPlayerInteractEntity( entity );
            }
        }
    }

    @EventHandler
    public void onHeldItem( PlayerItemHeldEvent event ) {
        Player player = event.getPlayer();

        ItemStack oldItem = player.getInventory().getItem( event.getPreviousSlot() );
        ItemStack newItem = player.getInventory().getItem( event.getNewSlot() );

        Wand oldWand = null;
        Wand newWand = null;

        if ( oldItem != null && (oldWand = getWandByItemStack( player, oldItem )) != null ) {
            playerWands.remove( player.getUniqueId() );
            oldWand.onDisable();
        }

        if ( newItem != null && (newWand = getWandByItemStack( player, newItem )) != null ) {
            newWand.onEnable();
        }
    }

    @EventHandler
    public void onInteract( PlayerInteractEvent event ) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if ( event.getAction() == Action.PHYSICAL )
            return;

        if ( item == null )
            return;

        Wand wand = getWandByItemStack( player, item );

        if ( wand == null ) {
            return;
        }

        event.setCancelled( true );

        wand.onBlockInteract( event );

        if ( event.getAction().name().startsWith( "LEFT" ) ) {
            wand.onLeftClick();
            return;
        }

        if ( wand.isRightClicking() ) {
            wand.setLastInteract( System.currentTimeMillis() );
            return;
        }

        wand.onRightClick();
        wand.setRightClicking( true );
        wand.setLastInteract( System.currentTimeMillis() );
    }

    @EventHandler
    public void onEntityDamageByEntity( EntityDamageByEntityEvent event ) {
        if ( !(event.getEntity() instanceof Player) )
            return;

        if ( !(event.getDamager() instanceof Snowball) )
            return;

        if ( !((( Snowball ) event.getDamager()).getShooter() instanceof Player) )
            return;

        Player player = ( Player ) event.getEntity();
        Player shooter = ( Player ) (( Snowball ) event.getDamager()).getShooter();

        if ( !playerWands.containsKey( shooter.getUniqueId() ) )
            return;

        Wand wand = null;

        if ( !((wand = playerWands.get( shooter.getUniqueId() )) instanceof FreezeWand) )
            return;

        FreezeWand freezeWand = ( FreezeWand ) wand;

        if ( FreezeWand.lastFreeze < System.currentTimeMillis() && !freezeWand.getFreezedPlayers().containsKey( player.getUniqueId() ) ) {
            FreezeWand.lastFreeze = System.currentTimeMillis() + 1000 * 30;
            player.setWalkSpeed( 0.0f );
            freezeWand.getFreezedPlayers().put( player.getUniqueId(), System.currentTimeMillis() );
            player.addPotionEffect( new PotionEffect( PotionEffectType.JUMP, 10000, 128 ) );
            player.getWorld().playSound( player.getLocation(), Sound.ENTITY_ENDERDRAGON_DEATH, 5, 1 );
        } else {
            shooter.sendMessage( Devathlon.PREFIX + "§cDu musst noch " + (( int ) ((FreezeWand.lastFreeze - System.currentTimeMillis()) / 1000)) + " Sekunden warten, um diesen Stab zu benutzen!" );
        }
    }

    /**
     * Gets a wand by the given itemstack
     *
     * @param player    the wand should be for
     * @param itemStack itemstack
     * @return wand (null if there is no wand for this item)
     */
    private Wand getWandByItemStack( Player player, ItemStack itemStack ) {
        Wand playersWand = null;

        if ( playerWands.containsKey( player.getUniqueId() ) && (playersWand = playerWands.get( player.getUniqueId() )).getItem().equals( itemStack ) ) {
            return playersWand;
        }

        for ( Wand wand : wands ) {
            if ( !wand.getItem().equals( itemStack ) )
                continue;

            try {
                Wand returnedWand = wand.getClass().getConstructor( Player.class ).newInstance( player );

                playerWands.put( player.getUniqueId(), returnedWand );
                return returnedWand;
            } catch ( Exception ex ) {
                // THIS SHOULDN'T HAPPEN!
                ex.printStackTrace();
                break;
            }
        }

        return null;
    }

    public void giveAllWands( Player player ) {
        int slotIndex = 2;

        for ( Wand wand : wands ) {
            player.getInventory().setItem( slotIndex++, wand.getItem() );
        }

        player.getInventory().setHeldItemSlot( 0 );
    }

}
