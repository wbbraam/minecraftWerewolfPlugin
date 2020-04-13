package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.EnumDeadType;
import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;

import java.util.concurrent.Callable;

/**
 * Created by DeStilleGast 7-4-2020
 */
public interface IRole {

    public String getRoleName();
//    public void onGameStart(WerewolfPlayer player, WaitTillAllReady waiter);
    public WaitTillAllReady firstNight(WerewolfGame game, WerewolfPlayer player, WaitTillAllReady waiter);
    public void onGameStateChange(WerewolfGame game, WerewolfPlayer player, GameStatus status);
    public void onDead(WerewolfGame game, WerewolfPlayer meDied, WerewolfPlayer killedBy, EnumDeadType deadType);
}
