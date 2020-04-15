package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.EnumDeadType;
import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;
import org.bukkit.ChatColor;

import java.util.concurrent.Callable;

/**
 * Created by DeStilleGast 7-4-2020
 */
public abstract class IRole {

    public abstract String getRoleName();

    public WaitTillAllReady firstNight(WerewolfGame game, WerewolfPlayer player, WaitTillAllReady waiter) {
        return null;
    }

    public void onGameStateChange(WerewolfGame game, WerewolfPlayer player, GameStatus status) {
    }

    public String onDead(WerewolfGame game, WerewolfPlayer meDied, EnumDeadType deadType) {
        switch (deadType) {
            case LOVE:
                game.notifyPlayer(meDied, "You died out of a broken " + ChatColor.LIGHT_PURPLE + "heart");
                return " died of a broken " + ChatColor.LIGHT_PURPLE + "heart";

            case WITCH:
                game.notifyPlayer(meDied, "You died because someone has " + ChatColor.DARK_GREEN + "poisoned" + ChatColor.RESET + " you");
                return "has died of some " + ChatColor.DARK_GREEN + "poison";
            case HUNTER:
                game.notifyPlayer(meDied, "You died because the hunter pointed his gun to you");
                return "has died because he/she met a angry " + ChatColor.GOLD + "hunter";
            case WOLVES:
                game.notifyPlayer(meDied, "Seems like you were chosen to be eaten.");
                return "has been killed by the " + ChatColor.DARK_RED + "wolves";
            default:
                game.notifyPlayer(meDied, "You died of some unknown reason");
                return "died for a unknown reason";
        }
    }
}
