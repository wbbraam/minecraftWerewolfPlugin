package hoeve.plugins.werewolf.game.scoreboards;

import com.google.common.collect.Lists;
import hoeve.plugins.werewolf.game.WerewolfGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DeStilleGast 12-4-2020
 */
public class ScoreboardManager {

//    private List<WerewolfScoreboard> scoreboardList = new ArrayList<>();

    private Map<Player, WerewolfScoreboard> scoreboardMap = new HashMap<>();


    public void addPlayer(Player player){
        removePlayer(player);
        scoreboardMap.put(player, new WerewolfScoreboard(player));
    }

    public void removePlayer(Player player){
        if(scoreboardMap.containsKey(player)){
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            scoreboardMap.remove(player);
        }
    }

    public void updateScoreboards(WerewolfGame game){
        scoreboardMap.values().forEach(ws -> ws.update(game));
    }
}
