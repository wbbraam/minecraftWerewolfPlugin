package hoeve.plugins.werewolf.game.scoreboards;

import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Optional;

/**
 * Created by DeStilleGast 12-4-2020
 */
public class WerewolfScoreboard {

    private Player player;
    private Scoreboard scoreboard;

    public WerewolfScoreboard(Player player){
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        player.setScoreboard(scoreboard);
    }

    public void update(WerewolfGame game) {
        if(game == null) return;

        if(game.getGameMaster() == game.getPlayer(player)){
            onGameMasterBoard(game);
        }else {
            switch (game.getStatus()){
                case PLAYERSELECT:
                    createSidebar("Game status:", "Starting...", "", "Players: " + game.getPlayerList().size());
                    break;
                case STARTUP:
                    createSidebar("Game status:", "Preparing...", "", "Your role:", game.getPlayer(player).getRole().getRoleName(), "", "Players: " + game.getPlayerList().size());
                    break;
            }
        }
    }

    private void onGameMasterBoard(WerewolfGame game){
        switch (game.getStatus()){
            case PLAYERSELECT:
                createSidebar("Game status:", "Starting...", "", "Players: " + game.getPlayerList().size());
                break;
            case STARTUP:
//                createSidebar("Game status:", "Cupido is selecting");
        }
    }

    private void createSidebar(String...lines){
        // setting up sidebar
        Objective sidebar = scoreboard.getObjective("Sidebar");
        if(sidebar == null) sidebar = scoreboard.registerNewObjective("Sidebar", "dummy", "Game information");

        for (String line: scoreboard.getEntries()) scoreboard.resetScores(line);

        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        // end

        for (int i = 0; i < lines.length; i++) {
            sidebar.getScore(lines[i]).setScore(lines.length - i);
        }
    }
}
