package net.laby.devathlon.game;

import net.laby.devathlon.Devathlon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
public class Arena {

    private ArenaConfig arenaConfig;

    private String name;
    private List<Location> spawns;
    private List<Location> signs;

    private List<UUID> joinedPlayers = new ArrayList<>();
    private boolean ingame;

    private File mapFolder;

    public Arena( ArenaConfig arenaConfig, File mapFolder, String name, List<Location> spawns, List<Location> signs ) {
        this.arenaConfig = arenaConfig;
        this.mapFolder = mapFolder;
        this.name = name;
        this.spawns = spawns;
        this.signs = signs;
    }

    public File getMapFolder() {
        return mapFolder;
    }

    public void setMapFolder( File mapFolder ) {
        this.mapFolder = mapFolder;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List<Location> getSpawns() {
        return spawns;
    }

    public void setSpawns( List<Location> spawns ) {
        this.spawns = spawns;
    }

    public List<Location> getSigns() {
        return signs;
    }

    public void setSigns( List<Location> signs ) {
        this.signs = signs;
    }

    public boolean isIngame() {
        return ingame;
    }

    public void setIngame( boolean ingame ) {
        this.ingame = ingame;

        updateSigns();
    }

    public List<UUID> getJoinedPlayers() {
        return joinedPlayers;
    }

    /**
     * Called when a player looses or leaves the server
     *
     * @param player player that looses/leaves the server
     */
    public void loosePlayer( Player player ) {
        // Checking whether the player is joined
        if ( !getJoinedPlayers().contains( player.getUniqueId() ) )
            return;

        // Removing player from joined players
        getJoinedPlayers().remove( player.getUniqueId() );

        if ( !isIngame() ) {
            updateSigns();
            return;
        }

        // Getting last player
        UUID lastPlayer = joinedPlayers.get( 0 );

        // Clearing joined players finally
        joinedPlayers.clear();

        // Setting ingame-state
        setIngame( false );

        // Sending win-message and teleporting to spawn
        for ( UUID joinedPlayerUUID : getJoinedPlayers() ) {
            Player joinedPlayer = Bukkit.getPlayer( joinedPlayerUUID );

            joinedPlayer.sendMessage( Devathlon.PREFIX + "§6" +  Bukkit.getPlayer( lastPlayer ) + " §ahat die Runde gewonnen!");
            joinedPlayer.setHealth( 20D );

            joinedPlayer.getInventory().clear();

            // Returning player to cached location
            if(Devathlon.getInstance().getCachedLocations().containsKey( joinedPlayerUUID )) {
                joinedPlayer.teleport( Devathlon.getInstance().getCachedLocations().get( player.getUniqueId() ) );

                Devathlon.getInstance().getCachedLocations().remove( player.getUniqueId() );
            }
        }

        updateSigns();
    }

    /**
     * Updates the arena's signs
     */
    public void updateSigns() {
        // Iterating through all signs
        for ( Location signLoc : signs ) {
            // Checking if they're even signs
            if ( !(signLoc.getBlock().getState() instanceof Sign) )
                continue;

            Sign sign = ( Sign ) signLoc.getBlock().getState();

            // Setting lines
            sign.setLine( 0, "§7- " + getName() + " -" );
            sign.setLine( 1, (joinedPlayers.size() == 2 ? "§7" : "§a") + joinedPlayers.size() + "§7/2" );
            sign.setLine( 2, (ingame ? "§cIm Spiel" : "§6Lobby") );
            sign.setLine( 3, (joinedPlayers.size() == 2 ? "" : "§aClick to join") );

            // Updating
            sign.update( true );
        }
    }

    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

}