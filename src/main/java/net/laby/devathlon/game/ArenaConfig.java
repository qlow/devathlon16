package net.laby.devathlon.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Class created by qlow | Jan
 * Represents an arena's config
 */
public class ArenaConfig {

    private String name = "undefined";
    private List<String> spawns = new ArrayList<>();
    private List<String> signs = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List<String> getSpawns() {
        return spawns;
    }

    public void setSpawns( List<String> spawns ) {
        this.spawns = spawns;
    }

    public List<String> getSigns() {
        return signs;
    }

    public void setSigns( List<String> signs ) {
        this.signs = signs;
    }

}
