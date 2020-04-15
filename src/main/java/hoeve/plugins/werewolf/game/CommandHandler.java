package hoeve.plugins.werewolf.game;

import hoeve.plugins.werewolf.WerewolfPlugin;
import hoeve.plugins.werewolf.game.actions.NearbySelector;
import hoeve.plugins.werewolf.game.interfaces.CupidoScreen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Created by DeStilleGast 12-4-2020
 */
public class CommandHandler implements CommandExecutor, TabCompleter {

    private final WerewolfGame werewolfGame;
    private final WerewolfPlugin plugin;

//    private WerewolfScreen wwScreen;
    private CupidoScreen cupidoScreen;
    private NearbySelector nearbySelector;


    public CommandHandler(WerewolfGame werewolfGame, WerewolfPlugin plugin) {
        this.werewolfGame = werewolfGame;
        this.plugin = plugin;
    }

    private void notify(CommandSender sender, String message){
        sender.sendMessage(message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            boolean hasLeader = werewolfGame.getGameMaster() != null;

            if(args.length == 0){

                return true;
            }

            switch (args[0].toLowerCase()){
                case "new":

                    if(hasLeader){
                        notify(sender, "There is already a game running");
                    }else{
                        werewolfGame.setGameMaster(player);
                        plugin.getScoreboardManager().updateScoreboards(werewolfGame);
                    }

                    break;
                case "join":
                    if(hasLeader){
                        if(!werewolfGame.addPlayer(player)){
                            notify(player, "You are already in the game, please wait...");
                        }else{
                            plugin.getScoreboardManager().updateScoreboards(werewolfGame);
                        }
                    }else{
                        notify(player, "There is no game active");
                    }
                    break;
                case "leave":
                    if(werewolfGame.removePlayer(player)){
                        notify(player, "You left the game");
                        plugin.getScoreboardManager().updateScoreboards(werewolfGame);

                    }else{
                        notify(player, "You are not even in the game");
                    }
                    break;
                case "vote":
                    if(hasLeader) {
                        if (sender.equals(werewolfGame.getGameMaster().getPlayer())) {
                            werewolfGame.startDayVote();
                        } else {
                            werewolfGame.showDayVote(player);
                        }
                    }

                    break;
                case "start":
                    if(hasLeader) {
                        werewolfGame.centerPlayers();
                        werewolfGame.startGame();
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
                    }else{
                        notify(player, "There is no game active");
                    }
                    break;
                case "center":
                    werewolfGame.centerPlayers();
                    break;
                case "night":
                    werewolfGame.startDefaultNightActivities();
                    break;
                case "roles":
                    for (WerewolfPlayer pl : werewolfGame.getPlayerList()) {
                        notify(sender, pl.getPlayer().getName() + " -  " + pl.getRole().getRoleName());
                    }
                    break;
                case "kill":
                    if(hasLeader) {
                        if (sender.equals(werewolfGame.getGameMaster().getPlayer())) {
                            if(args.length == 1){
                                notify(sender, "Please specify a name");
                            }else {
                                WerewolfPlayer target = werewolfGame.getPlayerByName(args[1]);
                                if(target != null){
                                    werewolfGame.getDeathTeller().addDeath(target.getPlayer(), EnumDeadType.GAMEMASTER);
                                }else{
                                    notify(sender, "Could not find player");
                                }
                            }
                        }
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

        return Arrays.asList("new", "start", "join", "leave");
    }
}
