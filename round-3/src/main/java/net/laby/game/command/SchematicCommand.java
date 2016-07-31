package net.laby.game.command;

import net.laby.devathlon.DevAthlon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Class created by LabyStudio
 */
public class SchematicCommand implements CommandExecutor {

    public SchematicCommand( ) {
        DevAthlon.getInstance().getCommand( "shipmatic" ).setExecutor( this );
    }

    @Override
    public boolean onCommand( CommandSender commandSender, Command command, String label, String[] args ) {
        // Disallowing console-senders
        if ( !( commandSender instanceof Player ) )
            return true;

        // Disallowing players without permissions
        if ( !commandSender.hasPermission( "game.schematic" ) ) {
            commandSender.sendMessage( "§cKeine Rechte!" );
            return true;
        }

        Player p = ( Player ) commandSender;

        if ( args.length <= 1 ) {
            p.sendMessage( "§c/shipmatic save <name>" );
            return true;
        }

        String action = args[ 0 ];
        if ( action.equalsIgnoreCase( "save" ) ) {
            String name = args[ 1 ];
            DevAthlon.getInstance().getSchematicCreator().createSchematic( p, name );
        } else {
            p.sendMessage( "§cUnknown action: " + action );
        }
        return true;
    }
}
