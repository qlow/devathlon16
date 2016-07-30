package net.laby.ship;

import net.laby.devathlon.DevAthlon;
import net.minecraft.server.v1_10_R1.EntityArmorStand;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Class created by qlow | Jan
 */
public class ShipModel {

    private final static double MAX_SPEED = 1D;
    private final static double MAX_BACKWARDS_SPEED = 0.5D;
    private final static double SPEED = 0.006;

    private Player player;

    private ArmorStand armorStand;
    private ArmorStand seatStand;
    private ArmorStand movilityStand;

    public ShipModel( final ArmorStand armorStand, final ArmorStand seatStand, final ArmorStand movilityStand, Player player ) {
        this.armorStand = armorStand;
        this.seatStand = seatStand;
        this.movilityStand = movilityStand;

        this.player = player;

        new BukkitRunnable() {

            private double addX;
            private double addZ;

            private Location lastLocation = armorStand.getLocation();
            private int stayTicks;

            @Override
            public void run() {
                if ( seatStand.getPassenger() == null ) {
                    dismount();
                    return;
                }

                if ( !player.isOnline() ) {
                    dismount();
                    return;
                }

                if ( lastLocation.distance( armorStand.getLocation() ) == 0 ) {
                    if ( ++stayTicks > 3 ) {
                        this.addX = 0;
                        this.addZ = 0;
                    }
                } else {
                    lastLocation = armorStand.getLocation();
                    stayTicks = 0;
                }

                EntityPlayer entityPlayer = (( CraftPlayer ) player).getHandle();

                double sideMotion = entityPlayer.bf;
                double forwardMotion = entityPlayer.bg;

                double yVelocity = armorStand.getVelocity().getY();

                final Material type = armorStand.getLocation().clone().add(0.0, 0.3, 0.0).getBlock().getType();

                if(type == Material.WALL_BANNER || type == Material.STATIONARY_WATER) {
                    yVelocity = 0.01;
                }

                // TODO: Check whether the block of the armorstand-location is water

                final Vector direction = armorStand.getLocation().getDirection();

                // Calculating the velocity-things
                if ( forwardMotion > 0 ) {
                    // Calculating if the player drives forwards
                    final double x = direction.getX();
                    final double z = direction.getZ();

                    if ( x * this.addX < MAX_SPEED && z * this.addZ < MAX_SPEED && x * this.addX > -MAX_SPEED && z * this.addZ > -MAX_SPEED ) {
                        this.addX += SPEED;
                        this.addZ += SPEED;
                    }
                } else {
                    // Calculating if the player doesn't drive or he drives backwards
                    if ( this.addX > 0.0 ) {
                        this.addX -= 0.007;

                        // Backwards driving
                        if ( forwardMotion < 0.0 ) {
                            this.addX -= 0.01;
                        }
                    }

                    if ( this.addX < 0.0 ) {
                        this.addX = 0.0;
                    }

                    if ( this.addZ > 0.0 ) {
                        this.addZ -= 0.007;

                        // Backwards driving
                        if ( forwardMotion < 0.0 ) {
                            this.addZ -= 0.01;
                        }
                    }

                    if ( this.addZ < 0.0 ) {
                        this.addZ = 0.0;
                    }
                }

                // Setting velocity of armorstand
                armorStand.setVelocity( new Vector( direction.getX() * this.addX, yVelocity, direction.getZ() * this.addZ ) );

                // Getting handles of main-armorstand & seatstand
                EntityArmorStand entityArmorStand = (( CraftArmorStand ) armorStand).getHandle();
                EntityArmorStand entitySeatStand = (( CraftArmorStand ) seatStand).getHandle();

                // Calculating new yaw by side motion
                float newYaw = ( float ) (entityArmorStand.yaw - (sideMotion * 3));

                // Setting yaw
                entityArmorStand.yaw = newYaw;
                entitySeatStand.yaw = newYaw;

                // Setting locations
                Location location = armorStand.getLocation();

                ((CraftArmorStand) movilityStand).getHandle().setLocation(
                        location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch() );
                entitySeatStand.setLocation( location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch() );

            }

        }.runTaskTimer( DevAthlon.getInstance(), 1L, 1L );
    }

    private void dismount() {
        this.player = null;

        setGravity( movilityStand, false );
        setGravity( armorStand, false );
    }

    private static void setGravity( ArmorStand armorStand, boolean gravity ) {
        armorStand.setGravity( gravity );
        ((CraftArmorStand) armorStand).getHandle().noclip = !gravity;
    }

    public static ShipModel spawnShipModel( Location location, Player player ) {
        ArmorStand mainStand = ( ArmorStand ) location.getWorld().spawnEntity( location, EntityType.ARMOR_STAND );
        setGravity( mainStand, true );
        //mainStand.setVisible( false );

        ArmorStand movilityStand = ( ArmorStand ) location.getWorld().spawnEntity( location, EntityType.ARMOR_STAND );
        setGravity( movilityStand, false );
        //movilityStand.setVisible( false );
        movilityStand.setCustomName( "ShipPart;" + mainStand.getUniqueId() );

        ArmorStand seatStand = ( ArmorStand ) location.getWorld().spawnEntity( location, EntityType.ARMOR_STAND );
        setGravity( seatStand, false );
        //seatStand.setVisible( false );
        seatStand.setCustomName( "ShipPart;" + mainStand.getUniqueId() );

        // Setting passenger
        movilityStand.setPassenger( seatStand );

        // Setting name of main-stand
        mainStand.setCustomName( "Ship;" + movilityStand.getUniqueId() + ";" + seatStand.getUniqueId() );

        // Setting player-passenger
        seatStand.setPassenger( player );

        return new ShipModel( mainStand, seatStand, movilityStand, player );
    }

}
