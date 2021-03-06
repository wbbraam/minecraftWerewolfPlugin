package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.EnumDeadType;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import org.bukkit.ChatColor;

/**
 * Created by DeStilleGast 7-4-2020
 */
public class WerewolfRole extends BaseRole {

    @Override
    public String getRoleName() {
        return ChatColor.DARK_RED + "Werewolf";
    }

    @Override
    public String onDead(WerewolfGame game, WerewolfPlayer meDied, EnumDeadType deadType) {
        // Tell other wolves that one of there friends died
        if (deadType == EnumDeadType.LOVE) {
//            game.notifyRole(WerewolfRole.class, "One of your family has died out of love");
            game.notifyRole(WerewolfRole.class, "Een van je vrienden is overleden aan de liefde");
        } else {
//            game.notifyRole(WerewolfRole.class, "One of your family has been killed");
            game.notifyRole(WerewolfRole.class, "Een van je vrienden is overleden");
        }

        return super.onDead(game, meDied, deadType);
    }
}
