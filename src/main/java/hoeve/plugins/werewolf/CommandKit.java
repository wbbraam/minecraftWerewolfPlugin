package hoeve.plugins.werewolf;

import hoeve.plugins.werewolf.game.GameStatus;
import hoeve.plugins.werewolf.game.WerewolfGame;
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
    public WerewolfGame werewolfGame = new WerewolfGame();

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Commands only allowed for players");
            return false;
        }
        // First split the command into an array so we can check the sub commands
        String[] commandArray = command.toString().split(" ");

        if(!(label.equals("werewolf")))
        {
            sender.sendMessage("Unknown command in werewolf engine");
            getLogger().warning("Unknown commands entered werewolf engine. Ignored.");
        }

        if (args.length < 1) {
            sender.sendMessage("No correct command send");
            return false;
        }

        switch(args[0].toString()) {
            case "player":

                if(!sender.getName().equals(werewolfGame.getLeaderName())) {
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

    private boolean gameCommands(CommandSender sender, String[] args){
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
                werewolfGame.removePlayer(args[2]);
        }

        return true;
    }

    private boolean whisperCommands(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Wrong usage of whisper command, missing arguments.");
            return false;
        }
        String message = sender.getName()+" - "+werewolfGame.getPlayerRole(sender.getName())+": "+ String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        sendIfExist(werewolfGame.getLeaderName(), message);
        return true;
    }

    private boolean werechatCommands(CommandSender sender, String[] args){

        if (args.length < 2) {
            sender.sendMessage("Wrong usage of werechat command, missing arguments.");
            return false;
        }
        if (!werewolfGame.getPlayerRole(sender.getName()).equals("Werewolf"))
        {
            sender.sendMessage("You are no werewolf! Not for you!");
            return false;
        }
        if (werewolfGame.getStatus().equals(GameStatus.NIGHT) ||
                werewolfGame.getStatus().equals(GameStatus.WEREWOLFVOTE))
        {
            String message = sender.getName()+" says as werewolf: "+String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            List<String> playerList = werewolfGame.listPlayerNames();
            ListIterator<String> iter = playerList.listIterator();
            sendIfExist(werewolfGame.getLeaderName(), message);
            while(iter.hasNext()){
                String[] splitted = iter.next().split(" - ");
                String name = splitted[0];
                String role = splitted[1];

                if(role.equals("Werewolf")) {
                    sendIfExist(name, message);
                }
            }

        } else {
            sender.sendMessage("It is not night. Werechat doesnt work");
        }

        return true;
    }

    private boolean takeCommands (CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Wrong usage of take command, missing arguments.");
            return false;
        }


        switch(args[1]) {
            case "leadership":
                String newLeader = sender.getName();
                sendIfExist(werewolfGame.getLeaderName(), "Leadership taken by: "+newLeader);
                sendIfExist(newLeader, "Leadership taken from: "+werewolfGame.getLeaderName());
                werewolfGame.takeLeadership(newLeader);
                break;
        }

        return true;
    }

    private boolean stateCommands (CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Wrong usage of state command, missing arguments.");
            return false;
        }

        switch(args[1]) {
            case "list":
                sender.sendMessage("Current gamestate: "+werewolfGame.getStatus().toString());
                break;

            case "next":
                werewolfGame.nextStatus();
                sender.sendMessage("New gamestate: "+werewolfGame.getStatus().toString());
                if (werewolfGame.getStatus().equals(GameStatus.STARTUP))
                {
                    sender.sendMessage("Moving into starttup state, assigning roles.");
                    werewolfGame.assignRoles();
                    sender.sendMessage("Roles assigned, sending roles to players");

                    tellRolesToPlayers(sender);

                }
                break;

            case "end":
                werewolfGame.endStatus();
                sender.sendMessage("Game has ended. To start new game, use state next command.");
                break;
        }

        return true;
    }

    private void tellRolesToPlayers(CommandSender sender)
    {
        if(werewolfGame.getStatus().equals(GameStatus.PLAYERSELECT))
        {
            sender.sendMessage("Asked to send roles to all players, cant due to gamestate. No roles assigned yet.");
            return;
        }

        List<String> playerroleslist = werewolfGame.listPlayerNames();
        ListIterator<String> iter = playerroleslist.listIterator();

        while(iter.hasNext()){
            String[] playerRole = iter.next().split(" - ");
            Player player = getPlayer(playerRole[0]);
            if (player != null){
                player.sendMessage("Your role is: "+playerRole[1]);

            } else {
                sender.sendMessage("Player: " + playerRole[0] + "could not be found in game. Player role is: " + playerRole[1]);
            }
        }
    }

    private boolean playerCommands (CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage("Wrong usage of player command, missing arguments.");
            return false;
        }

        switch(args[1]){

            case "addoffline":
                if(args.length < 3) {
                    sender.sendMessage("Missing player name, cant addoffline");
                    break;
                }

                werewolfGame.addPlayer(args[2]);
                sender.sendMessage("Player added without check. Your responsibility! Not mine. :)");
                break;

            case "add":
                if (args.length < 3) {
                    sender.sendMessage("Missing player name, cant add");
                    break;
                }

                Player playeradd = getPlayer(args[2]);
                if (playeradd != null) {
                    werewolfGame.addPlayer(args[2]);
                    sender.sendMessage("Player added: "+args[2]);
                } else {
                    sender.sendMessage("Could not find player, offline maybe?");
                }
                break;

            case "remove":
                if (args.length < 3) {
                    sender.sendMessage("Missing player name, cant remove");
                    break;
                }
                werewolfGame.removePlayer(args[2]);
                sender.sendMessage("Player removed: "+args[2]);
                break;

            case "list":
                List<String> players = werewolfGame.listPlayerNames();
                ListIterator<String> i = players.listIterator();
                sender.sendMessage("Retreiving players - roles");
                while(i.hasNext()){
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

    private boolean sendToRole (String role, String message) {

        List<String> players = werewolfGame.listPlayerNames();
        ListIterator<String> iter = players.listIterator();

        while(iter.hasNext()) {
            String[] entries = iter.next().split(" - ");
            if (entries[1].equals(role)) {
                sendIfExist(entries[0], message);
            }
        }

        return true;
    }

    private boolean sendIfExist (String name, String message)
    {
        Player player = getPlayer(name);
        if (player != null){
            player.sendMessage(message);
        } else
        {
            getLogger().warning("Requested to send a mesage to non existing player: "+player);
            getLogger().warning("message: "+message);
        }
        return true;
    }

}
