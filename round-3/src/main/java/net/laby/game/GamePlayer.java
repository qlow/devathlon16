package net.laby.game;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
public class GamePlayer {

    @Getter
    private final static Map<UUID, GamePlayer> players = new HashMap<>();

    @Getter
    private UUID uuid;

    @Getter
    @Setter
    private int level, killStreak;

    public GamePlayer( UUID uuid ) {
        this.uuid = uuid;
    }

}
