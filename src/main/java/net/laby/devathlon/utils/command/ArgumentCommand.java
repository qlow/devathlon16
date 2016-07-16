package net.laby.devathlon.utils.command;

import org.bukkit.command.CommandSender;

/**
 * Class created by qlow | Jan
 */
public abstract class ArgumentCommand {

    private String argumentName;

    public ArgumentCommand( String argumentName ) {
        this.argumentName = argumentName;
    }

    /**
     * Called when the argument gets executed
     *
     * @param sender command-sender
     * @param args   arguments after this argument
     */
    public abstract void execute( CommandSender sender, String[] args );

    /**
     * Argument's name
     *
     * @return this argument's name
     */
    public String getArgumentName() {
        return argumentName;
    }
}
