package hoeve.plugins.werewolf.game.actions;

import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.roles.CupidoRole;
import hoeve.plugins.werewolf.game.roles.WerewolfRole;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

/**
 * Created by DeStilleGast 14-4-2020
 */
public class ParticleManager implements Runnable {

    private WerewolfGame game;
    private Random random = new Random();
    private double magicNumber = 180;

    private BukkitTask task;


    public ParticleManager(WerewolfGame game) {
        this.game = game;
    }

    public void start() {
        this.task = Bukkit.getScheduler().runTaskTimer(game.getPlugin(), this, 5, 5);
    }

    @Override
    public void run() {
        for (WerewolfPlayer player : game.getPlayerList()) {
            if (!player.isAlive()) continue;
            for (WerewolfPlayer particleOnThisPlayer : game.getPlayerList()) {

                if (player.getLover() != null && player.getLover() == particleOnThisPlayer) {
                    spawnParticle(player, Particle.HEART, player.getLover().getPlayer().getLocation());
                }

                if(player.getRole() instanceof CupidoRole && particleOnThisPlayer.getLover() != null){
                    if(particleOnThisPlayer.isAlive())
                        spawnParticle(player, Particle.HEART, particleOnThisPlayer.getPlayer().getLocation());
                }

                if (player.getRole() instanceof WerewolfRole && particleOnThisPlayer.getRole() instanceof WerewolfRole) {
                    spawnParticle(player, Particle.FLAME, particleOnThisPlayer.getPlayer().getLocation());
                }
            }
        }

        for (WerewolfPlayer player : game.getPlayerList()) {
            if(player.isAlive()) {
                if(player.getRole() instanceof WerewolfRole){
                    spawnParticle(game.getGameMaster(), Particle.FLAME, player.getPlayer().getLocation());
                }

                if (player.getLover() != null) {
                    spawnParticle(game.getGameMaster(), Particle.HEART, player.getPlayer().getLocation());
                }
            }
        }
    }

    private void spawnParticle(WerewolfPlayer player, Particle particle, Location location) {
        if(!player.isAlive()) return;
//        Location loc = player.getLocation();
        Location tmp = new Location(location.getWorld(), Math.sin(magicNumber) * 0.55, 0.1, Math.cos(magicNumber) * 0.55);

        magicNumber = random.nextDouble() * (Math.PI * 4);

        Location particleLocation = location.add(tmp);
        player.getPlayer().spawnParticle(particle, particleLocation, 0, 0, 0.1, 0);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(this.task.getTaskId());
    }
}
