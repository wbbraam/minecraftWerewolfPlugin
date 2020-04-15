package hoeve.plugins.werewolf;

import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfGame;
import hoeve.plugins.werewolf.game.WerewolfPlayer;
import hoeve.plugins.werewolf.game.roles.WerewolfRole;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getPlayer;

public class CommandKit implements CommandExecutor {

    // This method is called, when somebody uses our command
    //@Override
    private final WerewolfGame werewolfGame;
    private final WerewolfPlugin plugin;

    public CommandKit(WerewolfGame werewolfGame, WerewolfPlugin plugin) {
        this.werewolfGame = werewolfGame;
        this.plugin = plugin;
    }

    /*


    werewolf plugin

    COMMANDS:
    PLAYER SETTING COMMANDS, Only for leader and most during PLAYERSELECT STATE of game.
    /werewolf player add <playername>
    /werewolf player remove <playername>
    /werewolf player list <playername>
    /werewolf player reset <playername>
    /werewolf player tellroles (stuurt iedereen zijn rol opnieuw)

    Add player without checking if he exists!
    /werewolf player addoffline <playername>

    ONLY FOR LEADER
    Cycle through gane
    /werewolf state next
    /werewolf state list
    /werewolf state end

    ONLY FOR LEADER
    Kills a player (literaly) and removes from game.
    /werewolf game kill
    /werewolf rolemsg <role> <message to send>

    PLAYER COMMANDS:
    /werewolf whisper   <pm naar leader>
    /werewolf werechat  <pm naar alle weerwolven en leader alleen als nacht>

    /werewolf take leadership  (this makes you the leader of the game)

    GAMESTATES/GAME FLOW:
    PLAYERSELECT
    Add remove player allowed 
    STARTUP
    roles are automatically assigned and given to players through pm. Use this phase to ask cupid to whisper you lovers.
    Pm lovers in game they are lovers

    Ask seer to whisper you who to check. answer in pm.

    DAY
    Discussion all over the place who to kill

    DAYVOTE
    Can be done at day step. but as a reminder

    NIGHT
    /werewolf werechat is now available for werewolfs to discuss victem

    WEREWOLFVOTE
    Ask werewolfs to come to a decision via werechat
    PM witch if she wants to save this person
    PM witch if she wants to kill someone (ask to whipser back)

    Kill off the dead players, take into account hunter and lovers

    State goes back to DAY

    END
    Only triggerd by /werewolf state end
    DRaw up th econclusion

    next state is playerselect.

     */

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Commands only allowed for players");
            return false;
        }
        // First split the command into an array so we can check the sub commands
        String[] commandArray = command.toString().split(" ");



        if (args.length < 1) {
            sender.sendMessage("No correct command send");
            return false;
        }

        switch (args[0]) {
            case "player":

                if (!sender.getName().equals(werewolfGame.getLeaderName())) {
                    sender.sendMessage("You are not the leader!");
                    return false;
                }

                if (werewolfGame.getStatus().equals(GameStatus.PLAYERSELECT)) {
                    playerCommands(sender, args);
                } else {
                    if (args[1].equals("list")) {
                        playerCommands(sender, args);
                    } else {
                        sender.sendMessage("Can only change playerlist during PLAYERSELECT phase");

                    }
                }
                break;

            case "state":
                stateCommands(sender, args);
                break;

            case "take":
                takeCommands(sender, args);
                break;

            case "whisper":
                whisperCommands(sender, args);
                break;

            case "werechat":
                werechatCommands(sender, args);
                break;

            case "rolemsg":
                rolemsgCommands(sender, args);
                break;

            case "game":
                gameCommands(sender, args);
                break;
        }


        return true;
    }

    private boolean rolemsgCommands(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("Wrong usage of rolemsg command.");
            return false;
        }

        if (!werewolfGame.getLeaderName().equals(sender.getName())) {
            sender.sendMessage("Only available to game leader.");
        }

        sendToRole(args[1], String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
        return true;
    }

    private boolean gameCommands(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("Wrong usage of game command, missing arguments.");
            return false;
        }

        if (!sender.getName().equals(werewolfGame.getLeaderName())) {
            sender.sendMessage("You need to be leader to use these commands");
        }

        switch (args[1]) {
            case "kill":
                Player victem = getPlayer(args[2]);
                if (victem != null) {
                    victem.setHealth(0);
                }
//                werewolfGame.removePlayer(args[2]);
        }

        return true;
    }

    private boolean whisperCommands(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Wrong usage of whisper command, missing arguments.");
            return false;
        }
        String message = sender.getName() + " - " + werewolfGame.getPlayerRole(sender.getName()) + ": " + String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        sendIfExist(werewolfGame.getGameMaster(), message);
        return true;
    }

    private boolean werechatCommands(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage("Wrong usage of werechat command, missing arguments.");
            return false;
        }
        if (!(werewolfGame.getPlayerRole(sender.getName()) instanceof WerewolfRole)) {
            sender.sendMessage("You are no werewolf! Not for you!");
            return false;
        }
        if (werewolfGame.getStatus().equals(GameStatus.NIGHT) || werewolfGame.getStatus().equals(GameStatus.WEREWOLFVOTE)) {
            String message = sender.getName() + " says as werewolf: " + String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            sendIfExist(werewolfGame.getGameMaster(), message);

            werewolfGame.getPlayerList().forEach(p -> {
                if (p.getRole() instanceof WerewolfRole) {
                    sendIfExist(p, message);
                }
            });


        } else {
            sender.sendMessage("It is not night. Werechat doesnt work");
        }

        return true;
    }

    private boolean takeCommands(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Wrong usage of take command, missing arguments.");
            return false;
        }


        switch (args[1]) {
            case "leadership":
                WerewolfPlayer oldLeader = werewolfGame.getGameMaster();
                sendIfExist(oldLeader, "Leadership taken by: " + sender.getName());

//                werewolfGame.setGameMaster(sender);
                sendIfExist(werewolfGame.getGameMaster(), "Leadership taken from: " + oldLeader.getName());
                break;
        }

        return true;
    }

    private boolean stateCommands(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Wrong usage of state command, missing arguments.");
            return false;
        }

        switch (args[1]) {
            case "list":
                sender.sendMessage("Current gamestate: " + werewolfGame.getStatus().toString());
                break;

            case "start":
                if(werewolfGame.getStatus() == GameStatus.PLAYERSELECT) {
                    werewolfGame.nextStatus();
                    sender.sendMessage("Moving into starttup state, assigning roles.");
                    werewolfGame.startGame();
                    sender.sendMessage("Roles assigned, sending roles to players");

                    tellRolesToPlayers(sender);

                    werewolfGame.executeStartup(() -> { sender.sendMessage("Everyone has been set ready, players that are not marked as ready will be kicked out of the game"); });

                }else{
                    sender.sendMessage("Stop, we are already started !");
                }
                break;


            case "next":
                if(werewolfGame.getStatus() == GameStatus.STARTUP){
                    sender.sendMessage("Stop, we need to start first !");
                    break;
                }
                werewolfGame.nextStatus();
                sender.sendMessage("New gamestate: " + werewolfGame.getStatus().toString());
                werewolfGame.executeNewStatus();
                break;

            case "end":
                werewolfGame.finishGame();
                sender.sendMessage("Game has ended. To start new game, use state next command.");
                break;
        }

        return true;
    }

    private void tellRolesToPlayers(CommandSender sender) {
        if (werewolfGame.getStatus().equals(GameStatus.PLAYERSELECT)) {
            sender.sendMessage("Asked to send roles to all players, cant due to gamestate. No roles assigned yet.");
            return;
        }

        List<String> playerroleslist = werewolfGame.listPlayerNames();
        ListIterator<String> iter = playerroleslist.listIterator();

        while (iter.hasNext()) {
            String[] playerRole = iter.next().split(" - ");
            Player player = getPlayer(playerRole[0]);
            if (player != null) {
                player.sendMessage("Your role is: " + playerRole[1]);

            } else {
                sender.sendMessage("Player: " + playerRole[0] + "could not be found in game. Player role is: " + playerRole[1]);
            }
        }
    }

    private boolean playerCommands(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage("Wrong usage of player command, missing arguments.");
            return false;
        }

        switch (args[1]) {

            case "addoffline":
                if (args.length < 3) {
                    sender.sendMessage("Missing player name, cant addoffline");
                    break;
                }

//                werewolfGame.addPlayer(args[2]);
                sender.sendMessage("Player added without check. Your responsibility! Not mine. :)");
                break;

            case "add":
                if (args.length < 3) {
                    sender.sendMessage("Missing player name, cant add");
                    break;
                }

                Player playeradd = getPlayer(args[2]);
                if (playeradd != null) {
//                    werewolfGame.addPlayer(args[2]);
                    sender.sendMessage("Player added: " + args[2]);
                } else {
                    sender.sendMessage("Could not find player, offline maybe?");
                }
                break;

            case "remove":
                if (args.length < 3) {
                    sender.sendMessage("Missing player name, cant remove");
                    break;
                }
//                werewolfGame.removePlayer(args[2]);
                sender.sendMessage("Player removed: " + args[2]);
                break;

            case "list":
                List<String> players = werewolfGame.listPlayerNames();
                ListIterator<String> i = players.listIterator();
                sender.sendMessage("Retreiving players - roles");
                while (i.hasNext()) {
                    sender.sendMessage(i.next());
                }
                break;

            case "clear":
                werewolfGame.clearPlayerList();
                sender.sendMessage("PLayerlist cleared. It is empty now.");
                break;

            case "tellroles":
                tellRolesToPlayers(sender);
                break;
        }
        return true;
    }

    private boolean sendToRole(String role, String message) {

        werewolfGame.getPlayerList().forEach(p -> {
            if (p.getRole().getRoleName().equalsIgnoreCase(role)) {
                sendIfExist(p, message);
            }
        });

        return true;
    }

    private boolean sendIfExist(WerewolfPlayer gamePlayer, String message) {
        if(werewolfGame.isPlayerValid(gamePlayer)) {
            CommandSender player = gamePlayer.getPlayer();
            if (player != null) {
//                plugin.tellPlayer(player, message);
            } else {
                getLogger().warning("Requested to send a message to non existing player: " + gamePlayer.getName());
                getLogger().warning("message: " + message);
            }
            return true;
        }
        return false;
    }

}
