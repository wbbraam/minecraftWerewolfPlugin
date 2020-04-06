package hoeve.plugins.werewolf.game;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class TestWerewolfGame {

    @Test
    public void givenANewGameThenModesCycleCorrectly ()
    {
        WerewolfGame game = new WerewolfGame();
        assertEquals(GameStatus.PLAYERSELECT, game.getStatus());

        game.nextStatus();
        assertEquals(GameStatus.STARTUP, game.getStatus());

        game.nextStatus();
        assertEquals(GameStatus.DAY, game.getStatus());

        game.nextStatus();
        assertEquals(GameStatus.BURGERVOTE, game.getStatus());

        game.nextStatus();
        assertEquals(GameStatus.NIGHT, game.getStatus());


        game.nextStatus();
        assertEquals(GameStatus.WEREWOLFVOTE, game.getStatus());

        game.nextStatus();
        assertEquals(GameStatus.DAY, game.getStatus());

        game.endStatus();
        assertEquals(GameStatus.ENDED, game.getStatus());

        game.nextStatus();
        assertEquals(GameStatus.PLAYERSELECT, game.getStatus());
    }

    @Test
    public void givenANewGameThenPlayerListIsEmpty ()
    {
        WerewolfGame game = new WerewolfGame();
        assertEquals(0, game.listPlayerNames().size());
    }

    @Test
    public void givenANewGameThenPlayerCanBeAdded ()
    {
        WerewolfGame game = new WerewolfGame();
        game.addPlayer("foo");
        assertEquals(1, game.listPlayerNames().size());

    }

    @Test
    public void givenANewGameThenPlayerCantBeAddedTwice ()
    {
        WerewolfGame game = new WerewolfGame();
        game.addPlayer("foo");
        game.addPlayer("foo");
        assertEquals(1, game.listPlayerNames().size());

    }

    @Test
    public void givenMultipleAddPlayerThenListFillsCorrect()
    {
        WerewolfGame game = new WerewolfGame();

        game.addPlayer("foobar");
        game.addPlayer("foo");
        game.addPlayer("bar");
        assertEquals(3, game.listPlayerNames().size());
    }

    @Test
    public void givenMultiplePlayerTheRemoveWorksCorrect()
    {
        WerewolfGame game = new WerewolfGame();

        game.addPlayer("foobar");
        game.addPlayer("foo");
        game.addPlayer("bar");
        assertEquals(3, game.listPlayerNames().size());
        game.removePlayer("fo");
        assertEquals(3, game.listPlayerNames().size());
        game.removePlayer("foo");
        assertEquals(2, game.listPlayerNames().size());

    }

    @Test
    public void givenASetOfPlayersThenListReturnsCorrectly ()
    {
        WerewolfGame game = new WerewolfGame();

        game.addPlayer("foo");
        game.addPlayer("bar");
        game.addPlayer("chicken");
        game.addPlayer("cow");
        game.addPlayer("dexter");
        game.addPlayer("deedee");
        game.addPlayer("johnny");
        game.addPlayer("bravo");

        game.assignRoles();

        assertEquals(8, game.listPlayerNames().size());
        //System.out.println(game.listPlayerNames());
    }
}
