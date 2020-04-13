package hoeve.plugins.werewolf.game;

import hoeve.plugins.werewolf.game.helpers.WaitTillAllReady;
import hoeve.plugins.werewolf.game.roles.IRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WerewolfPlayer {

//    String name = "";
    private IRole role = null;
    private Boolean alive = true;
    private Player playerObject;

    private WerewolfPlayer lover;


    public String getName() {
        return playerObject.getName();
    }

//    public void setName(String name) {
//        this.name = name;
//    }

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

    public WerewolfPlayer(Player player) {
        this.playerObject = player;
    }

    public Player getPlayer(){
        return playerObject;
    }

    public void onGameStart(WaitTillAllReady waiter){
        this.getRole().onGameStart(this, waiter);
    }

    public void onGameStatusChange(GameStatus status){
        this.getRole().onGameStateChange(this, status);
    }

    public void onDead(WerewolfPlayer killedBy){
        if(!this.isAlive()) return;

        this.getRole().onDead(killedBy);

        if(this.lover != null){
            this.lover.onDead(this);
        }

        this.alive = false;
    }

    public void setLover(WerewolfPlayer player) {
        this.lover = player;
    }
}
