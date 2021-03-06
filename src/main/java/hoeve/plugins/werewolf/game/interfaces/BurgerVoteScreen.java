package hoeve.plugins.werewolf.game.interfaces;

import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.helpers.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

    private Map<Player, String> voteMap;

    public BurgerVoteScreen(WerewolfGame game) {
        this(game, "Who do we throw on the fire ?");
//        Bukkit.getPluginManager().registerEvents(this, game.getPlugin());
    }

    public BurgerVoteScreen(WerewolfGame game, String title) {
        super(game, title, (int)Math.ceil(game.getPlayerList(false).size() / 9D));

        resetVote();
        prepareInventory();
    }

    public void resetVote(){
        voteMap = new HashMap<>();
    }

    @Override
    protected void onInventoryClick(Player player, ItemStack itemStack, InventoryClickEvent e) {
        if(game.getPlayer(player).isAlive()) {
            if (itemStack.getType() == Material.PLAYER_HEAD) {
                voteMap.put(player, itemStack.getItemMeta().getDisplayName());

                prepareInventory();
                waitTillAllReady.markReady(game.getPlayer(player));
            }
        }
    }

    @Override
    protected void prepareInventory() {
        myInv.clear();

        game.getPlayerList(false).forEach(p -> {
            addItem(createVoteHead(p.getPlayer()));
        });
    }

    private ItemStack createVoteHead(Player p){
        if(game.getPlayer(p).isAlive()) {
            ItemStack head = createHead(p);

            int voteCount = 0;
            for (Player key : voteMap.keySet()) {
                String voted = voteMap.get(key);
                if (ChatColor.stripColor(p.getDisplayName()).equals(ChatColor.stripColor(voted))) {
                    String topLine = "Who has voted:";
                    if (!ItemHelper.containsLore(head, topLine)) {
                        ItemHelper.addLore(head, topLine);
                    }
                    ItemHelper.addLore(head, key.getName());
                    voteCount++;
                }
            }
            head.setAmount(Math.max(1, voteCount)); // 0 will remove the item from screen
            return head;
        }else{
            ItemStack deadHead = new ItemStack(Material.SKELETON_SKULL);
            ItemHelper.rename(deadHead, ChatColor.stripColor(p.getDisplayName()) + " skull");

            return deadHead;
        }
    }

    public Map<Player, String> getVoteMap(){
        return this.voteMap;
    }
}
