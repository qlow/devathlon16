package net.laby.devathlon;

import net.minecraft.server.v1_10_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
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
                for ( UUID vehiclePlayer : vehiclePlayers ) {
                    Player player = Bukkit.getPlayer( vehiclePlayer );

                    if ( player == null )
                        continue;

                    EntityPlayer entityPlayer = (( CraftPlayer ) player).getHandle();

                    Bukkit.broadcastMessage( player.getName() + ": §cbf: " + entityPlayer.bf + "§abg: " + entityPlayer.bg );
                }
            }
        }.runTaskTimer( plugin, 20L, 5L );
    }

    @EventHandler
    public void onVehicleEnter( VehicleEnterEvent event ) {
        if ( !(event.getEntered() instanceof Player) )
            return;

        vehiclePlayers.add( event.getEntered().getUniqueId() );
    }

    @EventHandler
    public void onVehicleExit( VehicleExitEvent event ) {
        if ( !(event.getExited() instanceof Player) )
            return;

        vehiclePlayers.remove( event.getExited().getUniqueId() );
    }

}
