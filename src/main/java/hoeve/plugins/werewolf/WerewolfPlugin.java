package hoeve.plugins.werewolf;

import hoeve.plugins.werewolf.game.WerewolfGame;
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
        werewolfGame = new WerewolfGame();

        // Setup command
        PluginCommand wereWolfCommand = this.getCommand("werewolf");
        commandKit = new CommandKit(werewolfGame);
        if(wereWolfCommand != null) {
            wereWolfCommand.setExecutor(commandKit);
        }



    }

    @Override
    public void onDisable() {
        getLogger().info("Closing werewolf plugin!");
    }

}
