package hoeve.plugins.werewolf.game.helpers;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.testng.collections.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by DeStilleGast 12-4-2020
 */
public class ItemHelper {

    public static ItemStack rename(ItemStack stack, String name){
        if(stack.getItemMeta() != null) {
            ItemMeta im = stack.getItemMeta();
            im.setDisplayName(ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', name));
            stack.setItemMeta(im);
        }

        return stack;
    }

    public static ItemStack setLore(ItemStack stack, String...lore){
        if(stack.getItemMeta() != null){
            ItemMeta im = stack.getItemMeta();
            im.setLore(Arrays.asList(lore));
            stack.setItemMeta(im);
        }

        return stack;
    }

    public static ItemStack addLore(ItemStack stack, String...lore){
        if(stack.getItemMeta() != null){
            ItemMeta im = stack.getItemMeta();
            List<String> currentLore = im.getLore();
            if(currentLore == null) currentLore = new ArrayList<>();
            currentLore.addAll(Arrays.asList(lore));

            im.setLore(currentLore);
            stack.setItemMeta(im);
        }

        return stack;
    }

    public static boolean containsLore(ItemStack stack, String lorePart) {
        if (stack.getItemMeta() != null) {
            ItemMeta im = stack.getItemMeta();
            List<String> currentLore = im.getLore();
            if(currentLore == null) return false;

            return currentLore.contains(lorePart);
        }

        return false;
    }
}
