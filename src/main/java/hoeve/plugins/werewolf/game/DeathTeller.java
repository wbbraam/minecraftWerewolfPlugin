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
        if(deaths.stream().map(Tuple::getFirst).noneMatch(deadPlayer -> deadPlayer.equals(player))) {
            deaths.add(new Tuple<>(player, deadType));
        }
    }

    public void healPlayer(Player player, EnumDeadType healType) {
        healing.add(new Tuple<>(player, healType));
    }

    private Optional<Tuple<Player, EnumDeadType>> isHealed(Player player){
        return healing.stream().filter(t -> t.getFirst() == player).findFirst();
    }

    public void tellStory(final WerewolfGame game, Runnable callback){
        List<Runnable> actions = new ArrayList<>();
        boolean isLovedDead = false;

        for(Tuple<Player, EnumDeadType> playerDeath : deaths){
            WerewolfPlayer deadCharacter = game.getPlayer(playerDeath.getFirst());

            if(deadCharacter.isAlive()){
                String deathName = playerDeath.getFirst().getDisplayName();
                Optional<Tuple<Player, EnumDeadType>> healEvent = isHealed(playerDeath.getFirst());

                // Check if player was healed
                if(healEvent.isPresent()){
                    // yes, player was healed, nothing much will happen
                    actions.add(() -> new BossBarTimer(game.getPlugin(), "Player " + deathName + " was found heavly injured from a " + playerDeath.getSecond().getFriendlyName() + " attack", 10, null, game.getPlayerList()));
                    actions.add(() -> new BossBarTimer(game.getPlugin(), "but was healed by a " + healEvent.get().getSecond().getFriendlyName(), 10, null, game.getPlayerList()));
                }else{
                    // No, player died
                    EnumDeadType deadType = playerDeath.getSecond();
                    String customDeadMessage = deadCharacter.getDeathMessage(game, deadType);
                    switch (deadType) {
                        case VOTE:
                            // Died because of vote
                            actions.add(() -> new BossBarTimer(game.getPlugin(), "Player " + deathName + " was throwed on the fire...", 10, deadCharacter::kill, game.getPlayerList()));
                            break;
                        case LEFT:
                            // Died because (s)he left
                            actions.add(() -> new BossBarTimer(game.getPlugin(), "Player " + deathName + " went missing", 10, deadCharacter::kill, game.getPlayerList()));
                            actions.add(() -> new BossBarTimer(game.getPlugin(), "The town decided that he/she was gone for good", 10, null, game.getPlayerList()));

                            break;
                        case GAMEMASTER:
                            // Died because the GameMaster said so
                            actions.add(() -> new BossBarTimer(game.getPlugin(), "Player " + deathName + " went missing", 10, deadCharacter::kill, game.getPlayerList()));
                            actions.add(() -> new BossBarTimer(game.getPlugin(), "But the town quickly found out that the gods punished him/her", 10, null, game.getPlayerList()));

                            break;
                        default:
                            // Died by werewolf, witch, hunter
                            actions.add(() -> new BossBarTimer(game.getPlugin(), "Player " + deathName + " was found dead..", 10, deadCharacter::kill, game.getPlayerList()));
                            actions.add(() -> new BossBarTimer(game.getPlugin(), deathName + " " + customDeadMessage.trim(), 10, null, game.getPlayerList()));
                            break;
                    }

                    // Tell role of player
                    actions.add(() -> new BossBarTimer(game.getPlugin(), deathName + " was a " + deadCharacter.getRole().getRoleName(), 10, null, game.getPlayerList()));

                    // Check if there is a lover
                    if(!isLovedDead && deadCharacter.getLover() != null && deadCharacter.getLover().isAlive()){
                        // Yes we found a lover
                        isLovedDead = true;
                        WerewolfPlayer lovedOne = deadCharacter.getLover();

                        // tell others that a lover died
                        actions.add(() -> new BossBarTimer(game.getPlugin(), "But quickly after that " + deathName + " was announced dead", 10, lovedOne::kill, game.getPlayerList()));
                        actions.add(() -> new BossBarTimer(game.getPlugin(), lovedOne.getPlayer().getDisplayName() + " " + lovedOne.getDeathMessage(game, EnumDeadType.LOVE), 10, null, game.getPlayerList()));

                        // Also tell them his/her role
                        actions.add(() -> new BossBarTimer(game.getPlugin(), lovedOne.getPlayer().getDisplayName() + " was a " + lovedOne.getRole().getRoleName(), 10, null, game.getPlayerList()));
                    }
                }
            }
        }

        actions.add(callback);
        for (int i = 0; i < actions.size(); i++) {
            Bukkit.getScheduler().runTaskLater(game.getPlugin(), actions.get(i), 20 * 5 * i);
        }

        // Clear story
        this.newStory();
    }
}