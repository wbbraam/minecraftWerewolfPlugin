package hoeve.plugins.werewolf.game;

import hoeve.plugins.werewolf.game.roles.IRole;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class WerewolfPlayer {

    private IRole role = null;
    private Boolean alive = true;
    private Player playerObject;

    private WerewolfPlayer lover;
    private GameMode oldGamemode;

    public WerewolfPlayer(Player player) {
        this.playerObject = player;
        this.oldGamemode = player.getGameMode();
    }


    public String getName() {
        return playerObject.getDisplayName();
    }


    public IRole getRole() {
        return role;
    }

    public void setRole(IRole role) {
        this.role = role;
    }

    public Boolean isAlive() {
        return alive;
    }

    public void setAlive(Boolean alive) {
        this.alive = alive;
    }

    public Player getPlayer() {
        return playerObject;
    }


    public void onGameStatusChange(WerewolfGame game, GameStatus status) {
        this.getRole().onGameStateChange(game, this, status);
    }

    public void kill() {
        if (!this.isAlive()) return;
        this.alive = false;

        if (this.getPlayer().isOnline()) {
            this.getPlayer().setGameMode(GameMode.SPECTATOR);

            this.getPlayer().getWorld().strikeLightningEffect(this.getPlayer().getLocation());
        }
    }

    public String getDeathMessage(WerewolfGame game, EnumDeadType deadType) {
        return this.getRole().onDead(game, this, deadType);
    }

    public WerewolfPlayer getLover() {
        return this.lover;
    }

    public void setLover(WerewolfPlayer player) {
        this.lover = player;
    }

    public void onPlayerLeave(WerewolfGame game){
        playerObject.setGameMode(this.oldGamemode);

        if(game.getStatus() != GameStatus.ENDED){
            game.notifyGameMaster(playerObject.getDisplayName() + " has left the game");
        }
    }
}
