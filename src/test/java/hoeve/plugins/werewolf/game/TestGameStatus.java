package hoeve.plugins.werewolf.game;


import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestGameStatus {

    @Test
    public void TestExistenceOfEnum () {
        assertEquals("PLAYERSELECT", GameStatus.PLAYERSELECT.toString());
        assertEquals("STARTUP", GameStatus.STARTUP.toString());
        assertEquals("DAY", GameStatus.DAY.toString());
        assertEquals("BURGERVOTE", GameStatus.BURGERVOTE.toString());
        assertEquals("NIGHT", GameStatus.NIGHT.toString());
        assertEquals("WEREWOLFVOTE", GameStatus.WEREWOLFVOTE.toString());
        assertEquals("ENDED", GameStatus.ENDED.toString());
    }


}
