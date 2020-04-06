package hoeve.plugins.werewolf;

import hoeve.plugins.werewolf.game.WerewolfGame;
import org.bukkit.plugin.java.JavaPlugin;

public class WerewolfPlugin extends JavaPlugin{

    @Override
    public void onEnable() {
        getLogger().info("*********************************");
        getLogger().info("*** Booting Werewolf plugin *****");
        getLogger().info("*********************************");
        getLogger().info("* Version: 0.1 ");
        getLogger().info("* Author: Scouting de Hoeve ");
        getLogger().info("* Date: 06-04-2020 ");
        getLogger().info("*********************************");



        this.getCommand("werewolf").setExecutor(new CommandKit());
    }

    @Override
    public void onDisable() {
        getLogger().info("Closing werewolf plugin!");
    }

}
