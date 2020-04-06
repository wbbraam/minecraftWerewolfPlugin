package hoeve.plugins.werewolf.game;

import com.sun.rowset.internal.WebRowSetXmlWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class WerewolfGame {

    private ArrayList<WerewolfPlayer> playerList = new ArrayList<WerewolfPlayer>();
    private WerewolfCardDeck cardDeck = new WerewolfCardDeck();
    private String leaderName = "";


    private GameStatus gamestatus;

    public WerewolfGame() {
        gamestatus = GameStatus.PLAYERSELECT;
        playerList = new ArrayList<WerewolfPlayer>();
    }

    ///////////////////////////
    // Game startup commands //
    ///////////////////////////
    
    public void assignRoles ()
    {
        cardDeck.resetDeck(playerList.size());
        for (WerewolfPlayer player : playerList){
            //System.out.println("Give card to:" + player.getName());
            //System.out.println("Cards left before dealing:"+ cardDeck.getDeckSize());
            player.setRole(cardDeck.drawCard());
        }
    }
    
    //////////////////////////////
    // PLAYER LIST MANIPULATION //
    //////////////////////////////
    public Boolean takeLeadership (String name){
        leaderName = name;
        return true;
    }

    public String getLeaderName (){
        return leaderName;
    }

    public Boolean addPlayer (String name){

        ListIterator<WerewolfPlayer> iter = playerList.listIterator();
        while (iter.hasNext())
        {
            if(iter.next().getName().equals(name))
            {
                return false;
            }
        }

        playerList.add(new WerewolfPlayer(name));
        return true;

    }

    public Boolean removePlayer (String name){
        ListIterator<WerewolfPlayer> iter = playerList.listIterator();
        while(iter.hasNext()) {
            if (iter.next().getName().equals(name))
            {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    public String getPlayerRole (String name) {
        ListIterator<WerewolfPlayer> iter = playerList.listIterator();
        while(iter.hasNext()) {
            WerewolfPlayer player = iter.next();
            if (player.getName().equals(name))
            {
                return player.getRole();
            }
        }
        return "";
    }

    public Boolean clearPlayerList (){
        playerList = new ArrayList<WerewolfPlayer>();
        return true;
    }

    public ArrayList<String> listPlayerNames (){
        ArrayList<String> returnList = new ArrayList<String>();
        ListIterator<WerewolfPlayer> iter = playerList.listIterator();
        while(iter.hasNext()) {
            WerewolfPlayer tempPlayer = iter.next();
            returnList.add(tempPlayer.getName() + " - " + tempPlayer.getRole());
        }

        return returnList;
    }

    ///////////////////////
    // GAMESTATE METHODS //
    ///////////////////////
    public GameStatus nextStatus () {
        switch(gamestatus) {
            case PLAYERSELECT:
                gamestatus = GameStatus.STARTUP;
                return GameStatus.STARTUP;

            case STARTUP:
                gamestatus = GameStatus.DAY;
                return gamestatus;

            case DAY:
                gamestatus = GameStatus.BURGERVOTE;
                return gamestatus;

            case BURGERVOTE:
                gamestatus = GameStatus.NIGHT;
                return gamestatus;

            case NIGHT:
                gamestatus = GameStatus.WEREWOLFVOTE;
                return gamestatus;

            case WEREWOLFVOTE:
                gamestatus = GameStatus.DAY;
                return gamestatus;

            case ENDED:
                gamestatus = GameStatus.PLAYERSELECT;
                return gamestatus;
        }

        return gamestatus;
    }

    public void endStatus () {
            gamestatus = GameStatus.ENDED;
    }

    public GameStatus getStatus () {
        return gamestatus;
    }

}
