package hoeve.plugins.werewolf.game;

/**
 * Created by DeStilleGast 13-4-2020
 */
public enum EnumDeadType {
    WOLVES("Werewolf"),
    HUNTER("Hunter"),
    LOVE("broken heart"),
    WITCH("Witch"),
    VOTE("Hanged"),
    LEFT("left"),
    GAMEMASTER("GameMaster");

    private String friendlyName;

    EnumDeadType(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName(){
        return friendlyName;
    }
}
