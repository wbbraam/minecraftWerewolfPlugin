package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.EnumDeadType;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;
import org.bukkit.ChatColor;

/**
 * Created by DeStilleGast 7-4-2020
 */
public abstract class BaseRole {

    public abstract String getRoleName();

    public WaitTillAllReady firstNight(WerewolfGame game, WerewolfPlayer player, WaitTillAllReady waiter) {
        return null;
    }


    public String onDead(WerewolfGame game, WerewolfPlayer meDied, EnumDeadType deadType) {
        switch (deadType) {
            case LOVE:
//                game.notifyPlayer(meDied, "You died out of a broken " + ChatColor.LIGHT_PURPLE + "heart");
                game.notifyPlayer(meDied, "Je bent overleden aan een gebroken " + ChatColor.LIGHT_PURPLE + "hart");
//                return " died of a broken " + ChatColor.LIGHT_PURPLE + "heart";
                return " is overleden aan een gebroken " + ChatColor.LIGHT_PURPLE + "hart";

            case WITCH:
//                game.notifyPlayer(meDied, "You died because someone has " + ChatColor.DARK_GREEN + "poisoned" + ChatColor.RESET + " you");
                game.notifyPlayer(meDied, "Je bent overleden omdat iemand je " + ChatColor.DARK_GREEN + "vergiftigd" + ChatColor.RESET + " heeft");
//                return "has died of some " + ChatColor.DARK_GREEN + "poison";
                return "is overleden aan " + ChatColor.DARK_GREEN + "vergif";
            case HUNTER:
//                game.notifyPlayer(meDied, "You died because the hunter pointed his gun to you");
                game.notifyPlayer(meDied, "Je bent overleden door de jager");
//                return "has died because the " + ChatColor.GOLD + "hunter" + ChatColor.RESET + " gun was bewitched and took his/her soul";
                return "is overleden omdat het wapen van de " + ChatColor.GOLD + "jager" + ChatColor.RESET + " betoverd was en zijn/haar neergeschoten heeft";
            case WOLVES:
//                game.notifyPlayer(meDied, "Seems like you were chosen to be eaten.");
                game.notifyPlayer(meDied, "Het ziet ernaar uit dat je opgegeten bent");
//                return "has been killed by the " + ChatColor.DARK_RED + "wolves";
                return "is opgegeten door de " + ChatColor.DARK_RED + "weerwolven";
            default:
//                game.notifyPlayer(meDied, "You died of some unknown reason");
                return null;
        }
    }
}
