package hoeve.plugins.werewolf.game.actions;

import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.interfaces.OracleScreen;
import hoeve.plugins.werewolf.game.roles.CupidoRole;
import hoeve.plugins.werewolf.game.roles.OracleRole;
import hoeve.plugins.werewolf.game.roles.WerewolfRole;
import hoeve.plugins.werewolf.game.roles.WitchRole;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
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
        for (WerewolfPlayer player : game.getPlayerList(false)) {

            // Spawn particle on gamemaster if the player can see the game master
            if (player.getPlayer().canSee(game.getGameMaster().getPlayer()))
                spawnParticle(player, Particle.END_ROD, game.getGameMaster().getPlayer().getLocation());

            if (!player.isAlive()) continue;
            for (WerewolfPlayer particleOnThisPlayer : game.getPlayerList(false)) {
                // skip particle on this player because (s)he can see him/her
                if (!player.getPlayer().canSee(particleOnThisPlayer.getPlayer())) continue;

                // loved one
                if (player.getLover() != null && player.getLover() == particleOnThisPlayer) {
                    spawnParticle(player, Particle.HEART, player.getLover().getPlayer().getLocation());
                }

                // Cupido
                if (player.getRole() instanceof CupidoRole && particleOnThisPlayer.getLover() != null) {
                    if (particleOnThisPlayer.isAlive())
                        spawnParticle(player, Particle.HEART, particleOnThisPlayer.getPlayer().getLocation());
                }

                // Werewolves
                if (player.getRole() instanceof WerewolfRole && particleOnThisPlayer.getRole() instanceof WerewolfRole) {
                    spawnParticle(player, Particle.FLAME, particleOnThisPlayer.getPlayer().getLocation());
                }
            }

            // Particle for gamemaster
            // Werewolf
            if (player.getRole() instanceof WerewolfRole) {
                spawnParticle(game.getGameMaster(), Particle.FLAME, player.getPlayer().getLocation());
            }

            // Witch
            if(player.getRole() instanceof WitchRole){
                spawnParticle(player, Particle.SPELL_WITCH, player.getPlayer().getLocation());
                spawnParticle(game.getGameMaster(), Particle.SPELL_WITCH, player.getPlayer().getLocation());
            }

            // Oracle
            if(player.getRole() instanceof OracleRole){
                spawnParticle(player, Particle.CLOUD, player.getPlayer().getLocation());
            }

            // Loved one
            if (player.getLover() != null) {
                spawnParticle(game.getGameMaster(), Particle.HEART, player.getPlayer().getLocation());
            }
        }
    }

    private void spawnParticle(WerewolfPlayer player, Particle particle, Location location) {
        if (!player.isAlive()) return;
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
