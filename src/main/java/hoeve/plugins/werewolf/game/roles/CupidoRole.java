package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.EnumDeadType;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;
import hoeve.plugins.werewolf.game.interfaces.CupidoScreen;
import org.bukkit.ChatColor;

/**
 * Created by DeStilleGast 7-4-2020
 */
public class CupidoRole extends BaseRole {

    @Override
    public String getRoleName() {
        return ChatColor.LIGHT_PURPLE + "Cupido";
    }

    @Override
    public WaitTillAllReady firstNight(WerewolfGame game, WerewolfPlayer player, WaitTillAllReady waiter) {
        CupidoScreen cupidoScreen = new CupidoScreen(game);
        WaitTillAllReady customWaiter = game.getPlugin().setupWaiter(1, 30, "Waiting for to shoot his arrows [%time%]", () -> {
           cupidoScreen.selectRandom(player.getPlayer());
           waiter.markReady(player);
        });
        cupidoScreen.prepareInternalInventory(waiter);

        return customWaiter;
    }


    @Override
    public String onDead(WerewolfGame game, WerewolfPlayer meDied, EnumDeadType deadType) {
        // maybe funny message about a couple if they are still alive, if dead sad message about how he lost
        boolean lovedOneStillAlive = game.getPlayerList(false).stream().filter(WerewolfPlayer::isAlive).allMatch(wp -> wp.getLover() != null);
        String originalMessage = super.onDead(game, meDied, deadType);


        if(deadType == EnumDeadType.VOTE){
            if(lovedOneStillAlive) {
//                return "With a good feeling about his choice, (s)he left the campfire";
                return "heeft met een goed gevoel over zijn keuze heeft hij/zij het kampvuur verlaten";
            }else{
//                return "With a bad feeling, (s)he felt guilty and has vanished quickly";
                return "heeft met een naar gevoel snel het dorp verlaten";
            }
        }else if(deadType == EnumDeadType.LEFT){
//            return "(s)he must have been feeling so bad because:";
            return "had blijkbaar een heel slecht gevoel, want:";
        }

        return originalMessage;
    }
}
