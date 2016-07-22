package net.laby.devathlon.commands.argument;

import net.laby.devathlon.Devathlon;
import net.laby.devathlon.game.Arena;
import net.laby.devathlon.game.ArenaManager;
import net.laby.devathlon.utils.LocationSerializer;
import net.laby.devathlon.utils.command.ArgumentCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Class created by qlow | Jan
 */
public class ArgumentAddSpawn extends ArgumentCommand {

    public ArgumentAddSpawn() {
        super( "addspawn" );

        ArgumentHelp.getHelpEntries().put( "/arena addspawn <Name>", "Fügt einen Spawn zu einer Map hinzu" );
    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        if ( !(sender instanceof Player) )
            return;

        if ( args.length < 1 ) {
            sender.sendMessage( Devathlon.PREFIX + "§c/arena addspawn <Name>" );
            return;
        }

        ArenaManager arenaManager = Devathlon.getInstance().getArenaManager();
        Arena arena = null;

        if ( (arena = arenaManager.getArenaByName( args[0] )) == null ) {
            sender.sendMessage( Devathlon.PREFIX + "§cDiese Map existiert nicht!" );
            return;
        }

        Location loc = (( Player ) sender).getLocation();

        arena.getSpawns().add( loc );
        arena.getArenaConfig().getSpawns().add( LocationSerializer.toString( loc ) );
        arenaManager.saveConfig( arena );

        sender.sendMessage( Devathlon.PREFIX + "§aSpawn hinzugefügt!" );
    }
}
