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
public class WitchRole implements IRole {
    @Override
    public String getRoleName() {
        return ChatColor.DARK_PURPLE + "Witch";
    }

    @Override
    public WaitTillAllReady firstNight(WerewolfGame game, WerewolfPlayer player, WaitTillAllReady waiter) {
        return null;
    }

    @Override
    public void onGameStateChange(WerewolfGame game, WerewolfPlayer player, GameStatus status) {
        // if it is getting day, ask if player wants to rescue that player
        // next ask if player wants to kill someone

        switch (status) {
            case STARTUP:
                game.notifyPlayer(player, "You are a " + getRoleName());

        }
    }

    @Override
    public void onDead(WerewolfGame game, WerewolfPlayer meDied, WerewolfPlayer killedBy, EnumDeadType deadType) {
        // drop potions ?
    }
}
