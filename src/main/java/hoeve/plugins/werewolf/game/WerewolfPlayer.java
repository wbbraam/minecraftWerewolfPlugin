package hoeve.plugins.werewolf.game;

import hoeve.plugins.werewolf.game.roles.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WerewolfPlayer {

    String name = "";
    IRole role = null;
    Boolean alive = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public WerewolfPlayer(String name) {
        this.name = name;
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(name);
    }
}
