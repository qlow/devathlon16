package net.laby.ship;

import net.laby.devathlon.DevAthlon;
import net.laby.game.GamePlayer;
import net.laby.schematic.ModelPart;
import net.laby.schematic.ShipModel;
import net.laby.schematic.Utils;
import net.minecraft.server.v1_10_R1.EntityArmorStand;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityDestroy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
    protected ArmorStand mainHologramHearts;
    protected ArmorStand mainHologramName;
    protected ArmorStand mainHologramLevel;

    protected double maxSpeed = 0.2D;
    protected double speed = 0.006;

    private long lastUpdate = 0;

    public Ship( Player player, String modelName ) {
        this.player = player;

        if ( DevAthlon.getInstance().getSchematicModels().containsKey( modelName ) ) {
            this.shipModel = DevAthlon.getInstance().getSchematicModels().get( modelName );
        }

        spawnMainArmorStands( player.getLocation(), player );
        if ( this.shipModel != null ) {
            buildModel();
        }

        updateHologramName( "§7" + player.getName() );

        player.playSound( player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 3, DevAthlon.getRandom().nextFloat() + 0.5f );

        new BukkitRunnable() {

            private double addX;
            private double addZ;

            private Location lastLocation = mainArmorStand.getLocation();
            private int stayTicks;

            private int lastLevel;

            @Override
            public void run( ) {
                if ( mainSeatStand.getPassenger() == null ) {
                    GamePlayer gamePlayer = GamePlayer.getPlayer( player.getUniqueId() );

                    boolean newLevel = false;

                    if ( gamePlayer != null ) {
                        newLevel = gamePlayer.getLevel() != lastLevel;
                    }

                    dismount( !newLevel );
                    cancel();

                    DevAthlon.getInstance().getShips().remove( this );
                    return;
                }

                if ( !player.isOnline() ) {
                    dismount();
                    cancel();

                    DevAthlon.getInstance().getShips().remove( this );
                    return;
                }

                if ( !player.hasPotionEffect( PotionEffectType.INVISIBILITY ) )
                    player.addPotionEffect( new PotionEffect( PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1 ) );

                mainHologramHearts.teleport( player.getLocation().clone().add( 0, -0.4, 0 ) );
                mainHologramName.teleport( player.getLocation() );
                mainHologramLevel.teleport( player.getLocation().clone().add( 0, -0.8, 0 ) );

                if ( lastUpdate < System.currentTimeMillis() ) {
                    lastUpdate = System.currentTimeMillis() + 1000;
                    Player target = Utils.getNearestPlayer( player );
                    if ( target != null ) {
                        player.setCompassTarget( target.getLocation() );
                    }
                }

                GamePlayer gamePlayer = GamePlayer.getPlayer( player.getUniqueId() );

                if ( gamePlayer != null ) {
                    updateHologramHearts( gamePlayer.getHeartString( false ) );
                    updateHologramLevel( "§aLevel " + ( gamePlayer.getLevel() + 1 ) );
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

                lastLevel = gamePlayer.getLevel();

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

        ArmorStand hologramHearts = ( ArmorStand ) location.getWorld().spawnEntity( location, EntityType.ARMOR_STAND );
        setGravity( hologramHearts, false );
        hologramHearts.setVisible( false );
        hologramHearts.setCustomNameVisible( true );
        hologramHearts.setCustomName( "" );
        destroyArmorStand( hologramHearts.getEntityId(), player );

        ArmorStand hologramName = ( ArmorStand ) location.getWorld().spawnEntity( location, EntityType.ARMOR_STAND );
        setGravity( hologramName, false );
        hologramName.setVisible( false );
        hologramName.setCustomNameVisible( true );
        hologramName.setCustomName( "" );
        destroyArmorStand( hologramName.getEntityId(), player );

        ArmorStand hologramLevel = ( ArmorStand ) location.getWorld().spawnEntity( location, EntityType.ARMOR_STAND );
        setGravity( hologramLevel, false );
        hologramLevel.setVisible( false );
        hologramLevel.setCustomNameVisible( true );
        hologramLevel.setCustomName( "" );
        destroyArmorStand( hologramLevel.getEntityId(), player );


        // Setting passenger
        movilityStand.setPassenger( seatStand );

        // Setting name of main-stand
        mainStand.setCustomName( "Ship;" + player.getUniqueId() + ";" + seatStand.getUniqueId() );

        // Setting player-passenger
        seatStand.setPassenger( player );

        this.mainArmorStand = mainStand;
        this.mainSeatStand = seatStand;
        this.mainMovilityStand = movilityStand;
        this.mainHologramHearts = hologramHearts;
        this.mainHologramName = hologramName;
        this.mainHologramLevel = hologramLevel;
    }

    public void buildModel( ) {
        for ( ModelPart part : this.shipModel.getModelParts() ) {
            add( part.getX(), part.getY(), part.getZ(), part.getRotation(), part.getMaterial(), part.getData() );
        }
    }

    public void updateHologramHearts( String text ) {
        this.mainHologramHearts.setCustomName( text );
    }

    public void updateHologramName( String text ) {
        this.mainHologramName.setCustomName( text );
    }

    public void updateHologramLevel( String text ) {
        this.mainHologramLevel.setCustomName( text );
    }

    private void destroyArmorStand( int entityId, Player forPlayer ) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy( entityId );
        CraftPlayer cp = ( CraftPlayer ) forPlayer;
        cp.getHandle().playerConnection.sendPacket( packet );
    }

    public void dismount( ) {
        dismount( true );
    }

    public void dismount( boolean leave ) {
        this.player.removePotionEffect( PotionEffectType.INVISIBILITY );

        mainArmorStand.remove();
        mainMovilityStand.remove();
        mainSeatStand.remove();
        mainHologramHearts.remove();
        mainHologramLevel.remove();
        mainHologramName.remove();


        for ( ArmorStandBlock model : blocksArmorStand ) {
            model.getArmorStand().remove();
        }

        GamePlayer gamePlayer = GamePlayer.getPlayer( player.getUniqueId() );

        if ( leave && gamePlayer != null ) {
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
        this.blocksArmorStand.add( new ArmorStandBlock( this.player.getLocation(), new Location( getWorld(), x * 0.6d, y * 0.6d + 0.2, z * 0.6d, rotation, 0 ), material, data, false ) );
    }
}
