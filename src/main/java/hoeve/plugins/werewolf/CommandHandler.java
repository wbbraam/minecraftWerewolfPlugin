package hoeve.plugins.werewolf;

import hoeve.plugins.werewolf.game.EnumDeadType;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by DeStilleGast 12-4-2020
 */
public class CommandHandler implements CommandExecutor, TabCompleter {

    private final WerewolfGame werewolfGame;
    private final WerewolfPlugin plugin;


    public CommandHandler(WerewolfGame werewolfGame, WerewolfPlugin plugin) {
        this.werewolfGame = werewolfGame;
        this.plugin = plugin;
    }

    private void notify(CommandSender sender, String message) {
        werewolfGame.notifyPlayer(sender, message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            boolean hasLeader = werewolfGame.getGameMaster() != null;

            if (args.length == 0) {
                return true;
            }

            if (hasLeader) {
                switch (args[0].toLowerCase()) {
                    case "new":
                        notify(sender, "There is already a game running");

                        break;
                    case "join":
                        if (!werewolfGame.addPlayer(player)) {
                            notify(player, "You are already in the game, please wait...");
                        } else {
                            plugin.getScoreboardManager().updateScoreboards(werewolfGame);
                            notify(player, "You have joined the game");
                        }

                        break;
                    case "leave":
                        if (werewolfGame.removePlayer(player)) {
                            notify(player, "You left the game");
                            plugin.getScoreboardManager().updateScoreboards(werewolfGame);
                        } else {
                            notify(player, "You are not even in the game");
                        }
                        break;
                    case "vote":
                        if (sender.equals(werewolfGame.getGameMaster().getPlayer())) {
                            werewolfGame.startDayVote();
                        } else {
                            werewolfGame.showDayVote(player);
                        }

                        break;
                    case "start":
                        werewolfGame.startGame();
                        werewolfGame.centerPlayers();
//
//                        if (nearbySelector == null) {
//                            nearbySelector = new NearbySelector(werewolfGame, WerewolfRole.class);
//                            nearbySelector.start();
//                        } else {
//                            nearbySelector.stop();
//                            werewolfGame.notifyGameMaster(nearbySelector.getTopSelectedPlayer().getName() + " was selected");
//                        }


//                    if(oracleScreen == null) {
//                        oracleScreen = new OracleScreen(werewolfGame);
//                        Bukkit.getPluginManager().registerEvents(oracleScreen, plugin);
//                    }
////                    WaitTillAllReady wtar = plugin.setupWaiter(1, 30, "Waiting for cupido to select a couple [%time%]", () -> cupidoScreen.selectRandom(player));
//                    WaitTillAllReady wtar = plugin.setupWaiter(1, 30, "Waiting for the seeer to select someone [%time%]", player::closeInventory);
//                    oracleScreen.prepareInternalInventory(wtar);
//
//                    oracleScreen.openInventory(player);

//                    wwScreen.prepareInventory(werewolfGame);

//                    werewolfGame.getPlayerList().forEach(w -> wwScreen.openInventory((Player) w.getPlayer()));
                        break;
                    case "center":
                        werewolfGame.centerPlayers();
                        break;
                    case "night":
                        werewolfGame.startDefaultNightActivities();
                        break;
                    case "roles":
                        for (WerewolfPlayer pl : werewolfGame.getPlayerList()) {
                            if(pl.equals(werewolfGame.getGameMaster())) continue;

                            notify(sender, pl.getPlayer().getName() + " -  " + pl.getRole().getRoleName());
                        }
                        break;
                    case "kill":
                    case "kick":
                        if (sender.equals(werewolfGame.getGameMaster().getPlayer())) {
                            if (args.length == 1) {
                                notify(sender, "Please specify a name");
                            } else {
                                WerewolfPlayer target = werewolfGame.getPlayerByName(args[1]);
                                if (target != null) {
                                    werewolfGame.getDeathTeller().addDeath(target.getPlayer(), EnumDeadType.GAMEMASTER);
                                    notify(sender, "Player will be death on by the next story");
                                } else {
                                    notify(sender, "Could not find player");
                                }
                            }
                        }
                        break;
                    case "test":
                        werewolfGame.checkIfGameHasEnded();
                        break;
                    case "end":
                        werewolfGame.finishGame();
                        notify(sender, "Game ended");

                }
            } else {
                if ("new".equals(args[0].toLowerCase()) && player.hasPermission("werewolf.gamemaster")) {
                    werewolfGame.setGameMaster(player);
                    plugin.getScoreboardManager().updateScoreboards(werewolfGame);
                    werewolfGame.notifyPlayer(sender, "New game started");
                }
            }


//            wwScreen.prepareInventory(werewolfGame);
//            wwScreen.openInventory(player);


//            plugin.setupWaiter(1, 30, () -> {
//                sender.sendMessage("done");
//            });


        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
//        switch (args.length){
//            case 1:
//                return Lists.asList("new", "start", "join");
//        }

        if(werewolfGame.getGameMaster() != null){
            return Collections.singletonList("new");
        }

        if(sender.hasPermission("werewolf.gamemaster")){
            return Arrays.asList("start", "night", "vote");
        }

        return Arrays.asList("vote", "leave");
    }
}
