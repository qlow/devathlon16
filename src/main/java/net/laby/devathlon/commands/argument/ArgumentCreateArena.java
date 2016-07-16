package net.laby.devathlon.commands.argument;

import net.laby.devathlon.Devathlon;
import net.laby.devathlon.game.Arena;
import net.laby.devathlon.game.ArenaConfig;
import net.laby.devathlon.game.ArenaManager;
import net.laby.devathlon.utils.command.ArgumentCommand;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class created by qlow | Jan
 */
public class ArgumentCreateArena extends ArgumentCommand {

    public ArgumentCreateArena() {
        super( "create" );

        ArgumentHelp.getHelpEntries().put( "/arena create <Name>", "Erstellt eine Arena" );
    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        if ( args.length < 1 ) {
            sender.sendMessage( Devathlon.PREFIX + "§c/arena create <Name>" );
            return;
        }

        ArenaManager arenaManager = Devathlon.getInstance().getArenaManager();

        if ( arenaManager.getArenaByName( args[0] ) != null ) {
            sender.sendMessage( Devathlon.PREFIX + "§cDiese Map existiert bereits!" );
            return;
        }

        File mapFolder = new File( Bukkit.getWorldContainer(), args[0] );

        if ( !mapFolder.exists() ) {
            sender.sendMessage( Devathlon.PREFIX + "§cDiese Map existiert nicht!" );
            return;
        }

        File arenaFolder = new File( arenaManager.getDirectory(), args[0] );
        arenaFolder.mkdirs();

        ArenaConfig arenaConfig = new ArenaConfig();
        arenaConfig.setName( args[0] );

        try {
            FileUtils.copyDirectory( mapFolder, new File( arenaFolder, "map" ) );
            new File( arenaFolder, "config.json" ).createNewFile();

            Arena arena = new Arena( arenaConfig, new File( arenaFolder, "map" ), args[0], new ArrayList<>(), new ArrayList<>() );

            arenaManager.saveConfig( arena );
            arenaManager.getArenas().add( arena );

            sender.sendMessage( Devathlon.PREFIX + "§aMap erstellt!!" );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

    }
}
