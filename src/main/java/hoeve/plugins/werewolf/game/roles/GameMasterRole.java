package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;

/**
 * Created by DeStilleGast 7-4-2020
 */
public class GameMasterRole implements IRole {

    @Override
    public String getRoleName() {
        return "GameMaster";
    }

    @Override
    public void onGameStart(WerewolfGame game) {

    }

    @Override
    public void onGameStateChange(GameStatus status) {

    }

    @Override
    public void onDead(WerewolfPlayer killedBy) {

    }
}
