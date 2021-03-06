package hoeve.plugins.werewolf.game.helpers;

import hoeve.plugins.werewolf.WerewolfPlugin;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DeStilleGast 7-4-2020
 */
public class WaitTillAllReady {

    private BossBarTimer bossBarTimer;

    private int waitingForAmount;
    private Runnable whenAllIsSet;
    private List<WerewolfPlayer> markedPlayers;

    private boolean hasRun = false;

    public WaitTillAllReady(BossBarTimer bossBarTimer, int waitingForAmount, int waitTime, Runnable whenAllIsSet) {
        this.bossBarTimer = bossBarTimer;
        this.waitingForAmount = waitingForAmount;
        this.whenAllIsSet = whenAllIsSet;

        this.markedPlayers = new ArrayList<>();


    }

    /**
     * Mark a player ready
     * @param player player to mark ready
     */
    public void markReady(WerewolfPlayer player) {
        if (!markedPlayers.contains(player)) markedPlayers.add(player);

        if (markedPlayers.size() == waitingForAmount) {
            allSet();
        }
    }

    /**
     * Ready or not, here I go, run command
     */
    public void allSet() {
        if (!hasRun) {
            bossBarTimer.cleanUp();
            if(whenAllIsSet != null)
                whenAllIsSet.run();
        }
        hasRun = true;
    }

    public boolean hasRun(){
        return hasRun;
    }
}
