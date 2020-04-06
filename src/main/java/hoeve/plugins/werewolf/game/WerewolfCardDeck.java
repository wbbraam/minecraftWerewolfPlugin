package hoeve.plugins.werewolf.game;

import java.util.ArrayList;
import java.util.Random;

public class WerewolfCardDeck {
    private ArrayList<String> playingCards = new ArrayList<String>();

    public boolean resetDeck(Integer playerAmount){

        playingCards = new ArrayList<String>();

        int specialCommonerAmount = 4;
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


        int werewolfAmount = (int) Math.ceil(playerAmount / 3);
        int commonerAmount = (playerAmount - specialCommonerAmount - werewolfAmount);

        for (int i = 0; i < commonerAmount; i++)
        {
            playingCards.add("Commoner");
        }

        for (int i = 0; i < werewolfAmount; i++)
        {
            playingCards.add("Werewolf");
        }

        playingCards.add("Seeer");
        playingCards.add("Witch");
        playingCards.add("Hunter");
        playingCards.add("Cupid");

        return true;
    }

    public String drawCard() {
        Random randomSeed = new Random();
        int randomInteger = randomSeed.nextInt(playingCards.size());

        String roleToReturn = playingCards.get(randomInteger);
        playingCards.remove(randomInteger);

        return roleToReturn;
    }

    public int getDeckSize() {
        return playingCards.size();
    }


}
