package hoeve.plugins.werewolf.game.interfaces;

import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.ItemHelper;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;
import hoeve.plugins.werewolf.game.roles.CupidoRole;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by DeStilleGast 12-4-2020
 */
public class CupidoScreen extends BaseScreen {

    private ItemStack acceptItem = new ItemStack(Material.RED_WOOL);
    private Enchantment magicEffect = Enchantment.LUCK;

    private boolean isFinishedSelecting = false;


    public CupidoScreen(WerewolfGame game) {
        super(game, "Who are the loved one", (int) Math.ceil(game.getPlayerList().size() / 9D) + 2);

        ItemHelper.rename(acceptItem, "Couple these people");
        ItemHelper.setLore(acceptItem, "You cannot change your mind when you have coupled 2 people");
    }

    @Override
    public void onInventoryClick(Player player, ItemStack itemStack, InventoryClickEvent e) {
        if (itemStack.getItemMeta() instanceof SkullMeta) {
            int glowCount = 0;

            for (int i = 0; i < myInv.getSize(); i++) {
                ItemStack item = myInv.getItem(i);
                if (item == null) continue;

                if (item.containsEnchantment(magicEffect)) glowCount++;
            }

            if (itemStack.containsEnchantment(magicEffect)) {
                itemStack.removeEnchantment(magicEffect);
                glowCount -= 1;
            } else {
                if (glowCount <= 1) {
                    itemStack.addUnsafeEnchantment(magicEffect, 1);
                    glowCount += 1;
                }
            }

//            for (int i = 1; i ; i++) {
                myInv.setItem(myInv.getSize() - 9, new ItemStack(Material.AIR));
                myInv.setItem(myInv.getSize() - 8, new ItemStack(Material.AIR));
//            }
//
            List<String> selectedPlayerNames = new ArrayList<>();

            if(glowCount == 2){
                acceptItem.setType(Material.LIME_WOOL);
            }else{
                acceptItem.setType(Material.RED_WOOL);
            }

            for (int i = 0; i < myInv.getSize() - 9; i++) {
                ItemStack skull = myInv.getItem(i);
                if (skull != null && skull.getType() == Material.PLAYER_HEAD) {
                    if (skull.containsEnchantment(magicEffect)) {
                        skull = skull.clone();
                        skull.removeEnchantment(magicEffect);
                        myInv.setItem(myInv.getSize() - 9 + selectedPlayerNames.size(), skull);
                        selectedPlayerNames.add(((SkullMeta)skull.getItemMeta()).getOwningPlayer().getName());
                    }
                }
            }


            ItemHelper.setLore(acceptItem, "You cannot change your mind when you have coupled these 2 people:", "", String.join(" and ",selectedPlayerNames));
            myInv.setItem(myInv.getSize() - 1, acceptItem);
        }else if (itemStack.equals(acceptItem)){
            if(acceptItem.getType() == Material.LIME_WOOL){

                ItemStack item1 = myInv.getItem(myInv.getSize() - 9); // first head
                ItemStack item2 = myInv.getItem(myInv.getSize() - 8); // second head

                WerewolfPlayer wereWolf1 = game.getPlayerByName(((SkullMeta)item1.getItemMeta()).getOwningPlayer().getName());
                WerewolfPlayer wereWolf2 = game.getPlayerByName(((SkullMeta)item2.getItemMeta()).getOwningPlayer().getName());

                selectPlayers(wereWolf1, wereWolf2);

                waitTillAllReady.markReady(game.getPlayer(player));
                player.closeInventory();
            }
        }
    }


    public void prepareInventory() {
        this.isFinishedSelecting = false;

        myInv.clear();

        game.getPlayerList().forEach(p -> {
            if (!(p.getRole() instanceof CupidoRole)) {
                addItem(createHead(p.getPlayer()));
            }
        });

        for (int i = 0; i < 9; i++) {
            myInv.setItem(myInv.getSize() - 10 - i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }

        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemHelper.rename(infoItem, "Information");
        ItemHelper.setLore(infoItem, "Select 2 people by clicking on there head", "when you have clicked on a head", "there head will be displayed on the lowest line", "when there are 2 heads selected you can click", "the green wol to confirm your selection", "", "You can select a head again to deselect it");

        myInv.setItem(myInv.getSize() - 2, infoItem);
        myInv.setItem(myInv.getSize() - 1, acceptItem);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(e.getInventory().getHolder() == this){
            if(!isFinishedSelecting)
                Bukkit.getScheduler().runTask(game.getPlugin(), () -> openInventory((Player) e.getPlayer()));
        }
    }

    public void selectRandom(Player p){
        if(!isFinishedSelecting){

            isFinishedSelecting = true; // onInventoryClose will reopen the screen if we dont set it on finish
            this.closeInventory();

            List<WerewolfPlayer> playerList = game.getPlayerList().stream().filter(pl -> p != pl.getPlayer()    /*!(pl.getRole() instanceof CupidoRole)*/).collect(Collectors.toList());
            Collections.shuffle(playerList);

            selectPlayers(playerList.get(0), playerList.get(1));
        }
    }

    private void selectPlayers(WerewolfPlayer p1, WerewolfPlayer p2){
        p1.setLover(p2);
        p2.setLover(p1);

        game.notifyGameMaster("Cupido made " + p1.getName() + " and " + p2.getName() + " a couple");

        isFinishedSelecting = true;
    }
}
