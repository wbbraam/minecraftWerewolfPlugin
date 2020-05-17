package hoeve.plugins.werewolf.game.roles;

import org.bukkit.ChatColor;

/**
 * Created by DeStilleGast 7-4-2020
 */
public class HunterRole extends BaseRole {
    
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
