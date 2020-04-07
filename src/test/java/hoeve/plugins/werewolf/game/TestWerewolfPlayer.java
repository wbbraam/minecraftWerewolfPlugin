package hoeve.plugins.werewolf.game;

import hoeve.plugins.werewolf.game.roles.WitchRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class TestWerewolfPlayer {

    @Test
    public void givenPlayerThenAllSettersAndGettersWork ()
    {
        WerewolfPlayer player = new WerewolfPlayer("myname");

        assertEquals("myname", player.getName());
        assertNull(player.getRole());
        assertEquals(true, player.isAlive());

        player.setRole(new WitchRole());
        player.setName("anothername");
        player.setAlive(false);

        assertEquals("anothername", player.getName());
        assertTrue("role", player.getRole() instanceof WitchRole);
        assertFalse(player.isAlive());
    }

}
