package hoeve.plugins.werewolf.game.roles;

import hoeve.plugins.werewolf.game.EnumDeadType;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import org.bukkit.ChatColor;

/**
 * Created by DeStilleGast 7-4-2020
 */
public class OracleRole extends BaseRole {
    @Override
    public String getRoleName() {
        return ChatColor.AQUA + "Oracle";
    }


    @Override
    public String onDead(WerewolfGame game, WerewolfPlayer meDied, EnumDeadType deadType) {
        // all the knowledge is lost
//        return "his/her knowledge are lost for ever";
        return "alles wat hij/zij wist over het groepje is nu voor altijd kwijt";
    }
}
