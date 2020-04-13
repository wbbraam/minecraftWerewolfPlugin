package hoeve.plugins.werewolf.game.interfaces;

import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.ItemHelper;
import hoeve.plugins.werewolf.game.roles.SeeerRole;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Created by DeStilleGast 13-4-2020
 * @deprecated - Use nearbySelector to keep it more fun
 */

@Deprecated
public class OracleScreen extends BaseScreen {

    private WerewolfGame game;

    /**
     * @deprecated Use nearbySelector to keep more fun
     */
    @Deprecated
    public OracleScreen(WerewolfGame game) {
        super(game, "Magic glass ball", (int) Math.ceil(game.getPlayerList().size() / 9D));
        this.game = game;
    }

    @Override
    public void onInventoryClick(Player player, ItemStack itemStack, InventoryClickEvent e) {
        if(itemStack.getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

            WerewolfPlayer wolfPlayer = game.getPlayer((Player) skullMeta.getOwningPlayer());
            if(wolfPlayer != null) {
                waitTillAllReady.markReady(game.getPlayer(player));

                player.closeInventory();
                game.notifyGameMaster("Oracle has selected to see the role of " + wolfPlayer.getPlayer().getName());
//                game.notifyPlayer(player, wolfPlayer.getPlayer().getName() + " is a " + wolfPlayer.getRole().getRoleName());
                player.sendTitle(wolfPlayer.getRole().getRoleName(), "Your glass ball is fading this word", 10, 70, 20);
            }
        }
    }

    @Override
    public void prepareInventory(){
        myInv.clear();
        game.getPlayerList().forEach(p -> {
            if (!(p.getRole() instanceof SeeerRole)) {
                addItem(ItemHelper.rename(createHead(p.getPlayer()), "Show the role from " + p.getPlayer().getName()));
            }
        });
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(e.getInventory().getHolder() == this){
            if(!waitTillAllReady.hasRun()) {
                game.notifyGameMaster("Oracle has decided to select no one for now");
                game.notifyPlayer((Player) e.getPlayer(), "You have decided to not use your glass ball");
            }
        }
    }
}
