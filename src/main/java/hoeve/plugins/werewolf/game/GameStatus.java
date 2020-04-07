package hoeve.plugins.werewolf.game;

public enum GameStatus {
    PLAYERSELECT,  // Add and remove players allowed
    STARTUP, // Gives players their card
    DAY, // Day activities
    BURGERVOTE, // Voting is going on
    NIGHT, // Night activities
    WEREWOLFVOTE, // Voting is goig on
    WITCHACTIVITY, // rescue and or kill [player
    ENDED // 1 player left, winner is there
}
