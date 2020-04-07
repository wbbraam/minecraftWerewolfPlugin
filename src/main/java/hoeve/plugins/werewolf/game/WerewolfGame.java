package hoeve.plugins.werewolf.game;

import com.google.common.collect.Lists;
import hoeve.plugins.werewolf.WerewolfPlugin;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;
import hoeve.plugins.werewolf.game.roles.IRole;
import hoeve.plugins.werewolf.game.roles.WereWolfRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class WerewolfGame implements Listener {

    private WerewolfPlugin plugin;

    private List<WerewolfPlayer> playerList;
    private WerewolfCardDeck cardDeck;
//    private CommandSender leaderName = Bukkit.getConsoleSender();
    private WerewolfPlayer gameMaster = null;


    private GameStatus gamestatus;

    public WerewolfGame(WerewolfPlugin plugin) {
        gamestatus = GameStatus.PLAYERSELECT;
        playerList = new ArrayList<>();
        cardDeck = new WerewolfCardDeck();

        this.plugin = plugin;
    }

    ///////////////////////////
    // Game startup commands //
    ///////////////////////////

    /**
     * Start the game, give every player a role
     */
    public void assignRoles() {
        cardDeck.resetDeck(playerList.size());
        for (WerewolfPlayer player : playerList) {
            //System.out.println("Give card to:" + player.getName());
            //System.out.println("Cards left before dealing:"+ cardDeck.getDeckSize());
            player.setRole(cardDeck.drawCard());
        }
    }

    //////////////////////////////
    // PLAYER LIST MANIPULATION //
    //////////////////////////////
    public Boolean takeLeadership(CommandSender newGameMaster) {
        gameMaster = new WerewolfPlayer(newGameMaster);
        return true;
    }

    public String getLeaderName() {
        return gameMaster.getName();
    }

    public WerewolfPlayer getLeaderPlayer(){
        return gameMaster;
    }


    /**
     * Add new Player to the game
     *
     * @param name name of Player
     * @return true if player was added to the game, false if player is already ingame
     */
    public Boolean addPlayer(String name) {

        // check if we find name in list, found it, return false (not added)
        if (playerList.stream().map(WerewolfPlayer::getName).anyMatch(s -> s.equalsIgnoreCase(name))) {
            return false;
        }

        playerList.add(new WerewolfPlayer(Bukkit.getPlayer(name)));
        return true;

    }

    /**
     * Remove player from game
     *
     * @param name name of player
     * @return true if player was removed, false if not found
     */
    public Boolean removePlayer(String name) {
        ListIterator<WerewolfPlayer> iter = playerList.listIterator();
        while (iter.hasNext()) {
            if (iter.next().getName().equals(name)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    public IRole getPlayerRole(String name) {
        return playerList.stream().filter(p -> p.getName().equalsIgnoreCase(name)).map(WerewolfPlayer::getRole).findFirst().orElse(null);
    }

    /**
     * Clear playerlist
     */
    public void clearPlayerList() {
        playerList = new ArrayList<>();
    }

    /**
     * Get list of player with there roles
     * @return List with playername with there role next to it
     */
    public List<String> listPlayerNames() {
        return playerList.stream().map(p -> p.getName() + " - " + p.getRole().getRoleName()).collect(Collectors.toList());
    }

    /**
     * Get list of players from the game
     * @return Copy of playerlist
     */
    public List<WerewolfPlayer> getPlayerList(){
        return new ArrayList<>(playerList);
    }

    ///////////////////////
    // GAMESTATE METHODS //
    ///////////////////////
    // TODO: Fix order
    public GameStatus nextStatus() {
        switch (gamestatus) {
            case PLAYERSELECT: // adding/joining and removing players from game
                gamestatus = GameStatus.STARTUP;
                return GameStatus.STARTUP;

            case STARTUP: // give every player a role
                gamestatus = GameStatus.DAY;
                return gamestatus;

            case DAY: // Someone died (except if we just started or was healed) and we need to kill someone
                gamestatus = GameStatus.BURGERVOTE;
                return gamestatus;

            case BURGERVOTE: // We killed someone and were happy or not, but it is bed time
                gamestatus = GameStatus.NIGHT;
                return gamestatus;

            case NIGHT: // Its night, party for the werewolves, they are going to meat, also the seer can look in its ball and look for some roles
                gamestatus = GameStatus.WEREWOLFVOTE;
                return gamestatus;

            case WEREWOLFVOTE: // After the werewolves went to bed after a good midnight snack, the witch wakes up and checks the night to heal or kill
                gamestatus = GameStatus.WITCHACTIVITY;
                return gamestatus;

            case WITCHACTIVITY: // Witch has done its job and went to bed, just to wake up again
                gamestatus = GameStatus.DAY;
                return gamestatus;

            case ENDED: // Common/villagers won or the wolves won
                gamestatus = GameStatus.PLAYERSELECT;
                return gamestatus;
        }

        return gamestatus;
    }

    /**
     * Finish the game
     */
    public void endStatus() {
        gamestatus = GameStatus.ENDED;
    }

    /**
     * @return Get the current status of the game
     */
    public GameStatus getStatus() {
        return gamestatus;
    }

    /**
     * Check if player is still online
     * @param werewolfPlayer
     * @return If player is still in the server
     */
    public boolean isPlayerValid(WerewolfPlayer werewolfPlayer){
        if(werewolfPlayer.getPlayer() instanceof ConsoleCommandSender) return true;
        if(werewolfPlayer.getPlayer() instanceof Player) {
            Player p = (Player) werewolfPlayer.getPlayer();
             return Bukkit.getOnlinePlayers().contains(p);
        }

        return false;
    }


    // Server events
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event){
//        Player p = event.getPlayer();
        playerList.forEach(pl -> {
            if(pl.getPlayer() == event.getPlayer()){
                // Kill player, give it some time to reconnect ???
            }
        });
    }


    public void executeStartup(Runnable whenAllIsReadyJob){
        WaitTillAllReady waitTillAllReady = plugin.setupWaiter(playerList.size(), 30, whenAllIsReadyJob);

        playerList.forEach(p -> p.onGameStart(waitTillAllReady));
    }

    public void executeNewStatus() {
        if(gamestatus == GameStatus.STARTUP) return;
        playerList.forEach(p -> p.onGameStatusChange(gamestatus));
    }
}
