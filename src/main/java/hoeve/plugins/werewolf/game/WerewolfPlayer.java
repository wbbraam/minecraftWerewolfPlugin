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
    private CommandSender playerObject;


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

    public WerewolfPlayer(CommandSender player) {
        this.playerObject = player;
    }

    public CommandSender getPlayer(){
        return playerObject;
    }

    public void onGameStart(WaitTillAllReady waiter){
        this.getRole().onGameStart(this, waiter);
    }

    public void onGameStatusChange(GameStatus status){
        this.getRole().onGameStateChange(this, status);
    }
}
