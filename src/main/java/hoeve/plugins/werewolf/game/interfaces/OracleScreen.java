package hoeve.plugins.werewolf.game.interfaces;

import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.BossBarTimer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

/**
 * Created by DeStilleGast 17-4-2020
 */
public class OracleScreen extends BurgerVoteScreen {

    public OracleScreen(WerewolfGame game) {
        super(game, "Magic glass ball");
    }

    @Override
    protected void onInventoryClick(Player player, ItemStack itemStack, InventoryClickEvent e) {
        if(game.getPlayer(player).isAlive()) {
            if (itemStack.getType() == Material.PLAYER_HEAD) {

                WerewolfPlayer selectedPlayer = game.getPlayerByName(itemStack.getItemMeta().getDisplayName());
                if(selectedPlayer != null) {
                    String text = selectedPlayer.getPlayer().getDisplayName() + " looks like a " + selectedPlayer.getRole().getRoleName();
                    player.sendTitle("Glass ball said", text, 10, 70, 20);
                    new BossBarTimer(game.getPlugin(), ChatColor.AQUA + "[GlassBall] " + ChatColor.RESET + text, 30, null, Collections.singletonList(game.getPlayer(player)));

                    waitTillAllReady.markReady(game.getPlayer(player));
                    closeInventory();
                }
            }
        }
    }
}
