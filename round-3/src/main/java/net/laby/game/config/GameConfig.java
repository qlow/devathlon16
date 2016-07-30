package net.laby.game.config;

import lombok.Getter;
import lombok.Setter;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.Yamler.Config.YamlConfig;
import net.laby.devathlon.DevAthlon;

import java.io.File;

/**
 * Class created by qlow | Jan
 */
@Getter
@Setter
public class GameConfig extends YamlConfig {

    public GameConfig() {
        CONFIG_FILE = new File( DevAthlon.getInstance().getDataFolder(), "config.yml" );

        try {
            init();
        } catch ( InvalidConfigurationException e ) {
            e.printStackTrace();
        }
    }

    @Comment( "The lobby's spawn" )
    private SimpleLocation lobbySpawn = new SimpleLocation();

    @Comment( "The game's region's points (In this area the ships will spawn)" )
    private SimpleLocation gameRegionFirstPoint, gameRegionSecondPoint = new SimpleLocation();

    @Comment( "The highest water position for spawning the ships" )
    private int highestWaterY = 60;

    @Comment( "The game-join sign" )
    private SimpleLocation gameJoinSign = new SimpleLocation();

}
