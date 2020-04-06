package hoeve.plugins.werewolf.game;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestWerewofCardDeck {

    @Test
    public void testGivenNewDeckThenSizeIsCorrect ()
    {
        WerewolfCardDeck deck = new WerewolfCardDeck();
        deck.resetDeck(10);
        assertEquals(11, deck.getDeckSize());

    }

    @Test
    public void testGivenNewDeckAndOneCardDrawThenSizeIsCorrect ()
    {
        WerewolfCardDeck deck = new WerewolfCardDeck();
        deck.resetDeck(10);
        deck.drawCard();
        assertEquals(10, deck.getDeckSize());
    }

    @Test
    public void testGivenDeckDrawnAndResetThenSizeIsCorrect ()
    {
        WerewolfCardDeck deck = new WerewolfCardDeck();
        deck.resetDeck(10);
        deck.drawCard();
        deck.resetDeck(15);
        assertEquals(16, deck.getDeckSize());

    }

    @Test
    public void testGivenVariousDecksizesThenBoundaryCheckCorrect ()
    {
        WerewolfCardDeck deck = new WerewolfCardDeck();
        assertFalse(deck.resetDeck(5));
        assertTrue(deck.resetDeck(6));
        assertTrue(deck.resetDeck(50));
        assertFalse(deck.resetDeck(51));

    }
}
