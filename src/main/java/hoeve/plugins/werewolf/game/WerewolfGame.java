package hoeve.plugins.werewolf.game;

import hoeve.plugins.werewolf.WerewolfPlugin;
import hoeve.plugins.werewolf.game.actions.NearbySelector;
import hoeve.plugins.werewolf.game.actions.ParticleManager;
import hoeve.plugins.werewolf.game.helpers.BossBarTimer;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;
import hoeve.plugins.werewolf.game.interfaces.AskScreen;
import hoeve.plugins.werewolf.game.interfaces.BurgerVoteScreen;
import hoeve.plugins.werewolf.game.interfaces.CupidoScreen;
import hoeve.plugins.werewolf.game.roles.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.testng.collections.Lists;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static hoeve.plugins.werewolf.game.EnumDeadType.WITCH;

public class WerewolfGame implements Listener {

    private WerewolfPlugin plugin;

    private List<WerewolfPlayer> playerList;
    private WerewolfCardDeck cardDeck;
    //    private CommandSender leaderName = Bukkit.getConsoleSender();
    private WerewolfPlayer gameMaster = null;
    private ParticleManager particleManager;

    private GameStatus gamestatus;

    public WerewolfGame() {
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

        for (WerewolfPlayer werewolfPlayer : playerList) {
            werewolfPlayer.setAlive(true);
            werewolfPlayer.setRole(null);
            werewolfPlayer.setLover(null);

            werewolfPlayer.getPlayer().setGameMode(GameMode.ADVENTURE);
        }

        playerList.get(0).setRole(new WerewolfRole());
        playerList.get(1).setRole(new WerewolfRole());
        playerList.get(2).setRole(new WitchRole());
        playerList.get(3).setRole(new CupidoRole());
        for (int i = 4; i < playerList.size(); i++) {
            playerList.get(i).setRole(new CommonRole());
        }

        if (particleManager != null) {
            particleManager.stop();
        }

        particleManager = new ParticleManager(this);
        particleManager.start();

        gamestatus = GameStatus.STARTUP;
        plugin.getScoreboardManager().updateScoreboards(this);
        centerPlayers();

        plugin.setupWaiter(1, 15, "Look around, make friends, it is a short day", this::startFirstNight);

//        executeNewStatus();
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

    public WerewolfPlayer getGameMaster() {
        return gameMaster;
    }

    //TODO: message formatting
    public void notifyGameMaster(String message) {
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

    public WerewolfPlayer getPlayer(Player player) {
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
     *
     * @return List with playername with there role next to it
     */
    public List<String> listPlayerNames() {
        return playerList.stream().map(p -> p.getName() + " - " + p.getRole().getRoleName()).collect(Collectors.toList());
    }

    /**
     * Get list of players from the game
     *
     * @return Copy of playerlist
     */
    public List<WerewolfPlayer> getPlayerList() {
        List<WerewolfPlayer> tmpList = new ArrayList<>(playerList);
        tmpList.add(this.gameMaster);
        return tmpList;
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
     *
     * @param werewolfPlayer
     * @return If player is still in the server
     */
    public boolean isPlayerValid(WerewolfPlayer werewolfPlayer) {
        if (werewolfPlayer.getPlayer() instanceof ConsoleCommandSender) return true;
        if (werewolfPlayer.getPlayer() != null) {
            Player p = werewolfPlayer.getPlayer();
            return Bukkit.getOnlinePlayers().contains(p);
        }

        return false;
    }


    // Server events
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
//        Player p = event.getPlayer();
        WerewolfPlayer leftPlayer = getPlayer(event.getPlayer());
        if (leftPlayer != null) {
//            playerList.remove(leftPlayer);
            leftPlayers.add(event.getPlayer());
        }
    }

    private List<Player> leftPlayers = new ArrayList<>();

    public void executeStartup(Runnable whenAllIsReadyJob) {
//        WaitTillAllReady waitTillAllReady = plugin.setupWaiter(playerList.size(), 30, "Letting everyone checking out there roles", whenAllIsReadyJob);

//        playerList.forEach(p -> p.onGameStart(waitTillAllReady));
    }

    @Deprecated
    public void executeNewStatus() {
//        if(gamestatus == GameStatus.STARTUP) return;

        playerList.forEach(p -> p.onGameStatusChange(this, gamestatus));
    }

    public WerewolfPlugin getPlugin() {
        return this.plugin;
    }

    // todo formatting
    public void notifyPlayer(Player player, String message) {
        player.sendMessage("[Game] " + message);
    }

    public void notifyPlayer(WerewolfPlayer player, String message) {
        notifyPlayer(player.getPlayer(), message);
    }

    public void centerPlayers() {
        int size = this.playerList.size();

        double theta = ((Math.PI * 2) / size);
        Location center = gameMaster.getPlayer().getLocation();

        Location gameMasterLocation = gameMaster.getPlayer().getLocation();

        // search campfire
        int radius = 15;
        for (int x = gameMasterLocation.getBlockX() - radius; x <= gameMasterLocation.getBlockX() + radius; x++) {
            for (int y = gameMasterLocation.getBlockY() - radius; y <= gameMasterLocation.getBlockY() + radius; y++) {
                for (int z = gameMasterLocation.getBlockZ() - radius; z <= gameMasterLocation.getBlockZ() + radius; z++) {
//                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                    if (gameMasterLocation.getWorld().getBlockAt(x, y, z).getType() == Material.CAMPFIRE) {
                        center = new Location(gameMasterLocation.getWorld(), x, y, z).add(0.5, 0, 0.5);
                    }
                }
            }
        }


        for (int i = 0; i < size; i++) {
            WerewolfPlayer player = this.playerList.get(i);
            if (!player.isAlive()) continue;

            double angle = (theta * i);

            int Radius = Math.max(size / 3, 3);
            double X = Radius * Math.cos(angle);
            double Z = Radius * Math.sin(angle);

            Location newPos = center.clone().add(X, 0, Z);

            while (gameMasterLocation.getWorld().getBlockAt(newPos).getType() != Material.AIR) {
                newPos = newPos.add(0, 0.5, 0);
            }

            newPos.setDirection((center.clone().subtract(newPos.clone()).toVector()).normalize()); // look at center
            player.getPlayer().teleport(newPos, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    public void notifyRole(Class<? extends IRole> roleClass, String message) {
        for (WerewolfPlayer p : this.getPlayersByRole(roleClass)) {
            notifyPlayer(p, message);
        }
    }

    private BurgerVoteScreen burgerVoteScreen;

    private void updateStatus(GameStatus newStatus) {
        this.gamestatus = newStatus;
        plugin.getScoreboardManager().updateScoreboards(this);
    }

    public void startFirstNight() {
        List<WerewolfPlayer> cupidos = getPlayersByRole(CupidoRole.class);
        WaitTillAllReady allWaiter = plugin.setupWaiter(cupidos.size(), 30, "Waiting for cupido(s)", null);

        updateStatus(GameStatus.CUPIDO);
        for (WerewolfPlayer cupido : cupidos) {
            CupidoScreen cupidoScreen = new CupidoScreen(this);
            WaitTillAllReady waiter = plugin.setupWaiter(1, 30, "Waiting for cupido to shoot his arrows [%time%]", () -> {
                cupidoScreen.selectRandom(cupido.getPlayer());
                allWaiter.markReady(cupido);
            });

            cupidoScreen.prepareInternalInventory(waiter);
            cupidoScreen.openInventory(cupido.getPlayer());
        }
    }

    public void startDayVote() {
        updateStatus(GameStatus.DAY);
        if (burgerVoteScreen != null) {
            HandlerList.unregisterAll(burgerVoteScreen);
        }

        burgerVoteScreen = new BurgerVoteScreen(this);
        Bukkit.getPluginManager().registerEvents(burgerVoteScreen, plugin);

        for (WerewolfPlayer werewolfPlayer : playerList) {
            burgerVoteScreen.openInventory(werewolfPlayer.getPlayer());
        }

        plugin.setupWaiter(playerList.size(), 60, "Waiting for everyone to cast there vote [%time%]", () -> {
            burgerVoteScreen.closeInventory();

            Collection<String> voteList = burgerVoteScreen.getVoteMap().values();
            Map<String, Long> collect = voteList.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            String highest = "";
            long currentMax = Long.MIN_VALUE;
            for (String key : collect.keySet()){
                if(highest.isEmpty()) highest = key;

                if(currentMax < collect.get(key)){
                    currentMax = collect.get(key);
                    highest = key;
                }
            }

            String finalHighest = highest;

//            deathTeller.newStory();
            new BossBarTimer(plugin, highest + " has been voted for.", 10, () -> {
                WerewolfPlayer p = getPlayerByName(finalHighest);
                if(p != null){
                    deathTeller.addDeath(p.getPlayer(), EnumDeadType.VOTE);
                }

                List<Player> leftPlayersAreGone = new ArrayList<>(leftPlayers);
                for (Player ghost : leftPlayersAreGone) {
                    deathTeller.addDeath(ghost, EnumDeadType.LEFT);
                }

                deathTeller.tellStory(this);
            }, playerList);

            HandlerList.unregisterAll(burgerVoteScreen);
            burgerVoteScreen = null;
        });
    }

    public void showDayVote(Player player) {
        if (burgerVoteScreen != null) {
            burgerVoteScreen.openInventory(player);
        } else {
            notifyPlayer(player, "There is no vote active !");
        }
    }

    public void startDefaultNightActivities() {
        centerPlayers();
//        deathTeller.newStory();

        updateStatus(GameStatus.WEREWOLFVOTE);

        notifyRole(WerewolfRole.class, "Stand by a player you want to vote for");

        NearbySelector dinnerSelector = new NearbySelector(this, WerewolfRole.class);
        plugin.setupWaiter(1, 30, "The wolves are selecting a player from the menu [%time%]", () -> {
            dinnerSelector.stop();

            wolvesHaveEaten(dinnerSelector);
            centerPlayers();
        });
        dinnerSelector.start();
    }

    //    private Map<Player, EnumDeadType> deaths = new HashMap<>();
    private DeathTeller deathTeller = new DeathTeller();

    private void wolvesHaveEaten(NearbySelector selectorWolfs) {
        Player food = selectorWolfs.getTopSelectedPlayer();
        notifyRole(WerewolfRole.class, "Selected target is: " + food.getDisplayName());

        deathTeller.addDeath(food, EnumDeadType.WOLVES);
//        deaths.put(food, EnumDeadType.WOLVES);

        new BossBarTimer(plugin, "Wolves have selected a target", 5, () -> {
            List<WerewolfPlayer> witchList = getPlayersByRole(WitchRole.class);
            if (!witchList.isEmpty()) {
                updateStatus(GameStatus.WITCHACTIVITY);
//                List<AskScreen> elixerScreens = new ArrayList<>();
//
//                WaitTillAllReady witchReviveCounter = plugin.setupWaiter(witchList.size(), 30, "It's now to the witch to see what she is going to do [%time%]", () -> {
//                    elixerScreens.forEach(AskScreen::closeInventory);
//                    askWitchToKill();
//                });
//
//                for (WerewolfPlayer witch : witchList) {
//                    if (witch.isAlive()) {
//                        WitchRole roleInfo = (WitchRole) witch.getRole();
//                        if (roleInfo.hasElixer()) {
//                            AskScreen askScreen = new AskScreen(this, food.getDisplayName() + " has been killed, revive him ?");
//                            askScreen.setFinishAction((event) -> {
//                                if (askScreen.saidYes) {
//                                    roleInfo.consumeElixer();
//                                    deaths.remove(food);
//
//                                    // Remove all screens from other witches
//                                    witchList.forEach(witchReviveCounter::markReady);
//                                }
//
//                                witchReviveCounter.markReady(witch);
//                            });
//
//                            elixerScreens.add(askScreen);
//                            askScreen.openInventory(witch.getPlayer());
//                        } else {
//                            witchReviveCounter.markReady(witch);
//                        }
//                    } else {
//                        witchReviveCounter.markReady(witch);
//                    }
//                }

                // check if a witch is alive
                if (witchList.stream().anyMatch(WerewolfPlayer::isAlive)) {
                    for (WerewolfPlayer witch : witchList) {
                        if (witch.isAlive()) {
                            if (((WitchRole) witch.getRole()).hasElixer()) {
                                AskScreen askScreen = new AskScreen(this, food.getName() + " has been killed, revive him ?");
                                askScreen.openInventory(witch.getPlayer());

                                askScreen.prepareInternalInventory(plugin.setupWaiter(1, 30, "It's now to the witch to see what she is going to do [%time%]", () -> {
                                    askScreen.closeInventory();

                                    if (askScreen.saidYes) {
                                        ((WitchRole) witch.getRole()).consumeElixer();
                                        deathTeller.healPlayer(food, WITCH);
                                    }

                                    askWitchToKill();
                                }));
                            } else { // has no healing potion
                                askWitchToKill();
                            }
                        }
                    }
                } else {
                    // no witch alive
                    theNightHasPassed();
                }
            }
        }, getPlayerList());
    }

    private void askWitchToKill() {
        List<WerewolfPlayer> witchList = getPlayersByRole(WitchRole.class).stream().filter(WerewolfPlayer::isAlive).collect(Collectors.toList());
        if (!witchList.isEmpty()) {
            updateStatus(GameStatus.WITCHACTIVITY);
//
//            List<WerewolfPlayer> witchWantToKill = new ArrayList<>();
//            List<AskScreen> askScreens = new ArrayList<>();
//
//            WaitTillAllReady waitTillAllReady = plugin.setupWaiter(witchList.size(), 15, "Does the witch wants to kill someone [%time%]", () -> {
//                askScreens.forEach(AskScreen::closeInventory);
//
//                if(witchWantToKill.isEmpty()) {
//                    theNightHasPassed();
//                }else{
//                    witchWantToKill(witchWantToKill);
//                }
//            });
//
//
//            for (WerewolfPlayer witch : witchList) {
//                if (witch.isAlive()) {
//                    WitchRole roleInfo = (WitchRole) witch.getRole();
//                    if (roleInfo.hasPoison()) {
//                        AskScreen killSomeone = new AskScreen(this, "Would you like to kill someone?");
//                        killSomeone.setFinishAction((event) -> {
//                            if (killSomeone.saidYes) {
//                                roleInfo.consumePoison();
//                                witchWantToKill.add(witch);
//                            }
//
//                            waitTillAllReady.markReady(witch);
//                        });
//                        askScreens.add(killSomeone);
//                        killSomeone.openInventory(witch.getPlayer());
//                    }

            WerewolfPlayer witch = witchList.get(0);
            WitchRole roleInfo = (WitchRole) witch.getRole();
            if (roleInfo.hasPoison()) {
                AskScreen killSomeone = new AskScreen(this, "Would you like to kill someone");
                killSomeone.openInventory(witch.getPlayer());
                killSomeone.prepareInternalInventory(plugin.setupWaiter(1, 30, "Does the witch wants to kill someone [%time%]", () -> {
                    killSomeone.closeInventory();

                    if (killSomeone.saidYes) {
                        centerPlayers();

                        NearbySelector witchTarget = new NearbySelector(this, WitchRole.class);
                        plugin.setupWaiter(1, 30, "The witch wants to kill someone [%time%]", () -> {
                            witchTarget.stop();

                            roleInfo.consumePoison();
//                            deaths.put(witchTarget.getTopSelectedPlayer(), WITCH);
                            deathTeller.addDeath(witchTarget.getTopSelectedPlayer(), WITCH);

                            theNightHasPassed();
                        });
                        witchTarget.start();
                    } else {
                        theNightHasPassed();
                    }
                }));
            } else {
                theNightHasPassed();
            }
        }
    }


//    private void witchWantToKill(List<WerewolfPlayer> killers) {
//        centerPlayers();
//
//        NearbySelector targetSelector = new NearbySelector(this, killers.stream().map(WerewolfPlayer::getPlayer).collect(Collectors.toList()));
//        plugin.setupWaiter(1, 30, "The witch wants to kill someone [%time%]", () -> {
//            targetSelector.stop();
//
//            targetSelector.getSelectedPlayers().forEach(deadPlayer -> deaths.put(deadPlayer, EnumDeadType.WITCH));
////            wolvesHaveEaten(targetSelector);
//            centerPlayers();
//
//            theNightHasPassed();
//        });
//        targetSelector.start();
//    }


    private void theNightHasPassed() {
        updateStatus(GameStatus.DAY);
        deathTeller.tellStory(this);
    }

    public DeathTeller getDeathTeller(){
        return this.deathTeller;
    }
}

