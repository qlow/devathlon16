package net.laby.devathlon.listener;

import net.laby.devathlon.Devathlon;
import net.laby.devathlon.game.Arena;
import net.laby.devathlon.utils.LocationSerializer;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Class created by qlow | Jan
 */
public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        if ( !(event.getBlock().getState() instanceof Sign) )
            return;

        // Checking for an arena-sign & destroying
        for ( Arena arena : Devathlon.getInstance().getArenaManager().getArenas() ) {
            if ( !arena.getSigns().contains( event.getBlock().getLocation() ) )
                continue;

            if ( !event.getPlayer().hasPermission( "devathlon.sign" ) ) {
                event.setCancelled( true );
                return;
            }

            arena.getSigns().remove( event.getBlock().getLocation() );

            arena.getArenaConfig().getSigns().remove( LocationSerializer.toString( event.getBlock().getLocation() ) );
            Devathlon.getInstance().getArenaManager().saveConfig( arena );

            event.getPlayer().sendMessage( Devathlon.PREFIX + "§aSchild entfernt!" );
            return;
        }
    }

}
