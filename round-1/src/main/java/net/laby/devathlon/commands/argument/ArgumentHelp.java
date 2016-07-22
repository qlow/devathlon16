package net.laby.devathlon.commands.argument;

import net.laby.devathlon.Devathlon;
import net.laby.devathlon.utils.command.ArgumentCommand;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

/**
 * Class created by qlow | Jan
 */
public class ArgumentHelp extends ArgumentCommand {

    public ArgumentHelp() {
        super( "help" );
    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        sender.sendMessage( Devathlon.PREFIX + "§7Alle Befehle:" );

        for ( Map.Entry<String, String> commandEntry : helpEntries.entrySet() ) {
            sender.sendMessage( Devathlon.PREFIX + "§a" + commandEntry.getKey() + " §b\u00BB §7" + commandEntry.getValue() );
        }
    }

    /**
     * Help-entries of /arena
     * <p>
     * key = command
     * value = description
     * </p>
     *
     * @return map with help-entries
     */
    public static Map<String, String> getHelpEntries() {
        return helpEntries;
    }

    private static Map<String, String> helpEntries = new HashMap<>();

}
