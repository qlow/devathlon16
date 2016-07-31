package net.laby.game.listeners;

import net.laby.devathlon.DevAthlon;
import net.laby.game.Game;
import net.laby.game.GamePlayer;
import net.laby.game.GameRegion;
import net.laby.game.Level;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftFireball;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
public class InteractListener implements Listener {

    @EventHandler
    public void onInteract( PlayerInteractEvent event ) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = GamePlayer.getPlayer( player.getUniqueId() );

        if ( event.getAction() == Action.PHYSICAL )
            return;

        if ( gamePlayer.isIngame()
                && (System.currentTimeMillis() - gamePlayer.getJoined()) >= 2000
                && (System.currentTimeMillis() - gamePlayer.getLastShooted()) >= 1000 ) {
            event.setCancelled( true );

            gamePlayer.setLastShooted( System.currentTimeMillis() );
            Fireball fireball = player.launchProjectile( Fireball.class );
            fireball.setVelocity( player.getLocation().getDirection().multiply( 3 ) );

            new BukkitRunnable() {

                @Override
                public void run() {
                    if ( (( CraftFireball ) fireball).getHandle().ticksLived >= 200 ) {
                        fireball.remove();
                        cancel();
                        return;
                    }

                    List<Entity> entityList = fireball.getNearbyEntities( 2D, 2D, 2D );

                    for ( Entity entity : entityList ) {
                        if ( !(entity instanceof ArmorStand) )
                            continue;

                        ArmorStand armorStand = ( ArmorStand ) entity;

                        if ( armorStand.getCustomName() == null || !armorStand.getCustomName().startsWith( "Ship" ) )
                            continue;

                        UUID playerUuid = UUID.fromString( armorStand.getCustomName().split( ";" )[1] );

                        if ( playerUuid.toString().equals( player.getUniqueId().toString() ) )
                            continue;

                        GamePlayer hitPlayer = GamePlayer.getPlayer( playerUuid );

                        if ( hitPlayer == null )
                            continue;

                        fireball.remove();
                        cancel();

                        if ( !hitPlayer.isIngame() )
                            break;

                        hitPlayer.setLife( hitPlayer.getLife() - gamePlayer.getAttackDamage() );

                        // Checking for HP
                        if ( hitPlayer.getLife() <= 0 ) {
                            // Sending messages
                            player.sendMessage( "§7Du hast §6" + hitPlayer.getPlayer().getName() + " §7getötet!" );
                            hitPlayer.getPlayer().sendMessage( "§7Du wurdest von §6" + player.getName() + " §7getötet!" );

                            // Leaving game for hit player
                            hitPlayer.getPlayer().getVehicle().setPassenger( null );
                            hitPlayer.leaveGame();

                            gamePlayer.setKillStreak( gamePlayer.getKillStreak() + 1 );

                            // Checking for new level
                            if ( gamePlayer.getRequiredKills() == 0 ) {
                                gamePlayer.setLevel( gamePlayer.getLevel() + 1 );
                                Level level = Level.values()[gamePlayer.getLevel()];

                                // Setting new level
                                player.getVehicle().setPassenger( null );
                                spawnShip( player, level );
                            }

                            // Healing and updating scoreboard
                            gamePlayer.setLife( Level.values()[gamePlayer.getLevel()].getMaxHearts() );
                            Game.getGame().getGameScoreboardManager().updateScoreboard( player );
                        }

                        break;
                    }
                }
            }.runTaskTimer( DevAthlon.getInstance(), 1L, 1L );

            return;
        }
        if ( !Game.getGame().getRegion().isValid() )
            return;

        if ( event.getClickedBlock() == null )
            return;

        if ( !(event.getClickedBlock().getState() instanceof Sign) )
            return;

        Location signLocation = Game.getGame().getConfig().getGameJoinSign().getLocation();

        if ( signLocation == null )
            return;

        if ( !signLocation.equals( event.getClickedBlock().getLocation() ) )
            return;

        // Setting some values
        gamePlayer.setIngame( true );
        gamePlayer.setJoined( System.currentTimeMillis() );
        gamePlayer.setLevel( 0 );
        gamePlayer.setKillStreak( 0 );
        gamePlayer.setLife( 20F );

        // Updating scoreboard
        Game.getGame().getGameScoreboardManager().updateScoreboard( player );

        // Clearing player's inventory
        player.getInventory().clear();

        // Teleporting player
        GameRegion region = Game.getGame().getRegion();

        Location location = new Location( Game.getGame().getConfig().getGameRegionFirstPoint().getLocation().getWorld(),
                region.getRandomX(), Game.getGame().getConfig().getHighestWaterY() + 5, region.getRandomZ() );

        // Loading chunk
        location.getChunk().load();

        player.teleport( location );

        // Constructing ship
        spawnShip( player, Level.values()[0] );
    }

    private void spawnShip( Player player, Level level ) {
        try {
            DevAthlon.getInstance().getShips().add( level.getShipModel().getConstructor( Player.class ).newInstance( player ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
