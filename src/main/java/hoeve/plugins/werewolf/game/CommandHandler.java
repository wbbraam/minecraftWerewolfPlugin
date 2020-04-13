package hoeve.plugins.werewolf.game;

import hoeve.plugins.werewolf.WerewolfPlugin;
import hoeve.plugins.werewolf.game.actions.NearbySelector;
import hoeve.plugins.werewolf.game.interfaces.CupidoScreen;
import hoeve.plugins.werewolf.game.interfaces.OracleScreen;
import hoeve.plugins.werewolf.game.interfaces.WerewolfScreen;
import hoeve.plugins.werewolf.game.roles.WereWolfRole;
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

    private WerewolfScreen wwScreen;
    private CupidoScreen cupidoScreen;
    private OracleScreen oracleScreen;
    private NearbySelector nearbySelector;


    public CommandHandler(WerewolfGame werewolfGame, WerewolfPlugin plugin) {
        this.werewolfGame = werewolfGame;
        this.plugin = plugin;
    }

    private void notifiy(CommandSender sender, String message){
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
                        notifiy(sender, "There is already a game running");
                    }else{
                        werewolfGame.setGameMaster(player);
                        plugin.getScoreboardManager().updateScoreboards(werewolfGame);
                    }

                    break;
                case "join":
                    if(hasLeader){
                        if(!werewolfGame.addPlayer(player)){
                            notifiy(player, "You are already in the game, please wait...");
                        }else{
                            plugin.getScoreboardManager().updateScoreboards(werewolfGame);
                        }
                    }else{
                        notifiy(player, "There is no game active");
                    }
                    break;
                case "leave":
                    if(werewolfGame.removePlayer(player)){
                        notifiy(player, "You left the game");
                        plugin.getScoreboardManager().updateScoreboards(werewolfGame);

                    }else{
                        notifiy(player, "You are not even in the game");
                    }

                case "start":
                    werewolfGame.startGame();

//
                    if(nearbySelector == null){
                        nearbySelector = new NearbySelector(werewolfGame, WereWolfRole.class);
                        nearbySelector.start();
                    }else{
                        nearbySelector.stop();
                        werewolfGame.notifyGameMaster(nearbySelector.getTopSelectedPlayer().getName() + " was selected");
                    }

                    werewolfGame.centerPlayers();


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
