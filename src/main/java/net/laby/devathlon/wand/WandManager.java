package net.laby.devathlon.wand;

import net.laby.devathlon.Devathlon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
public class WandManager implements Listener {

    private static Wand[] wands = new Wand[]{
            new FireWand(  ),
            new PortalWand(  )
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
    public void onJoin( PlayerJoinEvent event ) {
        Player player = event.getPlayer();

        for ( Wand wand : wands ) {
            player.getInventory().addItem( wand.getItem() );
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

        if(wand == null) {
            return;
        }

        if(event.getAction().name().startsWith( "LEFT" )) {
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

}
