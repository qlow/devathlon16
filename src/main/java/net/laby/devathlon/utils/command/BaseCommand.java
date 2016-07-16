package net.laby.devathlon.utils.command;

import net.laby.devathlon.Devathlon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Class created by qlow | Jan
 */
public abstract class BaseCommand implements CommandExecutor {

    private ArgumentCommand[] arguments;

    public BaseCommand( String command, ArgumentCommand... arguments ) {
        this.arguments = arguments;

        Devathlon.getInstance().getCommand( command ).setExecutor( this );
    }

    @Override
    public boolean onCommand( CommandSender commandSender, Command command, String label, String[] args ) {
        if ( executeBeforeArgumentsCheck( commandSender, args ) ) {
            return true;
        }

        // Checking for arguments
        if ( args.length != 0 ) {
            // Iterating through given arguments in constructor
            for ( ArgumentCommand argumentCommand : arguments ) {
                if ( !argumentCommand.getArgumentName().equalsIgnoreCase( args[0] ) )
                    continue;

                String[] newArguments = new String[args.length - 1];
                System.arraycopy( args, 1, newArguments, 0, args.length - 1 );

                // Executing ArgumentCommand
                argumentCommand.execute( commandSender, newArguments );
            }
        }

        execute( commandSender, args );
        return true;
    }

    /**
     * Executed before the arguments-check (for example for permissions-check)
     *
     * @param sender command-sender
     * @param args   arguments of command-execution
     * @return true if onCommand should return
     */
    public boolean executeBeforeArgumentsCheck( CommandSender sender, String[] args ) {
        return false;
    }

    /**
     * Executed on command
     *
     * @param sender command-sender
     * @param args   arguments of command-execution
     */
    public abstract void execute( CommandSender sender, String[] args );

}
