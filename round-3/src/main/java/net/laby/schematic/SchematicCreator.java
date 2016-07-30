package net.laby.schematic;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
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
    public void selection( PlayerInteractEvent event) {
        if(event.getItem() == null || event.getItem().getType() != Material.GOLD_HOE) {
            return;
        }

        if(event.getClickedBlock() == null) {
            return;
        }

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            pos1 = event.getClickedBlock().getLocation();
            event.getPlayer().sendMessage( "§5First position set!" );
        }

        if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
            pos2 = event.getClickedBlock().getLocation();
            event.getPlayer().sendMessage( "§5Second position set!" );
        }
        event.setCancelled( true );
    }

    // Das wird wieder entfernt!!!!
    @EventHandler
    public void onCommand( PlayerCommandPreprocessEvent event) {
        if(event.getMessage().toLowerCase().startsWith( "/saveschematic" )) {
            if(pos1 != null && pos2 != null) {
                save(event.getPlayer(), pos1, pos2);
            }
            event.setCancelled( true );
        }
    }

    private void save( Player p, Location position1, Location position2) {
        double xMin = Math.min(position1.getBlockX(), position2.getBlockX());
        double xMax = Math.max(position1.getBlockX(), position2.getBlockX());
        double yMin = Math.min(position1.getBlockY(), position2.getBlockY());
        double yMax = Math.max(position1.getBlockY(), position2.getBlockY());
        double zMin = Math.min(position1.getBlockZ(), position2.getBlockZ());
        double zMax = Math.max(position1.getBlockZ(), position2.getBlockZ());

        int i = 0;

        int px = p.getLocation().getBlockX();
        int py = p.getLocation().getBlockY();
        int pz = p.getLocation().getBlockZ();

        BufferedWriter output = null;
        try {
            File file = new File("schematic.txt");
            output = new BufferedWriter(new FileWriter(file));

            for (double y = yMin; y <= yMax; y++) {
                for ( double x = xMin; x <= xMax; x++ ) {
                    for ( double z = zMin; z <= zMax; z++ ) {
                        Location loc = new Location( position1.getWorld(), x, y, z );
                        output.write("add( "+(x-px)+", "+(y-py)+", "+(z-pz)+", Material."+loc.getBlock().getType().name()+"," +
                                ""+loc.getBlock().getData()+", "+ dataToFloat(loc.getBlock().getData())+" );\n");
                        output.flush();
                        i++;
                    }
                }
            }

            output.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        p.sendMessage( "§5Saved §e" + i + " blocks!");
    }


    private float dataToFloat(byte data) {
        switch(data) {
            case 1:
                return 180;
            case 2:
                return 90;
            case 3:
                return -90;
        }
        return 0;
    }
}
