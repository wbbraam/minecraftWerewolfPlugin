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
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static hoeve.plugins.werewolf.game.EnumDeadType.WITCH;

public class WerewolfGame implements Listener {

    private WerewolfPlugin plugin;

    private List<WerewolfPlayer> playerList;
    private WerewolfCardDeck cardDeck;
    //    private CommandSender leaderName = Bukkit.getConsoleSender();
    private WerewolfPlayer gameMaster = null;
    private ParticleManager particleManager;

    private DeathTeller deathTeller = new DeathTeller();
    private List<Player> leftPlayers = new ArrayList<>();
    private BurgerVoteScreen burgerVoteScreen;

    private GameStatus gamestatus;

    private Location gameCenter = null;

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

        Location gameMasterLocation = gameMaster.getPlayer().getLocation();

        // search campfire
        int radius = 15;
        for (int x = gameMasterLocation.getBlockX() - radius; x <= gameMasterLocation.getBlockX() + radius; x++) {
            for (int y = gameMasterLocation.getBlockY() - radius; y <= gameMasterLocation.getBlockY() + radius; y++) {
                for (int z = gameMasterLocation.getBlockZ() - radius; z <= gameMasterLocation.getBlockZ() + radius; z++) {
//                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                    if (gameMasterLocation.getWorld().getBlockAt(x, y, z).getType() == Material.CAMPFIRE) {
                        gameCenter = new Location(gameMasterLocation.getWorld(), x, y, z).add(0.5, 0, 0.5);
                    }
                }
            }
        }

        if (gameCenter == null) {
            notifyGameMaster("Could not find campfire !!");
            return;
        }


        for (WerewolfPlayer werewolfPlayer : playerList) {
            werewolfPlayer.setAlive(true);
            werewolfPlayer.setRole(null);
            werewolfPlayer.setLover(null);

            werewolfPlayer.getPlayer().setGameMode(GameMode.ADVENTURE);
        }

        playerList.get(2).setRole(new WerewolfRole());
        playerList.get(1).setRole(new WitchRole());
        playerList.get(0).setRole(new CupidoRole());

        for (int i = 3; i < playerList.size(); i++) {
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
    public void prepareNewGame(Player newGameMaster) {
        gameMaster = new WerewolfPlayer(newGameMaster);
        updateStatus(GameStatus.PLAYERSELECT);
        plugin.getScoreboardManager().addPlayer(newGameMaster);
    }

    public WerewolfPlayer getGameMaster() {
        return gameMaster;
    }

    public void notifyGameMaster(String message) {
        BaseComponent[] messageObject = new ComponentBuilder("[").color(ChatColor.GOLD)
                .append("WereWolfGame").color(ChatColor.YELLOW)
                .append(" | ").color(ChatColor.GOLD)
                .append("GameMaster").color(ChatColor.YELLOW)
                .append("]").color(ChatColor.GOLD)
                .append(" ").reset()
                .appendLegacy(message).create();

        gameMaster.getPlayer().spigot().sendMessage(messageObject);
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

        // check if player is valid and was alive, notify GameMaster that someone has left the server
        if (wwPlayer != null) {
            if (wwPlayer.isAlive()) {
                wwPlayer.onPlayerLeave(this);
            }
        }

        return playerList.remove(wwPlayer);
    }

    public WerewolfPlayer getPlayer(Player player) {
        return playerList.stream().filter(p -> p.getPlayer() == player).findFirst().orElse(null);
    }

    public WerewolfPlayer getPlayerByName(String name) {
        return playerList.stream().filter(p -> ChatColor.stripColor(p.getPlayer().getName()).equals(ChatColor.stripColor(name))).findFirst().orElse(null);
    }

    public List<WerewolfPlayer> getPlayersByRole(Class<? extends IRole> roleClass) {
        return playerList.stream().filter(p -> p.getRole().getClass() == roleClass).collect(Collectors.toList());
    }

    /**
     * Clear playerlist
     */
    public void clearPlayerList() {
        playerList.clear();
    }


    /**
     * Get list of players from the game
     *
     * @return Copy of playerlist
     */
    public List<WerewolfPlayer> getPlayerList(boolean withGameMaster) {
        List<WerewolfPlayer> tmpList = new ArrayList<>(playerList);
        if(withGameMaster)
            tmpList.add(this.gameMaster);

        return tmpList;
    }

    /**
     * Finish the game
     */
    public void finishGame() {
        if (particleManager != null) {
            particleManager.stop();
        }

        for (WerewolfPlayer player : getPlayerList(true)) {
            removePlayer(player.getPlayer());
        }

        this.gameMaster = null;
    }

    /**
     * @return Get the current status of the game
     */
    public GameStatus getStatus() {
        return gamestatus;
    }


    // Server events
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        WerewolfPlayer leftPlayer = getPlayer(event.getPlayer());
        if (leftPlayer != null) {
            leftPlayers.add(event.getPlayer());

            plugin.getScoreboardManager().updateScoreboards(this);
        }
    }


    public WerewolfPlugin getPlugin() {
        return this.plugin;
    }


    public void notifyPlayer(CommandSender player, String message) {
        BaseComponent[] messageObject = new ComponentBuilder("[").color(ChatColor.GOLD)
                .append("WereWolfGame").color(ChatColor.YELLOW)
                .append("]").color(ChatColor.GOLD)
                .append(" ").reset()
                .appendLegacy(message).create();

        player.spigot().sendMessage(messageObject);
    }

    public void notifyPlayer(WerewolfPlayer player, String message) {
        notifyPlayer(player.getPlayer(), message);
    }

    public void notifyRole(Class<? extends IRole> roleClass, String message) {
        for (WerewolfPlayer p : this.getPlayersByRole(roleClass)) {
            notifyPlayer(p, message);
        }
    }


    public void centerPlayers() {
        int size = this.playerList.size();

        double theta = ((Math.PI * 2) / size);


        for (int i = 0; i < size; i++) {
            WerewolfPlayer player = this.playerList.get(i);
            if (!player.isAlive()) continue;

            double angle = (theta * i);

            int Radius = Math.max(size / 3, 3);
            double X = Radius * Math.cos(angle);
            double Z = Radius * Math.sin(angle);

            Location newPos = gameCenter.clone().add(X, 0, Z);

            while (gameCenter.getWorld().getBlockAt(newPos).getType() != Material.AIR) {
                newPos = newPos.add(0, 0.5, 0);
            }

            newPos.setDirection((gameCenter.clone().subtract(newPos.clone()).toVector()).normalize()); // look at center
            player.getPlayer().teleport(newPos, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

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
        updateStatus(GameStatus.BURGERVOTE);
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
            if (!voteList.isEmpty()) {
                Map<String, Long> collect = voteList.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

                String highest = "";
                long currentMax = Long.MIN_VALUE;
                for (String key : collect.keySet()) {
                    if (highest.isEmpty()) highest = key;

                    if (currentMax < collect.get(key)) {
                        currentMax = collect.get(key);
                        highest = key;
                    }
                }

                String finalHighest = highest;

//            deathTeller.newStory();
                new BossBarTimer(plugin, highest + " has been voted for.", 10, () -> {
                    WerewolfPlayer p = getPlayerByName(finalHighest);
                    if (p != null) {
                        deathTeller.addDeath(p.getPlayer(), EnumDeadType.VOTE);
                    }

                    tellDeathStory();
                }, getPlayerList(true));
            } else {
                new BossBarTimer(plugin, "Nobody has been voted for. So a random player will be selected", 10, () -> {
                    Random rnd = new Random();
                    WerewolfPlayer p = playerList.get(rnd.nextInt(playerList.size() - 1));

                    while (!p.isAlive()) {
                        p = playerList.get(rnd.nextInt(playerList.size() - 1));
                    }

                    deathTeller.addDeath(p.getPlayer(), EnumDeadType.VOTE);

                    tellDeathStory();
                }, getPlayerList(true));
            }

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

        NearbySelector dinnerSelector = new NearbySelector(this, WerewolfRole.class);
        plugin.setupWaiter(1, 30, "The wolves are selecting a player from the menu [%time%]", () -> {
            dinnerSelector.stop();

            wolvesHaveEaten(dinnerSelector);
            centerPlayers();
        });
        dinnerSelector.start();
    }


    private void wolvesHaveEaten(NearbySelector selectorWolfs) {
        Player food = selectorWolfs.getTopSelectedPlayer();
        notifyGameMaster("Werewolves have selected to eat: " + food.getDisplayName());
        notifyRole(WerewolfRole.class, "Selected target is: " + food.getDisplayName());

        deathTeller.addDeath(food, EnumDeadType.WOLVES);
//        deaths.put(food, EnumDeadType.WOLVES);

        new BossBarTimer(plugin, "Wolves have selected a target", 5, () -> {
            List<WerewolfPlayer> witchList = getPlayersByRole(WitchRole.class);
            if (!witchList.isEmpty()) {
                updateStatus(GameStatus.WITCHACTIVITY);


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
                                        notifyGameMaster("Witch has decided to heal " + food.getDisplayName());
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
            } else {
                theNightHasPassed();
            }
        }, getPlayerList(true));
    }

    private void askWitchToKill() {
        List<WerewolfPlayer> witchList = getPlayersByRole(WitchRole.class).stream().filter(WerewolfPlayer::isAlive).collect(Collectors.toList());
        if (!witchList.isEmpty()) {
            updateStatus(GameStatus.WITCHACTIVITY);

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
                            notifyGameMaster("Witch has decided to kill " + witchTarget.getTopSelectedPlayer().getDisplayName());

                            theNightHasPassed();
                        });
                        witchTarget.start();
                    } else {
                        notifyGameMaster("Witch has decided not to kill anyone yet");
                        theNightHasPassed();
                    }
                }));
            } else {
                theNightHasPassed();
            }
        }
    }


    private void theNightHasPassed() {
        updateStatus(GameStatus.DAY);

        new BossBarTimer(plugin, "Everyone is waking up...", 10, this::tellDeathStory, getPlayerList(true));
    }

    private void tellDeathStory() {
        List<Player> leftPlayersAreGone = new ArrayList<>(leftPlayers);
        for (Player ghost : leftPlayersAreGone) {
            deathTeller.addDeath(ghost, EnumDeadType.LEFT);
        }

        deathTeller.tellStory(this, this::checkIfGameHasEnded);
    }

    public DeathTeller getDeathTeller() {
        return this.deathTeller;
    }


    public void checkIfGameHasEnded() {
        List<WerewolfPlayer> livingPlayers = playerList.stream().filter(WerewolfPlayer::isAlive).collect(Collectors.toList());
        boolean wolfWin = true, villagerWin = true;
        WerewolfPlayer lover = null;

        for (WerewolfPlayer livingPlayer : livingPlayers) {
            if (livingPlayer.isAlive()) {

                if (livingPlayer.getRole() instanceof WerewolfRole) {
                    // player is a werewolf, villagers lose
                    villagerWin = false;
                } else {
                    // check if player has a lover and not marked wolf as lose
                    // but if player has a lover and its lover is a werewolf, do nothing
                    if (livingPlayer.getLover() == null || !(livingPlayer.getLover().getRole() instanceof WerewolfRole)) {
                        wolfWin = false;
                    }
                }

                if (livingPlayer.getLover() != null) {
                    lover = livingPlayer.getLover();
                }
            }
        }

        if (wolfWin || villagerWin) {
            IRole winningRole = wolfWin ? new WerewolfRole() : new CommonRole();

            updateStatus(GameStatus.ENDED);

            for (WerewolfPlayer gamePlayer : getPlayerList(true)) {
                notifyPlayer(gamePlayer, winningRole.getRoleName() + ChatColor.RESET + " [" + playerList.stream().filter(WerewolfPlayer::isAlive).map(w -> w.getPlayer().getDisplayName()).collect(Collectors.joining(", ")) + "] has won the game !");
                if (lover != null) {
                    notifyPlayer(gamePlayer, "But that is not everything, " + ChatColor.LIGHT_PURPLE + lover.getPlayer().getDisplayName() + ChatColor.RESET + " and " + ChatColor.LIGHT_PURPLE + lover.getLover().getPlayer().getDisplayName() + ChatColor.RESET + " were a couple and can life long and forever.");
                }
            }


        } else {
            for (WerewolfPlayer hunter : getPlayersByRole(HunterRole.class)) {
                if (!hunter.isAlive() && !((HunterRole) hunter.getRole()).hasTakenRevenge()) {
                    new BossBarTimer(this.plugin, "But after the announcement something wierd happend", 10, () -> {
                        NearbySelector hunterSelector = new NearbySelector(this, Collections.singletonList(hunter.getPlayer()));
                        hunterSelector.isEveryoneVisible = true;

                        this.plugin.setupWaiter(1, 30, "The hunter is going to take revenge [%time%]", () -> {
                            hunterSelector.stop();
                            ((HunterRole) hunter.getRole()).setTakenRevenge();

                            deathTeller.addDeath(hunterSelector.getTopSelectedPlayer(), EnumDeadType.HUNTER);
                            this.tellDeathStory();
                        });

                        hunterSelector.start();


                    }, getPlayerList(true));
                }
            }
        }
    }
}

