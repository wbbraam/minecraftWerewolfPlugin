package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;

/**
 * Created by DeStilleGast 7-4-2020
 */
public class WitchRole implements IRole {
    @Override
    public String getRoleName() {
        return "Witch";
    }

    @Override
    public void onGameStart(WerewolfPlayer player, WaitTillAllReady game) {
        // basicly the same as Common
    }

    @Override
    public void onGameStateChange(WerewolfPlayer player, GameStatus status) {
        // if it is getting day, ask if player wants to rescue that player
        // next ask if player wants to kill someone
    }

    @Override
    public void onDead(WerewolfPlayer killedBy) {
        // drop potions ?
    }
}
