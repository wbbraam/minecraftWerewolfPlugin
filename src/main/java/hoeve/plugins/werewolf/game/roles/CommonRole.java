package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;

/**
 * Created by DeStilleGast 7-4-2020
 */
public class CommonRole implements IRole {


    @Override
    public String getRoleName() {
        return "Villager";
    }

    @Override
    public void onGameStart(WerewolfGame game) {
        // tell player that they need to find out who the wolves are
    }

    @Override
    public void onGameStateChange(GameStatus status) {
        // check if state is VOTE, display vote screen
    }

    @Override
    public void onDead(WerewolfPlayer killedBy) {
        // Tell how player died
    }
}
