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
public class HunterRole extends IRole {
    
    private boolean takenRevenge = false;
    
    @Override
    public String getRoleName() {
        return ChatColor.GOLD + "Hunter";
    }


    public boolean hasTakenRevenge(){
        return this.takenRevenge;
    }

    public void setTakenRevenge(){
        this.takenRevenge = true;
    }
}
