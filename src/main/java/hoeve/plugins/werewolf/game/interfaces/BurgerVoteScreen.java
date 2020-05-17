package hoeve.plugins.werewolf.game.interfaces;

import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.helpers.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DeStilleGast 13-4-2020
 */
public class BurgerVoteScreen extends BaseScreen {

    private boolean isFinishedSelecting;

    private Map<Player, OfflinePlayer> voteMap;

    public BurgerVoteScreen(WerewolfGame game) {
        super(game, "Who do we throw on the fire ?", (int)Math.ceil(game.getPlayerList().size() / 9D));
        resetVote();
        prepareInventory();

        Bukkit.getPluginManager().registerEvents(this, game.getPlugin());
    }

    public void resetVote(){
        voteMap = new HashMap<>();
        this.isFinishedSelecting = false;
    }

    @Override
    protected void onInventoryClick(Player player, ItemStack itemStack, InventoryClickEvent e) {
        if(itemStack.getType() == Material.PLAYER_HEAD){
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            OfflinePlayer votedPlayer = skullMeta.getOwningPlayer();

            voteMap.put(player, votedPlayer);

            prepareInventory();
        }
    }

    @Override
    protected void prepareInventory() {
        myInv.clear();

        game.getPlayerList().forEach(p -> {
            addItem(createVoteHead(p.getPlayer()));
        });
    }

    private ItemStack createVoteHead(OfflinePlayer p){
        ItemStack head = createHead((Player) p);
        int voteCount = 0;
        for (Player key : voteMap.keySet()){
            OfflinePlayer voted = voteMap.get(key);
            if(p.getPlayer() == voted){
                String topLine = "Who has voted:";
                if(!ItemHelper.containsLore(head, topLine)){
                    ItemHelper.addLore(head, topLine);
                }
                ItemHelper.addLore(head, key.getName());
                voteCount++;
            }
        }
        head.setAmount(Math.max(1, voteCount)); // 0 will remove the item from screen
        return head;
    }
}
