package hoeve.plugins.werewolf.game;

import hoeve.plugins.werewolf.game.helpers.BossBarTimer;
import hoeve.plugins.werewolf.game.helpers.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by DeStilleGast 14-4-2020
 */
public class DeathTeller {
    private List<Tuple<Player, EnumDeadType>> deaths;
    private List<Tuple<Player, EnumDeadType>> healing;

    public DeathTeller() {
        this.deaths = new ArrayList<>();
        this.healing = new ArrayList<>();
    }

    private void newStory(){
        deaths.clear();
        healing.clear();
    }

    public void addDeath(Player player, EnumDeadType deadType){
        deaths.add(new Tuple<>(player, deadType));
    }

    public void healPlayer(Player player, EnumDeadType healType) {
        healing.add(new Tuple<>(player, healType));
    }

    private Optional<Tuple<Player, EnumDeadType>> isHealed(Player player){
        return healing.stream().filter(t -> t.getFirst() == player).findFirst();
    }

    public void tellStory(final WerewolfGame game){
        List<Runnable> actions = new ArrayList<>();
        for(Tuple<Player, EnumDeadType> playerDeath : deaths){
            WerewolfPlayer deadCharacter = game.getPlayer(playerDeath.getFirst());

            if(deadCharacter.isAlive()){
                String deathName = playerDeath.getFirst().getDisplayName();
                Optional<Tuple<Player, EnumDeadType>> healEvent = isHealed(playerDeath.getFirst());

                if(healEvent.isPresent()){
                    actions.add(() -> new BossBarTimer(game.getPlugin(), "Player " + deathName + " was found heavly injured from a " + playerDeath.getSecond() + " attack", 10, null, game.getPlayerList()));
                    actions.add(() -> new BossBarTimer(game.getPlugin(), "but was healed by a " + healEvent.get().getSecond(), 10, null, game.getPlayerList()));
                }else{
                    EnumDeadType deadType = playerDeath.getSecond();
                    if(deadType == EnumDeadType.VOTE) {
                        actions.add(() -> new BossBarTimer(game.getPlugin(), "Player " + deathName + " was throwed on the fire...", 10, null, game.getPlayerList()));
                        deadCharacter.onDead(game, EnumDeadType.VOTE);
                    }else{
                        actions.add(() -> new BossBarTimer(game.getPlugin(), "Player " + deathName + " was found dead..", 10, null, game.getPlayerList()));
                        actions.add(() -> new BossBarTimer(game.getPlugin(), deathName + " " + deadCharacter.onDead(game, deadType).trim(), 10, null, game.getPlayerList()));
                    }

                    actions.add(() -> new BossBarTimer(game.getPlugin(), deathName + " was a " + deadCharacter.getRole().getRoleName(), 10, null, game.getPlayerList()));

                    if(deadCharacter.getLover() != null && deadCharacter.getLover().isAlive()){
                        WerewolfPlayer lovedOne = deadCharacter.getLover();

                        actions.add(() -> new BossBarTimer(game.getPlugin(), "But quickly after that " + deathName + " was announced dead", 10, null, game.getPlayerList()));
                        actions.add(() -> new BossBarTimer(game.getPlugin(), lovedOne.getPlayer().getDisplayName() + " " + lovedOne.onDead(game, EnumDeadType.LOVE), 10, null, game.getPlayerList()));

                        actions.add(() -> new BossBarTimer(game.getPlugin(), lovedOne.getPlayer().getDisplayName() + " was a " + lovedOne.getRole().getRoleName(), 10, null, game.getPlayerList()));
                    }
                }
            }
        }



//        List<Runnable> actions = new ArrayList<>();
//        for (Player deadPlayer : deaths.keySet()) {
//            WerewolfPlayer deadGameCharacter = getPlayer(deadPlayer);
//
//            if (deadGameCharacter.isAlive()) {
//                actions.add(() -> new BossBarTimer(plugin, "Player " + deadPlayer.getName() + " was found dead..", 10, null, playerList));
//                actions.add(() -> new BossBarTimer(plugin, deadPlayer.getName() + " " + deadGameCharacter.onDead(this, deaths.get(deadPlayer)).trim(), 10, null, playerList));
//                if (deadGameCharacter.getLover() != null && deadGameCharacter.getLover().isAlive()) {
//                    actions.add(() -> new BossBarTimer(plugin, "But quickly after that " + deadPlayer.getName() + " was announced dead", 10, null, playerList));
//                    actions.add(() -> new BossBarTimer(plugin, deadGameCharacter.getLover().getPlayer().getName() + " " + deadGameCharacter.getLover().onDead(this, EnumDeadType.LOVE), 10, null, playerList));
//                }
//            }
//        }

        for (int i = 0; i < actions.size(); i++) {
            Bukkit.getScheduler().runTaskLater(game.getPlugin(), actions.get(i), 20 * 5 * i);
        }

        newStory();
    }
}