package hoeve.plugins.werewolf.game.roles;

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
    public void onGameStart(WerewolfPlayer player, WaitTillAllReady waiter);
    public void onGameStateChange(WerewolfPlayer player, GameStatus status);
    public void onDead(WerewolfPlayer killedBy);
}
