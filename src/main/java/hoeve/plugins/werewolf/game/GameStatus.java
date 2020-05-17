package hoeve.plugins.werewolf.game;

public enum GameStatus {
    PLAYERSELECT("Waiting for others"),  // Add and remove players allowed
    STARTUP("Giving out roles"), // Gives players their card
    CUPIDO("Cupido activity"),
    DAY("Waking up"), // Day activities
    BURGERVOTE("Vote"), // Voting is going on
    NIGHT("Going to bed"), // Night activities
    WEREWOLFVOTE("Werewolves are awake"), // Voting is goig on
    WITCHACTIVITY("Witches are plotting"), // rescue and or kill [player,
    ENDED("Ended"); // Only wolves or villagers, winner is there

    private String friendlyName;

    GameStatus(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public String toString() {
        return friendlyName;
    }
}
