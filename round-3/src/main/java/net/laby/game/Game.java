package net.laby.game;

import lombok.Getter;
import lombok.Setter;
import net.laby.devathlon.DevAthlon;
import net.laby.game.command.GameCommand;
import net.laby.game.config.GameConfig;
import net.laby.game.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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

        // Registering listeners
        Listener[] listeners = new Listener[]{
                new DamageListener(),
                new FoodLevelChangeListener(),
                new InteractListener(),
                new JoinListener(),
                new QuitListener(),
                new EntityExplodeListener(),
                new PlayerBlockListener(),
                new InventoryListener()
        };

        for ( Listener listener : listeners ) {
            Bukkit.getPluginManager().registerEvents( listener, DevAthlon.getInstance() );
        }

        // Calling PlayerJoinEvent for each player
        for ( Player players : Bukkit.getOnlinePlayers() ) {
            Bukkit.getPluginManager().callEvent( new PlayerJoinEvent( players, null ) );
        }
    }

}
