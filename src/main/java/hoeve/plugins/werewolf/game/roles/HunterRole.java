package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;

/**
 * Created by DeStilleGast 7-4-2020
 */
public class HunterRole implements IRole {
    @Override
    public String getRoleName() {
        return "Hunter";
    }

    @Override
    public void onGameStart(WerewolfPlayer player, WaitTillAllReady game) {

    }

    @Override
    public void onGameStateChange(WerewolfPlayer player, GameStatus status) {

    }

    @Override
    public void onDead(WerewolfPlayer killedBy) {
        // take killer with him
    }
}
