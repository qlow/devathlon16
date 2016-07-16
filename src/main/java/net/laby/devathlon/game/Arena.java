package net.laby.devathlon.game;

import org.bukkit.Location;
import org.bukkit.block.Sign;

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

    public void updateSigns() {
        for ( Location signLoc : signs ) {
            if ( !(signLoc.getBlock().getState() instanceof Sign) )
                continue;

            Sign sign = ( Sign ) signLoc.getBlock().getState();

            sign.setLine( 0, "§7- " + getName() + " -" );
            sign.setLine( 1, (joinedPlayers.size() == 2 ? "§7" : "§a") + joinedPlayers.size() + "§7/2" );
            sign.setLine( 2, (ingame ? "§cIm Spiel" : "§6Lobby") );
            sign.setLine( 3, (joinedPlayers.size() == 2 ? "" : "§aClick to join") );

            sign.update( true );
        }
    }

    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

}
