package hoeve.plugins.werewolf.game;

import hoeve.plugins.werewolf.game.roles.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class WerewolfCardDeck {
    private ArrayList<BaseRole> playingCards = new ArrayList<>();

    public boolean resetDeck(Integer playerAmount){

        playingCards = new ArrayList<>();

//        int specialCommonerAmount = 4;
        // Seer
        // Witch
        // Hunter
        // Cupid

        if (playerAmount < 6) {
            return false;
        }

        if (playerAmount > 50) {
            return false;
        }

        playerAmount = playerAmount + 1; // Always make sure there is 1 card left



        // Special roles
        playingCards.add(new OracleRole());
        playingCards.add(new WitchRole());
        playingCards.add(new HunterRole());
        playingCards.add(new CupidoRole());

        int werewolfAmount = (int) Math.ceil(playerAmount / 3F);
        int commonerAmount = (playerAmount - werewolfAmount - playingCards.size());

        for (int i = 0; i < commonerAmount; i++)
        {
            playingCards.add(new CommonRole());
        }

        for (int i = 0; i < werewolfAmount; i++)
        {
            playingCards.add(new WerewolfRole());
        }

        //Shuffle the cards ;)
        Collections.shuffle(playingCards);

        return true;
    }

    private Random randomSeed = new Random();
    public BaseRole drawCard() {
        int randomInteger = randomSeed.nextInt(playingCards.size());

        BaseRole roleToReturn = playingCards.get(randomInteger);
        playingCards.remove(randomInteger);

        return roleToReturn;
    }

    public int getDeckSize() {
        return playingCards.size();
    }

}
