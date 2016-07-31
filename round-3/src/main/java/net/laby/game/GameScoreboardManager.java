package net.laby.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

/**
 * Class created by qlow | Jan
 */
public class GameScoreboardManager {

    private final String[] scores = new String[]{
            "Level",
            "Killstreak",
            "Maximales Level",
            "Benötigte Kills"
    };

    public void updateScoreboard( Player player ) {
        Map<String, String> playerScores = new HashMap();
        GamePlayer gamePlayer = GamePlayer.getPlayer( player.getUniqueId() );

        playerScores.put( "Level", String.valueOf( gamePlayer.getLevel() ) );
        playerScores.put( "Killstreak", String.valueOf( gamePlayer.getKillStreak() ) );
        playerScores.put( "Maximales Level", String.valueOf( Level.getMaxLevels() - 1 ) );

        int requiredKills = gamePlayer.getRequiredKills();

        playerScores.put( "Benötigte Kills", String.valueOf( (requiredKills == -1 ? "/" : requiredKills) ) );

        Scoreboard scoreboard = player.getScoreboard();

        if ( scoreboard == null || scoreboard == Bukkit.getScoreboardManager().getMainScoreboard() ) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

            Objective objective = scoreboard.registerNewObjective( "mainObjective", "dummy" );
            objective.setDisplayName( "§a§lShipBattles" );
            objective.setDisplaySlot( DisplaySlot.SIDEBAR );

            // Setting scoreboard to player
            player.setScoreboard( scoreboard );
        }

        Team team = getOrCreate( scoreboard, "colorTeam" );
        team.setPrefix( "§a" );

        // Getting main-objective
        Objective objective = scoreboard.getObjective( "mainObjective" );

        int highestScore = 11;
        int spaceBetweenScoresCounter = 12;

        for ( int i = 0; i < scores.length; i++ ) {
            String score = scores[i];

            // Adding score-name to colorTeam
            if ( !team.getEntries().contains( score ) ) {
                team.addEntry( score );
            }

            // Setting score of colored score
            objective.getScore( score ).setScore( highestScore-- );

            String spaceString = generateSpaces( highestScore );

            // Getting or creating team with spaces
            Team spaceTeam = getOrCreate( scoreboard, spaceString );

            // Adding member to spaceTeam
            if ( spaceTeam.getEntries().size() == 0 )
                spaceTeam.addEntry( spaceString );

            // Setting prefix of space-team
            spaceTeam.setPrefix( "§7" + playerScores.get( score ) );

            // Adding space score
            objective.getScore( spaceString ).setScore( highestScore-- );

            if ( i != (scores.length - 1) ) {
                objective.getScore( generateSpaces( spaceBetweenScoresCounter++ ) ).setScore( highestScore-- );
            }
        }

    }

    private Team getOrCreate( Scoreboard scoreboard, String teamName ) {
        Team team = scoreboard.getTeam( teamName );

        return team != null ? team : scoreboard.registerNewTeam( teamName );
    }

    private String generateSpaces( int amount ) {
        String spaceString = "";

        for ( int i = 0; i < amount; i++ ) {
            spaceString += " ";
        }

        return spaceString;
    }

}
