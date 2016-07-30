package net.laby.game;

import lombok.Getter;
import lombok.Setter;
import net.laby.devathlon.DevAthlon;
import net.laby.game.command.GameCommand;
import net.laby.game.config.GameConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Class created by qlow | Jan
 */
public class Game implements Listener {

    @Getter
    private static Game game;

    @Getter
    private GameConfig config;

    @Getter
    private GameScoreboardManager gameScoreboardManager;

    @Getter
    @Setter
    private GameRegion region;

    public Game() {
        game = this;

        // Registering /game-command
        new GameCommand();

        // Loading config
        this.config = new GameConfig();

        // Setting region
        region = new GameRegion( config.getGameRegionFirstPoint().getLocation(),
                config.getGameRegionSecondPoint().getLocation() );

        // Loading scoreboard-manager
        this.gameScoreboardManager = new GameScoreboardManager();

        Bukkit.getPluginManager().registerEvents( this, DevAthlon.getInstance() );

        // Calling PlayerJoinEvent for each player
        for ( Player players : Bukkit.getOnlinePlayers() ) {
            Bukkit.getPluginManager().callEvent( new PlayerJoinEvent( players, null ) );
        }
    }

    @EventHandler
    public void onJoin( PlayerJoinEvent event ) {
        Player player = event.getPlayer();

        // Setting main-scoreboard
        player.setScoreboard( Bukkit.getScoreboardManager().getMainScoreboard() );

        Location lobbySpawn = config.getLobbySpawn().getLocationAtMid();

        if ( lobbySpawn != null ) {
            // Teleporting player to lobby-spawn
            player.teleport( lobbySpawn );
        }

        // Putting GamePlayer into GamePlayer-map
        GamePlayer.getPlayers().put( player.getUniqueId(), new GamePlayer( player.getUniqueId() ) );
    }

    @EventHandler
    public void onQuit( PlayerQuitEvent event ) {
        GamePlayer.getPlayers().remove( event.getPlayer().getUniqueId() );
    }

    @EventHandler
    public void onInteract( PlayerInteractEvent event ) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = GamePlayer.getPlayer( player.getUniqueId() );

        Bukkit.broadcastMessage( "1" );
        if ( gamePlayer.isIngame() )
            return;

        Bukkit.broadcastMessage( "2" );
        if ( !region.isValid() )
            return;

        Bukkit.broadcastMessage( "3" );
        if ( !(event.getClickedBlock().getState() instanceof Sign) )
            return;

        Bukkit.broadcastMessage( "4" );
        Location signLocation = getConfig().getGameJoinSign().getLocation();

        if ( signLocation == null )
            return;

        Bukkit.broadcastMessage( "5" );
        if ( signLocation != event.getClickedBlock().getLocation() )
            return;

        Bukkit.broadcastMessage( "6" );
        // Setting some values
        gamePlayer.setIngame( true );
        gamePlayer.setLevel( 0 );
        gamePlayer.setKillStreak( 0 );

        // Updating scoreboard
        gameScoreboardManager.updateScoreboard( player );

        // Teleporting player
        player.teleport( new Location( config.getGameRegionFirstPoint().getLocation().getWorld(),
                region.getRandomX(), config.getHighestWaterY() + 2, region.getRandomZ() ) );

        // Constructing ship
        try {
            Level.values()[0].getShipModel().getConstructor( Player.class ).newInstance( player );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onDamage( EntityDamageEvent event ) {
        event.setCancelled( true );
    }

    @EventHandler
    public void onFoodLevelChange( FoodLevelChangeEvent event ) {
        event.setCancelled( true );
    }

}
