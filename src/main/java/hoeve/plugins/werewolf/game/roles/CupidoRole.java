package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;

/**
 * Created by DeStilleGast 7-4-2020
 */
public class CupidoRole implements IRole {
    @Override
    public String getRoleName() {
        return "Cupido";
    }

    @Override
    public void onGameStart(WerewolfPlayer player, WaitTillAllReady game) {
        // show screen for match 2 players
    }

    @Override
    public void onGameStateChange(WerewolfPlayer player, GameStatus status) {

    }

    @Override
    public void onDead(WerewolfPlayer killedBy) {
        // maybe funny message about a couple if they are still alive, if dead sad message about how he lost
    }
}
