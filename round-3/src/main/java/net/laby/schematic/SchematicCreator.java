package net.laby.schematic;

import net.laby.devathlon.DevAthlon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class created by LabyStudio
 */
public class SchematicCreator implements Listener {

    private Location pos1 = null;
    private Location pos2 = null;

    @EventHandler
    public void selection( PlayerInteractEvent event ) {
        if ( event.getItem() == null || event.getItem().getType() != Material.GOLD_HOE ) {
            return;
        }
        if ( event.getClickedBlock() == null ) {
            return;
        }
        if ( event.getAction() == Action.RIGHT_CLICK_BLOCK ) {
            pos1 = event.getClickedBlock().getLocation();
            event.getPlayer().sendMessage( "§5First position set!" );
        }
        if ( event.getAction() == Action.LEFT_CLICK_BLOCK ) {
            pos2 = event.getClickedBlock().getLocation();
            event.getPlayer().sendMessage( "§5Second position set!" );
        }
        event.setCancelled( true );
    }

    private void save( Player p, Location position1, Location position2, String name ) {
        double xMin = Math.min( position1.getBlockX(), position2.getBlockX() );
        double xMax = Math.max( position1.getBlockX(), position2.getBlockX() );
        double yMin = Math.min( position1.getBlockY(), position2.getBlockY() );
        double yMax = Math.max( position1.getBlockY(), position2.getBlockY() );
        double zMin = Math.min( position1.getBlockZ(), position2.getBlockZ() );
        double zMax = Math.max( position1.getBlockZ(), position2.getBlockZ() );

        int amount = 0;

        int px = p.getLocation().getBlockX();
        int py = p.getLocation().getBlockY();
        int pz = p.getLocation().getBlockZ();

        // Fixed y position;
        py = (int) yMin + 1;

        BufferedWriter bufferedWriter = null;
        try {
            File file = new File( DevAthlon.SCHEMATIC_FOLDER, name + ".schem" );
            bufferedWriter = new BufferedWriter( new FileWriter( file ) );
            for ( double y = yMin; y <= yMax; y++ ) {
                for ( double x = xMin; x <= xMax; x++ ) {
                    for ( double z = zMin; z <= zMax; z++ ) {
                        Location loc = new Location( position1.getWorld(), x, y, z );
                        if(loc.getBlock().getType() == Material.AIR) {
                            continue;
                        }
                        String line = ( int ) ( x - px ) + ";" + ( int ) ( y - py ) + ";" + ( int ) ( z - pz ) + ";"
                                + Utils.dataToYaw( loc.getBlock().getData() )
                                + ";" + loc.getBlock().getType().name()
                                + ";" + loc.getBlock().getData() + "\n";
                        bufferedWriter.write( line );
                        bufferedWriter.flush();
                        amount++;
                    }
                }
            }
            bufferedWriter.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        p.sendMessage( "§5Saved §e" + amount + "§5 blocks as §e" + name + "§5!" );
    }


    public void createSchematic( Player p, String name ) {
        if ( pos1 != null && pos2 != null ) {
            save( p, pos1, pos2, name );
        } else {
            p.sendMessage( "§cNo selection found! (Create a selection with a gold hoe)" );
        }
    }
}
