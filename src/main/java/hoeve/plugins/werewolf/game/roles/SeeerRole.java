package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;

/**
 * Created by DeStilleGast 7-4-2020
 */
public class SeeerRole implements IRole {
    @Override
    public String getRoleName() {
        return "Seer";
    }

    @Override
    public void onGameStart(WerewolfPlayer player, WaitTillAllReady game) {
        // explain role
    }

    @Override
    public void onGameStateChange(WerewolfPlayer player, GameStatus status) {
        // if night, ask player to see a role of a player (DO NOT PUT IN CHAT)
    }

    @Override
    public void onDead(WerewolfPlayer killedBy) {
        // all the knowledge is lost
    }
}
