package hoeve.plugins.werewolf.game.helpers;

import hoeve.plugins.werewolf.WerewolfPlugin;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 * Created by DeStilleGast 12-4-2020
 */
public class BossBarTimer implements Runnable {

    private BossBar bossBar;

    private String message;
    private long startTime, duration;
    private Runnable finishAction;

    public BossBarTimer(WerewolfPlugin plugin, String message, int seconds, Runnable finishAction, List<WerewolfPlayer> players) {
        this.message = message;
        this.finishAction = finishAction;

        this.startTime = System.currentTimeMillis();
        this.duration = seconds * 1000;

        bossBar = Bukkit.createBossBar(message.replace("%time%", formatTime(duration * 1000)), BarColor.values()[new Random().nextInt(BarColor.values().length-1)], BarStyle.SOLID, BarFlag.DARKEN_SKY);
        bossBar.setProgress(1);

        for (WerewolfPlayer pl : players) bossBar.addPlayer(pl.getPlayer());

        Bukkit.getScheduler().runTaskTimer(plugin, this, 10, 1);
    }

    @Override
    public void run() {
        if(bossBar == null) return;
        try{
            double perc = ((startTime + duration * 1D) - System.currentTimeMillis()) / duration;
            if(perc >= 0){
                bossBar.setTitle(message.replace("%time%", formatTime((startTime + duration) - System.currentTimeMillis())));
                bossBar.setProgress(perc);
            }else{
                cleanUp();

                if(finishAction != null)
                    finishAction.run();
            }

        }catch (Exception ex){
            ex.printStackTrace();
            cleanUp();
        }
    }


    public void cleanUp(){
        bossBar.removeAll();
        bossBar = null;
    }

    private String formatTime(long totalMilliSecs){
        long totalSecs = totalMilliSecs / 1000;
//        int hours = totalSecs / 3600;
        long minutes = (totalSecs % 3600) / 60;
        long seconds = totalSecs % 60;


        return String.format("%02d:%02d", minutes, seconds);
    }
}
