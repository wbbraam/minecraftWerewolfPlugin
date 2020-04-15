package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.EnumDeadType;
import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;
import org.bukkit.ChatColor;

/**
 * Created by DeStilleGast 7-4-2020
 */
public class WitchRole extends IRole {
    @Override
    public String getRoleName() {
        return ChatColor.DARK_PURPLE + "Witch";
    }


    @Override
    public String onDead(WerewolfGame game, WerewolfPlayer meDied, EnumDeadType deadType) {
        // drop potions ?

        return super.onDead(game, meDied, deadType);
    }

    private boolean elixer = true, poison = true;
    public boolean hasElixer() {
        return elixer;
    }

    public boolean hasPoison(){
        return poison;
    }

    public void consumeElixer(){
        elixer = false;
    }

    public void consumePoison(){
        poison = false;
    }
}
