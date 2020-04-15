package hoeve.plugins.werewolf.game;

public enum GameStatus {
    PLAYERSELECT,  // Add and remove players allowed
    STARTUP, // Gives players their card
    CUPIDO,
    DAY, // Day activities
    BURGERVOTE, // Voting is going on
    NIGHT, // Night activities
    WEREWOLFVOTE, // Voting is goig on
    WITCHACTIVITY, // rescue and or kill [player,
    ENDED, // Only wolves or villagers, winner is there
    MISSING
}
