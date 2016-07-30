package net.laby.game.command;

import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.laby.devathlon.DevAthlon;
import net.laby.game.Game;
import net.laby.game.GameRegion;
import net.laby.game.config.GameConfig;
import net.laby.game.config.SimpleLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Class created by qlow | Jan
 */
public class GameCommand implements CommandExecutor {

    public GameCommand() {
        DevAthlon.getInstance().getCommand( "game" ).setExecutor( this );
    }

    @Override
    public boolean onCommand( CommandSender commandSender, Command command, String label, String[] args ) {
        // Disallowing console-senders
        if ( !(commandSender instanceof Player) )
            return true;

        // Disallowing players without permissions
        if ( !commandSender.hasPermission( "game.setup" ) ) {
            commandSender.sendMessage( "§cKeine Rechte!" );
            return true;
        }

        // Checking for invalid args-length
        if ( args.length < 1 ) {
            sendHelp( commandSender );
            return true;
        }

        GameConfig config = Game.getGame().getConfig();

        Player player = ( Player ) commandSender;

        // /game setlobby
        if ( args[0].equalsIgnoreCase( "setlobby" ) ) {
            config.setLobbySpawn( SimpleLocation.toSimpleLocation( player.getLocation() ) );
            saveConfig();

            player.sendMessage( "§aLobby-Spawn gesetzt!" );
            return true;
        }

        // /game setregionpoint <1/2>
        if ( args[0].equalsIgnoreCase( "setregionpoint" ) ) {
            if ( args.length < 2 || (!args[1].equals( "1" ) && !args[1].equals( "2" )) ) {
                player.sendMessage( "§c/game setregionpoint <1/2>" );
                return true;
            }

            SimpleLocation location = SimpleLocation.toSimpleLocation( player.getLocation() );

            if ( args[1].equals( "1" ) ) {
                config.setGameRegionFirstPoint( location );
            } else {
                config.setGameRegionSecondPoint( location );
            }

            saveConfig();

            Game.getGame().setRegion(new GameRegion( config.getGameRegionFirstPoint().getLocation(),
                    config.getGameRegionSecondPoint().getLocation() ));

            player.sendMessage( "§aRegion-Point gesetzt!" );
            return true;
        }

        // /game setwatery
        if ( args[0].equalsIgnoreCase( "setwatery" ) ) {
            int waterY = 0;
            boolean numberic = false;

            if ( args.length > 1 ) {
                try {
                    waterY = Integer.parseInt( args[1] );
                    numberic = true;
                } catch ( NumberFormatException ex ) {
                }
            }

            if ( !numberic || args.length < 2 || waterY < 0 ) {
                player.sendMessage( "§c/game setwatery <Y-Position>" );
                return true;
            }

            config.setHighestWaterY( waterY );
            saveConfig();

            player.sendMessage( "§aWater-Y gesetzt!" );
            return true;
        }

        // /game setsign
        if ( args[0].equalsIgnoreCase( "setsign" ) ) {
            Block targetBlock = player.getTargetBlock( (Set<Material> ) null, 5 );

            if(targetBlock == null || !(targetBlock.getState() instanceof Sign)) {
                player.sendMessage( "§cDu musst auf ein Schild zeigen!" );
                return true;
            }

            config.setGameJoinSign( SimpleLocation.toSimpleLocation( targetBlock.getLocation() ) );
            saveConfig();

            player.sendMessage( "§aSign gesetzt!" );
            return true;
        }

        sendHelp( commandSender );
        return true;
    }

    private void saveConfig() {
        try {
            Game.getGame().getConfig().save();
        } catch ( InvalidConfigurationException e ) {
            e.printStackTrace();
        }
    }

    private void sendHelp( CommandSender sender ) {
        sender.sendMessage( "§7/game setlobby -> Setzt die Lobby" );
        sender.sendMessage( "§7/game setregionpoint <1/2> -> Setzt die Region der Arena" );
        sender.sendMessage( "§7/game setwatery <Y-Position> -> Setzt die Wasser-Höhe" );
        sender.sendMessage( "§7/game setsign -> Setzt das Join-Schild, auf das du zeigst" );
    }

}
