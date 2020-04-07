package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;

/**
 * Created by DeStilleGast 7-4-2020
 */
public class WereWolfRole implements IRole {

    @Override
    public String getRoleName() {
        return "Werewolf";
    }

    @Override
    public void onGameStart(WerewolfPlayer player, WaitTillAllReady game) {
        // tell wolfs to kill all the villagers
    }

    @Override
    public void onGameStateChange(WerewolfPlayer player, GameStatus status) {
        // check if it is night, setup vote
    }

    @Override
    public void onDead(WerewolfPlayer killedBy) {
        // Tell other wolves that one of there friends died
    }
}
