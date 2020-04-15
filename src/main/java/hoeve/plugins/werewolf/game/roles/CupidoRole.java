package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.EnumDeadType;
import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;
import hoeve.plugins.werewolf.game.interfaces.CupidoScreen;
import org.bukkit.ChatColor;

/**
 * Created by DeStilleGast 7-4-2020
 */
public class CupidoRole implements IRole {

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
    public void onGameStateChange(WerewolfGame game, WerewolfPlayer player, GameStatus status) {
        switch (status) {
            case STARTUP:
                game.notifyPlayer(player, "You are " + getRoleName());

        }
    }

    @Override
    public String onDead(WerewolfGame game, WerewolfPlayer meDied, EnumDeadType deadType) {
        // maybe funny message about a couple if they are still alive, if dead sad message about how he lost
        return "To be made";
    }
}
