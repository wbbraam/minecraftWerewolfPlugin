package hoeve.plugins.werewolf.game;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;


public class TesttWerewolfPlayer {

    @Test
    public void givenPlayerThenAllSettersAndGettersWork ()
    {
        WerewolfPlayer player = new WerewolfPlayer("myname");

        assertEquals("myname", player.getName());
        assertEquals("None", player.getRole());
        assertEquals(true, player.isAlive());

        player.setRole("dork");
        player.setName("anothername");
        player.setAlive(false);

        assertEquals("anothername", player.getName());
        assertEquals("dork", player.getRole());
        assertFalse(player.isAlive());
    }

}
