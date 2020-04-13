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
public class WereWolfRole implements IRole {

    @Override
    public String getRoleName() {
        return ChatColor.DARK_RED + "Werewolf";
    }

    @Override
    public WaitTillAllReady firstNight(WerewolfGame game, WerewolfPlayer player, WaitTillAllReady waiter) {
        return null;
    }

    @Override
    public void onGameStateChange(WerewolfGame game, WerewolfPlayer player, GameStatus status) {
        // check if it is night, setup vote

        switch (status) {
            case STARTUP:
                game.notifyPlayer(player, "You are a " + getRoleName());

        }
    }


    @Override
    public void onDead(WerewolfGame game, WerewolfPlayer meDied, WerewolfPlayer killedBy, EnumDeadType deadType) {
        // Tell other wolves that one of there friends died
        if (deadType == EnumDeadType.LOVE) {
            game.notifyRole(WereWolfRole.class, "One of your family has died out of love");
        } else {
            game.notifyRole(WereWolfRole.class, "One of your family has been killed");
        }
    }
}
