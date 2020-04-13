package hoeve.plugins.werewolf.game;

import hoeve.plugins.werewolf.WerewolfPlugin;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;
import hoeve.plugins.werewolf.game.roles.CommonRole;
import hoeve.plugins.werewolf.game.roles.CupidoRole;
import hoeve.plugins.werewolf.game.roles.IRole;
import hoeve.plugins.werewolf.game.roles.WereWolfRole;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WerewolfGame implements Listener {

    private WerewolfPlugin plugin;

    private List<WerewolfPlayer> playerList;
    private WerewolfCardDeck cardDeck;
//    private CommandSender leaderName = Bukkit.getConsoleSender();
    private WerewolfPlayer gameMaster = null;


    private GameStatus gamestatus;

    public WerewolfGame(){
        gamestatus = GameStatus.PLAYERSELECT;
        playerList = new ArrayList<>();
        cardDeck = new WerewolfCardDeck();
    }

    public WerewolfGame(WerewolfPlugin plugin) {
        this();

        this.plugin = plugin;
    }

    ///////////////////////////
    // Game startup commands //
    ///////////////////////////

    /**
     * Start the game, give every player a role
     */
    public void startGame() {
//        cardDeck.resetDeck(playerList.size());
//        for (WerewolfPlayer player : playerList) {
//            //System.out.println("Give card to:" + player.getName());
//            //System.out.println("Cards left before dealing:"+ cardDeck.getDeckSize());
//            player.setRole(cardDeck.drawCard());
//        }

        playerList.get(0).setRole(new WereWolfRole());
        playerList.get(1).setRole(new CommonRole());
        playerList.get(2).setRole(new CupidoRole());

        gamestatus = GameStatus.STARTUP;
        plugin.getScoreboardManager().updateScoreboards(this);
        centerPlayers();

        executeNewStatus();
    }

    //////////////////////////////
    // PLAYER LIST MANIPULATION //
    //////////////////////////////
    public void setGameMaster(Player newGameMaster) {
        gameMaster = new WerewolfPlayer(newGameMaster);
        plugin.getScoreboardManager().addPlayer(newGameMaster);
    }

    public String getLeaderName() {
        return gameMaster.getName();
    }

    public WerewolfPlayer getGameMaster(){
        return gameMaster;
    }

    //TODO: message formatting
    public void notifyGameMaster(String message){
        gameMaster.getPlayer().sendMessage(message);
    }

    /**
     * Add new Player to the game
     *
     * @param player name of Player
     * @return true if player was added to the game, false if player is already ingame
     */
    public Boolean addPlayer(Player player) {
        // check if we find name in list, found it, return false (not added)
        if (playerList.stream().map(WerewolfPlayer::getPlayer).anyMatch(s -> s.equals(player))) {
            return false;
        }

        playerList.add(new WerewolfPlayer(player));
        plugin.getScoreboardManager().addPlayer(player);
        return true;

    }

    /**
     * Remove player from game
     *
     * @param player player
     * @return true if player was removed, false if not found
     */
    public Boolean removePlayer(Player player) {
        WerewolfPlayer wwPlayer = playerList.stream().filter(w -> w.getPlayer() == player).findFirst().orElse(null);

        plugin.getScoreboardManager().removePlayer(player);
        return playerList.remove(wwPlayer);
    }

    public WerewolfPlayer getPlayer(Player player){
        return playerList.stream().filter(p -> p.getPlayer() == player).findFirst().orElse(null);
    }

    public WerewolfPlayer getPlayerByName(String name) {
        return playerList.stream().filter(p -> p.getPlayer().getName().equals(name)).findFirst().orElse(null);
    }

    public IRole getPlayerRole(String name) {
        return playerList.stream().filter(p -> p.getName().equalsIgnoreCase(name)).map(WerewolfPlayer::getRole).findFirst().orElse(null);
    }

    public List<WerewolfPlayer> getPlayersByRole(Class<? extends IRole> roleClass) {
        return playerList.stream().filter(p -> p.getRole().getClass() == roleClass).collect(Collectors.toList());
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
    public void finishGame() {
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
        if(werewolfPlayer.getPlayer() != null) {
            Player p = werewolfPlayer.getPlayer();
             return Bukkit.getOnlinePlayers().contains(p);
        }

        return false;
    }


    // Server events
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event){
//        Player p = event.getPlayer();
        WerewolfPlayer leftPlayer = getPlayer(event.getPlayer());
        if(leftPlayer != null){
            playerList.remove(leftPlayer);
        }
    }


    public void executeStartup(Runnable whenAllIsReadyJob){
//        WaitTillAllReady waitTillAllReady = plugin.setupWaiter(playerList.size(), 30, "Letting everyone checking out there roles", whenAllIsReadyJob);

//        playerList.forEach(p -> p.onGameStart(waitTillAllReady));
    }

    public void executeNewStatus() {
        if(gamestatus == GameStatus.STARTUP) return;
        playerList.forEach(p -> p.onGameStatusChange(this, gamestatus));
    }

    public WerewolfPlugin getPlugin(){
        return this.plugin;
    }

    // todo formatting
    public void notifyPlayer(Player player, String message) {
        player.sendMessage("[Game] " + message);
    }

    public void notifyPlayer(WerewolfPlayer player, String message){
        notifyPlayer(player.getPlayer(), message);
    }

    public void centerPlayers(){
        int size = this.playerList.size();

        double theta = ((Math.PI * 2) / size);
        Location center = gameMaster.getPlayer().getLocation();

        Location gameMasterLocation = gameMaster.getPlayer().getLocation();

        int radius = 15;
        for(int x = gameMasterLocation.getBlockX() - radius; x <= gameMasterLocation.getBlockX() + radius; x++) {
            for(int y = gameMasterLocation.getBlockY() - radius; y <= gameMasterLocation.getBlockY() + radius; y++) {
                for(int z = gameMasterLocation.getBlockZ() - radius; z <= gameMasterLocation.getBlockZ() + radius; z++) {
//                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                    if(gameMasterLocation.getWorld().getBlockAt(x, y, z).getType() == Material.CAMPFIRE){
                        center = new Location(gameMasterLocation.getWorld(), x, y, z).add(0.5, 0, 0.5);
                    }
                }
            }
        }



        for(int i = 0; i < size; i++){
            WerewolfPlayer player = this.playerList.get(i);
            if(!player.isAlive()) continue;

            double angle = (theta * i);

            int Radius = Math.max(size / 3, 3);
            double X = Radius * Math.cos(angle);
            double Z = Radius * Math.sin(angle);

            Location newPos = center.clone().add(X, 0, Z);

            while(gameMasterLocation.getWorld().getBlockAt(newPos).getType() != Material.AIR){
                newPos = newPos.add(0, 0.5, 0);
            }

            newPos.setDirection((center.clone().subtract(newPos.clone()).toVector()).normalize()); // look at venter

//            ArmorStand stand = (ArmorStand) center.getWorld().spawnEntity(newPos, EntityType.ARMOR_STAND);
//            stand.setGravity(false);
//            stand.setCustomNameVisible(true);
//            stand.setCustomName("Player spawn #" + (i+1));
//            stand.setCollidable(false);

//            this.armorStandArrayList.add(stand);
            player.getPlayer().teleport(newPos, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    public void notifyRole(Class<? extends IRole> roleClass, String message) {
        for (WerewolfPlayer p : this.getPlayersByRole(roleClass)){
            notifyPlayer(p, message);
        }
    }
}
