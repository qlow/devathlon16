package net.laby.devathlon;

import net.minecraft.server.v1_10_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
public class TestingClass implements Listener {

    private DevAthlon plugin;

    private Set<UUID> vehiclePlayers = new HashSet<UUID>();

    public TestingClass( DevAthlon plugin ) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents( this, plugin );

        new BukkitRunnable() {

            public void run() {
                List<UUID> removeAll = new ArrayList<UUID>();

                for ( UUID vehiclePlayer : vehiclePlayers ) {
                    Player player = Bukkit.getPlayer( vehiclePlayer );

                    if ( player == null ) {
                        removeAll.add( player.getUniqueId() );
                        continue;
                    }

                    if ( player.getVehicle() == null ) {
                        removeAll.add( player.getUniqueId() );
                        continue;
                    }

                    Entity vehicle = player.getVehicle();
                    EntityPlayer entityPlayer = (( CraftPlayer ) player).getHandle();

                    float addX = 0;
                    float addZ = 0;

                    if ( entityPlayer.bf != 0F ) {
                        float distance = entityPlayer.bf * -1;

                        addX += ( float ) (distance * Math.cos( Math.toRadians( vehicle.getLocation().getYaw() + 90 * 0 ) ));
                        addZ += ( float ) (distance * Math.sin( Math.toRadians( vehicle.getLocation().getYaw() + 90 * 0 ) ));
                    }

                    if ( addX != 0 || addZ != 0 ) {
                        Vector vector = vehicle.getLocation().getDirection();
                        vehicle.setVelocity( new Vector( vector.getX() * addX, 0D, vector.getZ() * addZ ) );
                        Bukkit.broadcastMessage( "Setting velocity" );
                    }

                    Bukkit.broadcastMessage( player.getName() + ": §cbf: " + entityPlayer.bf + " §abg: " + entityPlayer.bg );
                }

                vehiclePlayers.removeAll( removeAll );
            }
        }.runTaskTimer( plugin, 20L, 1L );
    }

    @EventHandler
    public void onInteractEntity( PlayerInteractAtEntityEvent event ) {
        if ( !(event.getRightClicked() instanceof ArmorStand) )
            return;

        event.getRightClicked().setPassenger( event.getPlayer() );
        vehiclePlayers.add( event.getPlayer().getUniqueId() );
    }

}
