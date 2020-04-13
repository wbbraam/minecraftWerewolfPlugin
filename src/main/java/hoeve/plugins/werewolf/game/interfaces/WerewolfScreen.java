package hoeve.plugins.werewolf.game.interfaces;

import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.roles.WereWolfRole;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by DeStilleGast 12-4-2020
 */
public class WerewolfScreen extends BaseScreen {

    public WerewolfScreen(WerewolfGame game){
        super(game,"Who is dead next day ?", (int) Math.ceil(game.getPlayerList().size() / 9D));
    }

    @Override
    public void onInventoryClick(Player player, ItemStack itemStack, InventoryClickEvent e) {

        itemStack.setAmount(itemStack.getAmount() + 1);

        player.sendMessage("You clicked at slot" + e.getRawSlot());
        player.closeInventory();

    }


    public void prepareInventory(){
        myInv.clear();

        for(WerewolfPlayer player : game.getPlayerList()){
            if(!(player.getRole() instanceof WereWolfRole)){
                myInv.addItem(createHead(player.getPlayer()));
            }
        }
    }
}
