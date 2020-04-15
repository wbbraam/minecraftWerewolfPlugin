package hoeve.plugins.werewolf.game;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by DeStilleGast 13-4-2020
 */
public class ChatManager implements Listener {

    private WerewolfGame game;

    public ChatManager(WerewolfGame game) {
        this.game = game;

        Bukkit.getPluginManager().registerEvents(this, game.getPlugin());
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event){
//        event.setCancelled(true);
    }
}
