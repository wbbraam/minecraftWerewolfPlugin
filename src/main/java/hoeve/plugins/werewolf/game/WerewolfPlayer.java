package hoeve.plugins.werewolf.game;

public class WerewolfPlayer {
    String name = "";
    String role = "None";
    Boolean alive = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
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
}
