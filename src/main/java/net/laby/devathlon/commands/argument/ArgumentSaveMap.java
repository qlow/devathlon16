package net.laby.devathlon.commands.argument;

import net.laby.devathlon.Devathlon;
import net.laby.devathlon.game.Arena;
import net.laby.devathlon.game.ArenaManager;
import net.laby.devathlon.utils.command.ArgumentCommand;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;

/**
 * Class created by qlow | Jan
 */
public class ArgumentSaveMap extends ArgumentCommand {

    public ArgumentSaveMap() {
        super( "savemap" );

        ArgumentHelp.getHelpEntries().put( "/arena savemap <Map>", "Speichert die Arena im map-Ordner" );
    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        if ( args.length < 1 ) {
            sender.sendMessage( Devathlon.PREFIX + "§c/arena savemap <Map>" );
            return;
        }

        ArenaManager arenaManager = Devathlon.getInstance().getArenaManager();
        Arena arena = null;

        if ( (arena = arenaManager.getArenaByName( args[0] )) == null ) {
            sender.sendMessage( Devathlon.PREFIX + "§cMap existiert nicht!" );
            return;
        }

        Bukkit.getWorld( args[0] ).save();

        try {
            FileUtils.deleteDirectory(new File(arenaManager.getDirectory(), args[0] + "/map"));
            FileUtils.copyDirectory( new File(args[0]), new File(arenaManager.getDirectory(), args[0] + "/map") );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        sender.sendMessage( Devathlon.PREFIX + "§aMap gespeichert!" );
    }

}
