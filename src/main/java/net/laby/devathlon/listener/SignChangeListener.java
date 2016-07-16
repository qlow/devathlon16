package net.laby.devathlon.listener;

import net.laby.devathlon.Devathlon;
import net.laby.devathlon.game.Arena;
import net.laby.devathlon.utils.LocationSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Class created by qlow | Jan
 */
public class SignChangeListener implements Listener {

    @EventHandler
    public void onSignUpdate( SignChangeEvent event ) {
        Player player = event.getPlayer();

        if ( !player.hasPermission( "devathlon.sign" ) )
            return;

        if ( event.getLine( 1 ) == null || event.getLine( 0 ) == null || !event.getLine( 0 ).equals( "[mbattle]" ) )
            return;

        Arena arena = Devathlon.getInstance().getArenaManager().getArenaByName( event.getLine( 1 ) );

        if ( arena == null )
            return;

        arena.getSigns().add( event.getBlock().getLocation() );
        arena.updateSigns();

        arena.getArenaConfig().getSigns().add( LocationSerializer.toString( event.getBlock().getLocation() ) );
        Devathlon.getInstance().getArenaManager().saveConfig( arena );

        player.sendMessage( Devathlon.PREFIX + "§aSchild erstellt!" );
    }

}
