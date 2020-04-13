package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.EnumDeadType;
import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;
import org.bukkit.ChatColor;

/**
 * Created by DeStilleGast 7-4-2020
 */
public class CommonRole implements IRole {


    @Override
    public String getRoleName() {
        return "Villager";
    }

    @Override
    public WaitTillAllReady firstNight(WerewolfGame game, WerewolfPlayer player, WaitTillAllReady waiter) {
        return null;
    }

//    @Override
//    public void onGameStart(WerewolfPlayer player, WaitTillAllReady waiter) {
//        waiter.markReady(player);
//        // tell player that they need to find out who the wolves are
//    }

    @Override
    public void onGameStateChange(WerewolfGame game, WerewolfPlayer player, GameStatus status) {
        // check if state is VOTE, display vote screen
    }

    @Override
    public void onDead(WerewolfGame game, WerewolfPlayer meDied, WerewolfPlayer killedBy, EnumDeadType deadType) {
        switch (deadType) {
            case LOVE:
                game.notifyPlayer(meDied, "You died out of a broken " + ChatColor.LIGHT_PURPLE + "heart");
                break;
            case WITCH:
                game.notifyPlayer(meDied, "You died because someone has " + ChatColor.DARK_GREEN + "poisoned" + ChatColor.RESET + " you");
                break;
            case HUNTER:
                game.notifyPlayer(meDied, "You died because the hunter pointed his gun to you");
                break;
            case WOLVES:
                game.notifyPlayer(meDied, "Seems like you were chosen to be eaten.");
            default:
                game.notifyPlayer(meDied, "You died of some unknown reason");
                break;
        }
    }
}
