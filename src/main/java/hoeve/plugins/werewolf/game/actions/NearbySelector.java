package hoeve.plugins.werewolf.game.actions;

import hoeve.plugins.werewolf.WerewolfPlugin;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.roles.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by DeStilleGast 13-4-2020
 */
public class NearbySelector implements Runnable {

    private BukkitTask task;
    private WerewolfGame game;

    private List<Player> selectors;
    private Map<Player, Player> selected;

    private final PotionEffect selectEffect = new PotionEffect(PotionEffectType.GLOWING, 10, 1, false, false, false);

    public NearbySelector(WerewolfGame game, List<Player> selectors) {
        this.game = game;
        this.selectors = selectors;
        this.selected = new HashMap<>();
    }

    public NearbySelector(WerewolfGame game, Class<? extends IRole> byRole) {
        this(game, game.getPlayersByRole(byRole).stream().map(WerewolfPlayer::getPlayer).collect(Collectors.toList()));
    }


    public void start() {
        List<Player> playerList = game.getPlayerList().stream().map(WerewolfPlayer::getPlayer).collect(Collectors.toList());

        for (Player player : playerList) {
            if (!selectors.contains(player)) { // if player is a selector, we dont want to hide the players for them
                for (Player hideThisPlayer : playerList) {
                    player.hidePlayer(game.getPlugin(), hideThisPlayer);
                }
            }
        }

        task = Bukkit.getScheduler().runTaskTimer(game.getPlugin(), this, 5, 5);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(task.getTaskId());

        List<Player> playerList = game.getPlayerList().stream().map(WerewolfPlayer::getPlayer).collect(Collectors.toList());
        for (Player player : playerList) {
            for (Player thisPlayer : playerList) {
                player.showPlayer(game.getPlugin(), thisPlayer);
                player.removePotionEffect(PotionEffectType.GLOWING);
            }
        }
    }


    @Override
    public void run() {
        List<Player> selectables = game.getPlayerList().stream().map(WerewolfPlayer::getPlayer).filter(p -> !selectors.contains(p)).collect(Collectors.toList());
        selected.clear();

        for (Player selector : selectors) {
            Player mostNearby = null;
            double lastDistance = Double.MAX_VALUE;

            for (Player target : selectables) {
                double targetDistance = getDistance(selector, target);
                if (lastDistance > targetDistance) {
                    mostNearby = target;
                    lastDistance = targetDistance;
                }
            }

            selected.put(selector, mostNearby);
        }

        for (Player selected : selected.values()) {
            selected.addPotionEffect(selectEffect);
        }
    }

    private double getDistance(Player p1, Player p2) {
        if (p1.getLocation().getWorld() != p2.getLocation().getWorld()) return Double.MAX_VALUE;

        return p1.getLocation().distanceSquared(p2.getLocation());
    }
}


