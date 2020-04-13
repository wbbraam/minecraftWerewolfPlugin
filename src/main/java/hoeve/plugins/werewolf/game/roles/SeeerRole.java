package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.EnumDeadType;
import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfGame;
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
    public WaitTillAllReady firstNight(WerewolfGame game, WerewolfPlayer player, WaitTillAllReady waiter) {
        return null;
    }

    @Override
    public void onGameStateChange(WerewolfGame game, WerewolfPlayer player, GameStatus status) {
        // if night, ask player to see a role of a player (DO NOT PUT IN CHAT)
    }

    @Override
    public void onDead(WerewolfGame game, WerewolfPlayer meDied, WerewolfPlayer killedBy, EnumDeadType deadType) {
        // all the knowledge is lost

    }
}
