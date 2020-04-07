package hoeve.plugins.werewolf;

import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class WerewolfPlugin extends JavaPlugin{

    private CommandKit commandKit;
    private WerewolfGame werewolfGame;


    @Override
    public void onEnable() {
        getLogger().info("*********************************");
        getLogger().info("*** Booting Werewolf plugin *****");
        getLogger().info("*********************************");
        getLogger().info("* Version: 0.1 ");
        getLogger().info("* Author: Scouting de Hoeve ");
        getLogger().info("* Date: 06-04-2020 ");
        getLogger().info("*********************************");

        // Setup game core
        werewolfGame = new WerewolfGame(this);
        Bukkit.getPluginManager().registerEvents(werewolfGame, this);

        // Setup command
        PluginCommand wereWolfCommand = this.getCommand("werewolf");
        commandKit = new CommandKit(werewolfGame, this);
        if(wereWolfCommand != null) {
            wereWolfCommand.setExecutor(commandKit);
        }



    }

    @Override
    public void onDisable() {
        getLogger().info("Closing werewolf plugin!");
    }


    /**
     * Setup a waiter, {@literal whenAllPlayersAreSet} will be run if time has expired or when all players are marked ready
     * @param playerListSize Current playerlist size
     * @param maxWaitTime Max amount of waiting time
     * @param whenAllPlayersAreSet What needs to be executed if everything is set
     * @return Waiter object for the players
     */
    public WaitTillAllReady setupWaiter(int playerListSize, int maxWaitTime, Runnable whenAllPlayersAreSet){
        WaitTillAllReady wtar = new WaitTillAllReady(playerListSize, maxWaitTime, whenAllPlayersAreSet);

        // Before you ask, why *20, the server (should always be) run on 20 ticks per seconds, this task uses ticks for its calculation
        // 20 = 1 second
        // 40 = 2 seconds
        // 80 = 4 seconds
        Bukkit.getScheduler().runTaskLater(this, wtar::allSet, maxWaitTime * 20);
        return wtar;
    }

    public void tellPlayer(CommandSender player, String message){
        player.sendMessage("[Game] " + message);
    }

    public void tellPlayer(WerewolfPlayer player, String message){
        this.tellPlayer(player.getPlayer(), message);
    }

}
