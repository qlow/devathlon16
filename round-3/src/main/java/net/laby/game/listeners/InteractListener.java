package net.laby.game.listeners;

import net.laby.game.Game;
import net.laby.game.GamePlayer;
import net.laby.game.GameRegion;
import net.laby.game.Level;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Class created by qlow | Jan
 */
public class InteractListener implements Listener {

    @EventHandler
    public void onInteract( PlayerInteractEvent event ) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = GamePlayer.getPlayer( player.getUniqueId() );

        if ( gamePlayer.isIngame() )
            return;

        if ( !Game.getGame().getRegion().isValid() )
            return;

        if ( !(event.getClickedBlock().getState() instanceof Sign) )
            return;

        Location signLocation = Game.getGame().getConfig().getGameJoinSign().getLocation();

        if ( signLocation == null )
            return;

        if ( !signLocation.equals( event.getClickedBlock().getLocation() ) )
            return;

        // Setting some values
        gamePlayer.setIngame( true );
        gamePlayer.setLevel( 0 );
        gamePlayer.setKillStreak( 0 );

        // Updating scoreboard
        Game.getGame().getGameScoreboardManager().updateScoreboard( player );

        // Clearing player's inventory
        player.getInventory().clear();

        // Teleporting player
        GameRegion region = Game.getGame().getRegion();

        player.teleport( new Location( Game.getGame().getConfig().getGameRegionFirstPoint().getLocation().getWorld(),
                region.getRandomX(), Game.getGame().getConfig().getHighestWaterY() + 5, region.getRandomZ() ) );

        // Constructing ship
        try {
            Level.values()[0].getShipModel().getConstructor( Player.class ).newInstance( player );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
