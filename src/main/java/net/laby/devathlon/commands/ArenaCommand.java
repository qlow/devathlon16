package net.laby.devathlon.commands;

import net.laby.devathlon.Devathlon;
import net.laby.devathlon.commands.argument.ArgumentAddSpawn;
import net.laby.devathlon.commands.argument.ArgumentCreateArena;
import net.laby.devathlon.commands.argument.ArgumentHelp;
import net.laby.devathlon.utils.command.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * Class created by qlow | Jan
 */
public class ArenaCommand extends BaseCommand {

    public ArenaCommand() {
        super( "arena", new ArgumentHelp(), new ArgumentCreateArena(), new ArgumentAddSpawn() );
    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        PluginDescriptionFile pluginDescriptionFile = Devathlon.getInstance().getDescription();
        sender.sendMessage( Devathlon.PREFIX + "§7" +  pluginDescriptionFile.getName() + " §bv" + pluginDescriptionFile.getVersion());
        sender.sendMessage( Devathlon.PREFIX + "§7/arena help for help" );
        sender.sendMessage( Devathlon.PREFIX + "§aby qlow & LabyStudio" );
    }

    @Override
    public boolean executeBeforeArgumentsCheck( CommandSender sender, String[] args ) {
        // Permission-check
        if(!sender.hasPermission( "devathlon.arena" )) {
            sender.sendMessage( Devathlon.PREFIX + "§cDu hast keine Rechte dazu!" );
            return true;
        }

        return false;
    }
}
