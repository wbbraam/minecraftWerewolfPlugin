package hoeve.plugins.werewolf.game.actions;

import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.roles.BaseRole;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by DeStilleGast 13-4-2020
 */
public class NearbySelector implements Runnable, Listener {

    private BukkitTask task;
    private WerewolfGame game;

    private List<Player> selectors;
    private Map<Player, Player> selected;

    private final PotionEffect selectEffect = new PotionEffect(PotionEffectType.GLOWING, 10, 1, false, false, false);

    private List<Player> selectables;

    public boolean isEveryoneVisible = false;

    public NearbySelector(WerewolfGame game, List<Player> selectors) {
        this.game = game;
        this.selectors = selectors;
        this.selected = new HashMap<>();
    }

    public NearbySelector(WerewolfGame game, Class<? extends BaseRole> byRole) {
        this(game, game.getPlayersByRole(byRole).stream().map(WerewolfPlayer::getPlayer).collect(Collectors.toList()));
    }


    public void start() {
        List<Player> playerList = game.getPlayerList(false).stream().filter(WerewolfPlayer::isAlive).map(WerewolfPlayer::getPlayer).collect(Collectors.toList());

        if(!isEveryoneVisible) {
            for (Player player : playerList) {
                if (player.getGameMode() != GameMode.SPECTATOR) {
                    if (!selectors.contains(player)) { // if player is a selector, we dont want to hide the players for them
                        for (Player hideThisPlayer : playerList) {
                            player.hidePlayer(game.getPlugin(), hideThisPlayer);
                        }
                    }
                }
            }
        }

        game.centerPlayers();
        task = Bukkit.getScheduler().runTaskTimer(game.getPlugin(), this, 5, 5);
        Bukkit.getPluginManager().registerEvents(this, game.getPlugin());

        selectables = game.getPlayerList(false).stream().filter(WerewolfPlayer::isAlive).map(WerewolfPlayer::getPlayer).filter(p -> !selectors.contains(p)).collect(Collectors.toList());
        selectables.remove(game.getGameMaster().getPlayer());

        for (Player selector : selectors) {
            game.notifyPlayer(selector, "Stand by a player you want to vote for");
        }
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(task.getTaskId());
        HandlerList.unregisterAll(this); // https://www.spigotmc.org/wiki/using-the-event-api/

        game.centerPlayers();

        List<Player> playerList = game.getPlayerList(true).stream().map(WerewolfPlayer::getPlayer).collect(Collectors.toList());
        for (Player player : playerList) {
            player.removePotionEffect(PotionEffectType.GLOWING);

            for (Player thisPlayer : playerList) {
                player.showPlayer(game.getPlugin(), thisPlayer);
            }
        }
    }

    public List<Player> getSelectedPlayers(){
        return this.selected.values().stream().distinct().collect(Collectors.toList());
    }

    // Could be better, for now this is it
    public Player getTopSelectedPlayer(){
        Map<Player, Integer> countMap = new HashMap<>();
        for (Player p : selected.values()){
            countMap.put(p, countMap.getOrDefault(p, 0) + 1);
        }

        int currentCount = 0;
        Player mostCounts = null;
        for(Player key : countMap.keySet()){
            if(countMap.get(key) > currentCount){
                mostCounts = key;
                currentCount = countMap.get(key);
            }
        }

        return mostCounts;
    }

    public Map<Player, Player> getSelectedMap(){
        return this.selected;
    }


    @Override
    public void run() {
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

            if(mostNearby != null) {
                selected.put(selector, mostNearby);
                selector.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("You are selecing: ").append(mostNearby.getDisplayName()).color(ChatColor.AQUA).create());
            }
        }

        for (Player selected : selected.values()) {
            selected.addPotionEffect(selectEffect);
        }
    }

    private double getDistance(Player p1, Player p2) {
        if (p1.getLocation().getWorld() != p2.getLocation().getWorld()) return Double.MAX_VALUE;

        return p1.getLocation().distanceSquared(p2.getLocation());
    }

    // prevent players from running await when a selector is active
    @EventHandler
    public void onAttemptToMove(PlayerMoveEvent event){
        Player p = event.getPlayer();
        if(game.getGameMaster().getPlayer() == p) return;

        if(game.getPlayerList(false).stream().map(WerewolfPlayer::getPlayer).anyMatch(player -> player == event.getPlayer())) {
//            if(game.getGameMaster().getPlayer().equals(p)) return;

            if (!selectors.contains(p)) { // is it one of the targets that wants to run away, deny it
                if (event.getTo() != null && (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ())){
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100f, 1f);

                    event.setCancelled(true);
                }
            }
        }
    }
}


