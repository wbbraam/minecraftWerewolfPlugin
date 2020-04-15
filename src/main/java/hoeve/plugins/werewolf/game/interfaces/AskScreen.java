package hoeve.plugins.werewolf.game.interfaces;

import hoeve.plugins.werewolf.game.helpers.Triple;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.helpers.ItemHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * Created by DeStilleGast 13-4-2020
 */
public class AskScreen extends BaseScreen {

    private ItemStack yesItem, noItem;

    private String yesText, noText;

    public boolean saidYes = false;

    private Consumer<Triple<Player, ItemStack, InventoryClickEvent>> finishAction;

    public AskScreen(WerewolfGame game, String title) {
        this(game, title, "Yes", "No");
    }

    public AskScreen(WerewolfGame game, String title, String yesText, String noText) {
        super(game, title);

        this.yesText= yesText;
        this.noText = noText;

        finishAction = (item) -> {
//            saidYes = item.getSecond().equals(yesItem);

            waitTillAllReady.markReady(game.getPlayer(item.getFirst()));
        };
    }

    @Override
    protected void onInventoryClick(Player player, ItemStack itemStack, InventoryClickEvent e) {
        if(itemStack.equals(yesItem) || itemStack.equals(noItem)){
            saidYes = itemStack.equals(yesItem);
//
//            waitTillAllReady.markReady(game.getPlayer(player));
            finishAction.accept(new Triple<>(player, itemStack, e));
        }
    }

    @Override
    protected void prepareInventory() {
        yesItem = new ItemStack(Material.LIME_WOOL);
        noItem = new ItemStack(Material.RED_WOOL);

        ItemHelper.rename(yesItem, yesText);
        ItemHelper.rename(noItem, noText);

        myInv.setItem(0, noItem);
        myInv.setItem(1, noItem);

        myInv.setItem(7, yesItem);
        myInv.setItem(8, yesItem);
    }

    public void setFinishAction(Consumer<Triple<Player, ItemStack, InventoryClickEvent>> action){
        this.finishAction = action;
    }
}
