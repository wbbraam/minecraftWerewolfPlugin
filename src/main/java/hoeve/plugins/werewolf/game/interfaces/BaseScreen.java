package hoeve.plugins.werewolf.game.interfaces;

import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.ItemHelper;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by DeStilleGast 12-4-2020
 */
public abstract class BaseScreen implements InventoryHolder, Listener {

    protected WerewolfGame game;
    protected WaitTillAllReady waitTillAllReady;

    protected Inventory myInv;

    public BaseScreen(WerewolfGame game, String title) {
        this(game, title, 1);
    }

    public BaseScreen(WerewolfGame game, String title, int rows) {
        this.game = game;
        this.myInv = Bukkit.createInventory(this, rows * 9, title);

        Bukkit.getPluginManager().registerEvents(this, game.getPlugin());
    }

    @Override
    public Inventory getInventory() {
        return myInv;
    }


    public void openInventory(Player player) {
        player.openInventory(myInv);
    }

    @EventHandler
    public void onInternalInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) return; // Not our inventory, ignore it

        // Cancel any action in that inventory screen
        e.setCancelled(true);

        ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null or air
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player p = (Player) e.getWhoClicked();


//        clickedItem.setAmount(clickedItem.getAmount() + 1);
//
//        p.sendMessage("You clicked at slot" + e.getRawSlot());
//        p.closeInventory();

        onInventoryClick(p, clickedItem, e);
    }

    protected abstract void onInventoryClick(final Player player, final ItemStack itemStack, final InventoryClickEvent e);

    public void prepareInternalInventory(WaitTillAllReady waitTillAllReady){
        this.waitTillAllReady = waitTillAllReady;
        prepareInventory();
    }

    protected abstract void prepareInventory();

    protected ItemStack createHead(Player player){
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwningPlayer(player); // ignore warning, Bukkit will create correct meta data

        skullMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS); // prepare item to hide enchantments, give us the ability to glow items without enchantments (it was ment for heads, it doesnt work any more :( )

        itemStack.setItemMeta(skullMeta);
        ItemHelper.rename(itemStack, player.getName());
        return itemStack;
    }

    protected ItemStack createHead(WerewolfPlayer player){
        if(player.isAlive()) return createHead(player.getPlayer());

        ItemStack itemStack = new ItemStack(Material.ZOMBIE_HEAD);
        ItemHelper.rename(itemStack, player.getName());
        return itemStack;
    }

    /**
     * Add item to inventory
     * @param items items to add to iventory
     * @return Items that could not be added {@see Inventory}
     */
    protected HashMap<Integer, ItemStack> addItem(ItemStack...items){
        return myInv.addItem(items);
    }

    public void closeInventory(){
        for (HumanEntity viewer : new ArrayList<>(myInv.getViewers())) {
            viewer.closeInventory();
        }

        HandlerList.unregisterAll(this);
    }
}
