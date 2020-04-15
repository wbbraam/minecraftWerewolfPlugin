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
public class HunterRole extends IRole {
    @Override
    public String getRoleName() {
        return ChatColor.GOLD + "Hunter";
    }

    @Override
    public WaitTillAllReady firstNight(WerewolfGame game, WerewolfPlayer player, WaitTillAllReady waiter) {
        return null;
    }

    @Override
    public void onGameStateChange(WerewolfGame game, WerewolfPlayer player, GameStatus status) {
        switch (status) {
            case STARTUP:
                game.notifyPlayer(player, "You are a " + getRoleName());

        }
    }

    @Override
    public String onDead(WerewolfGame game, WerewolfPlayer meDied, EnumDeadType deadType) {
        if(deadType == EnumDeadType.VOTE || deadType == EnumDeadType.WOLVES){
            // let him kill someone (bow and arrow)
        }

        return "to be made";
    }
}
