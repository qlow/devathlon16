package net.laby.devathlon.commands.argument;

import net.laby.devathlon.Devathlon;
import net.laby.devathlon.utils.command.ArgumentCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Class created by qlow | Jan
 */
public class ArgumentTeleportToMap extends ArgumentCommand {

    public ArgumentTeleportToMap() {
        super( "tptomap" );

        ArgumentHelp.getHelpEntries().put( "/arena tptomap <Welt>", "Teleportiert dich zur Welt" );
    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        if ( !(sender instanceof Player) )
            return;

        if ( args.length < 1 ) {
            sender.sendMessage( Devathlon.PREFIX + "§c/arena tptomap <Welt>" );
            return;
        }

        if ( Bukkit.getWorld( args[0] ) == null ) {
            sender.sendMessage( Devathlon.PREFIX + "§cDiese Welt existiert nicht!" );
            return;
        }

        (( Player ) sender).teleport( Bukkit.getWorld( args[0] ).getSpawnLocation() );
    }

}
