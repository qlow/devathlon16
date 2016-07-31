package net.laby.ship;

import net.laby.devathlon.DevAthlon;
import net.laby.game.GamePlayer;
import net.laby.schematic.ModelPart;
import net.laby.schematic.ShipModel;
import net.minecraft.server.v1_10_R1.EntityArmorStand;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/**
 * Class created by LabyStudio
 */
public abstract class Ship {

    private Player player;
    private ShipModel shipModel;
    protected ArrayList<ArmorStandBlock> blocksArmorStand = new ArrayList<ArmorStandBlock>();
    protected ArmorStand mainArmorStand;
    protected ArmorStand mainSeatStand;
    protected ArmorStand mainMovilityStand;
    protected ArmorStand mainHologram;

    protected double maxSpeed = 0.2D;
    protected double speed = 0.006;

    public Ship( Player player, String modelName ) {

        player.addPotionEffect( new PotionEffect( PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1 ) );

        this.player = player;

        if ( DevAthlon.getInstance().getSchematicModels().containsKey( modelName ) ) {
            this.shipModel = DevAthlon.getInstance().getSchematicModels().get( modelName );
        }

        spawnMainArmorStands( player.getLocation(), player );
        if ( this.shipModel != null ) {
            buildModel();
        }

        new BukkitRunnable() {

            private double addX;
            private double addZ;

            private Location lastLocation = mainArmorStand.getLocation();
            private int stayTicks;

            @Override
            public void run( ) {
                if ( mainSeatStand.getPassenger() == null ) {
                    dismount();
                    cancel();
                    return;
                }

                if ( !player.isOnline() ) {
                    dismount();
                    cancel();
                    return;
                }

                mainHologram.teleport( player.getLocation() );

                GamePlayer gamePlayer = GamePlayer.getPlayer( player.getUniqueId() );

                if(gamePlayer != null) {
                    updateHologram( gamePlayer.getHeartString() );
                }

                if ( lastLocation.distance( mainArmorStand.getLocation() ) == 0 ) {
                    if ( ++stayTicks > 3 ) {
                        this.addX = 0;
                        this.addZ = 0;
                    }
                } else {
                    lastLocation = mainArmorStand.getLocation();
                    stayTicks = 0;
                }

                EntityPlayer entityPlayer = ( ( CraftPlayer ) player ).getHandle();

                double sideMotion = entityPlayer.bf;
                double forwardMotion = entityPlayer.bg;

                final Material type = mainArmorStand.getLocation().getBlock().getType();
                double yVelocity = type == Material.WATER || type == Material.STATIONARY_WATER ? 0.01
                        : mainArmorStand.getVelocity().getY();

                final Vector direction = mainArmorStand.getLocation().getDirection();

                // Moving ship only if it is on water
                if ( type == Material.WATER || type == Material.STATIONARY_WATER ) {
                    // Calculating the velocity-things
                    if ( forwardMotion > 0 ) {
                        // Calculating if the player drives forwards
                        final double x = direction.getX();
                        final double z = direction.getZ();

                        if ( x * this.addX < maxSpeed && z * this.addZ < maxSpeed && x * this.addX > -maxSpeed && z * this.addZ > -maxSpeed ) {
                            this.addX += speed;
                            this.addZ += speed;
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
                }

                // Setting velocity of armorstand
                mainArmorStand.setVelocity( new Vector( direction.getX() * this.addX, yVelocity, direction.getZ() * this.addZ ) );

                // Getting handles of main-armorstand & seatstand
                EntityArmorStand entityArmorStand = ( ( CraftArmorStand ) mainArmorStand ).getHandle();
                EntityArmorStand entitySeatStand = ( ( CraftArmorStand ) mainSeatStand ).getHandle();

                // Calculating new yaw by side motion
                float newYaw = ( float ) ( entityArmorStand.yaw - ( sideMotion * 3 ) );

                // Setting yaw
                entityArmorStand.yaw = newYaw;
                entitySeatStand.yaw = newYaw;

                // Setting locations
                Location location = mainArmorStand.getLocation();

                ( ( CraftArmorStand ) mainMovilityStand ).getHandle().setLocation(
                        location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch() );
                entitySeatStand.setLocation( location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch() );

                for ( ArmorStandBlock blocks : Ship.this.blocksArmorStand ) {
                    blocks.updatePosition( location );
                }

            }

        }.runTaskTimer( DevAthlon.getInstance(), 1L, 1L );
    }

    private void spawnMainArmorStands( Location location, Player player ) {
        ArmorStand mainStand = ( ArmorStand ) location.getWorld().spawnEntity( location, EntityType.ARMOR_STAND );
        setGravity( mainStand, true );
        mainStand.setVisible( false );

        ArmorStand movilityStand = ( ArmorStand ) location.getWorld().spawnEntity( location, EntityType.ARMOR_STAND );
        setGravity( movilityStand, false );
        movilityStand.setVisible( false );
        movilityStand.setCustomName( "ShipPart;" + player.getUniqueId() );

        ArmorStand seatStand = ( ArmorStand ) location.getWorld().spawnEntity( location, EntityType.ARMOR_STAND );
        setGravity( seatStand, false );
        seatStand.setVisible( false );
        seatStand.setCustomName( "ShipPart;" + player.getUniqueId() );

        ArmorStand hologram = ( ArmorStand ) location.getWorld().spawnEntity( location, EntityType.ARMOR_STAND );
        setGravity( hologram, false );
        hologram.setVisible( false );
        hologram.setCustomNameVisible( true );
        hologram.setCustomName( "" );

        // Setting passenger
        movilityStand.setPassenger( seatStand );

        // Setting name of main-stand
        mainStand.setCustomName( "Ship;" + player.getUniqueId() + ";" + seatStand.getUniqueId() );

        // Setting player-passenger
        seatStand.setPassenger( player );

        this.mainArmorStand = mainStand;
        this.mainSeatStand = seatStand;
        this.mainMovilityStand = movilityStand;
        this.mainHologram = hologram;
    }

    public void buildModel( ) {
        for ( ModelPart part : this.shipModel.getModelParts() ) {
            add( part.getX(), part.getY(), part.getZ(), part.getRotation(), part.getMaterial(), part.getData() );
        }
    }

    public void updateHologram(String text) {
        this.mainHologram.setCustomName( text );
    }

    public void dismount( ) {
        this.player.removePotionEffect( PotionEffectType.INVISIBILITY );

        mainArmorStand.remove();
        mainMovilityStand.remove();
        mainSeatStand.remove();
        mainHologram.remove();

        for(ArmorStandBlock model : blocksArmorStand) {
            model.getArmorStand().remove();
        }

        GamePlayer gamePlayer = GamePlayer.getPlayer( player.getUniqueId() );

        if(gamePlayer != null) {
            gamePlayer.leaveGame();
        }
    }

    private static void setGravity( ArmorStand armorStand, boolean gravity ) {
        armorStand.setGravity( gravity );
        ( ( CraftArmorStand ) armorStand ).getHandle().noclip = !gravity;
    }

    public void setSpeed( double speed ) {
        this.speed = speed;
    }

    public void setMaxSpeed( double maxSpeed ) {
        this.maxSpeed = maxSpeed;
    }

    public World getWorld( ) {
        return this.player.getWorld();
    }

    public void add( double x, double y, double z, float rotation, Material material, int data ) {
        this.blocksArmorStand.add( new ArmorStandBlock( this.player.getLocation(), new Location( getWorld(), x * 0.7d, y * 0.7d, z * 0.7d, rotation, 0 ), material, data, false ) );
    }
}
