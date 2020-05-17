package hoeve.plugins.werewolf.game.scoreboards;

import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        if(game.getGameMaster().getPlayer().equals(player)){
            onGameMasterBoard(game);
        }else {
            if (game.getStatus() == GameStatus.PLAYERSELECT) {
                createSidebar("Game status:", "Starting...", "", "Players: " + (game.getPlayerList(false).size()));
            } else {
                createSidebar("Game status:", game.getStatus().name(), "", "Your role:", game.getPlayer(player).getRole().getRoleName(), "", "Players: " + (game.getPlayerList(false).size()));
            }
        }
    }

    private void onGameMasterBoard(WerewolfGame game){
        List<WerewolfPlayer> playerList = game.getPlayerList(false);

        if(game.getStatus() == GameStatus.PLAYERSELECT) {
            createSidebar("Game status:", "Starting...", "", "Players: " + playerList.size());
        }else{
            List<String> sidebarItems = new ArrayList<>(Arrays.asList("Game status:", game.getStatus().name(), "", "Players: " + playerList.size(), ""));

            playerList.stream().map(WerewolfPlayer::getRole).distinct().forEach(r -> {
                long playersWithRole = game.getPlayersByRole(r.getClass()).stream().filter(WerewolfPlayer::isAlive).count();
                sidebarItems.add(r.getRoleName() + ChatColor.RESET + ": " + playersWithRole);
            });

            createSidebar(sidebarItems.toArray(new String[0]));
        }
    }

    private void createSidebar(String...lines){
        // setting up sidebar
        Objective sidebar = scoreboard.getObjective("Sidebar");
        if(sidebar == null) sidebar = scoreboard.registerNewObjective("Sidebar", "dummy", "Game information");

        for (String line: scoreboard.getEntries()) scoreboard.resetScores(line);

        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        // end

        int spaceCounter = 1;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if(line.trim().isEmpty()) {
                char[] array = new char[spaceCounter];
                Arrays.fill(array, ' ');
                line = new String(array);

                spaceCounter++;
            }

            sidebar.getScore(line).setScore(lines.length - i);
        }
    }
}
