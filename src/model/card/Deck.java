package model.card;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import engine.GameManager;
import engine.board.BoardManager;
import model.card.standard.*;
import model.card.wild.Burner;
import model.card.wild.Saver;

public class Deck {
    private static final String CARDS_FILE = "Cards.csv";
    
    private static ArrayList<Card> cardsPool = new ArrayList<>();
    private static ArrayList<Card> firepit = new ArrayList<>();

	@SuppressWarnings("resource")
    public static void loadCardPool(BoardManager boardManager, GameManager gameManager) throws IOException {
        cardsPool.clear();
        firepit.clear();

        BufferedReader br = new BufferedReader(new FileReader(CARDS_FILE));

        while (br.ready()) {
            String nextLine = br.readLine();
            String[] data = nextLine.split(",");

            if (data.length == 0)
                throw new IOException(nextLine);

            String name = data[2];
            String description = data[3];
            int code = Integer.parseInt(data[0]);
            int frequency = Integer.parseInt(data[1]);

            for (int i = 0; i < frequency; i++) {
                Card card;

                if (code > 13) {
                    switch (code) {
                        case 14: card = new Burner(name, description, boardManager, gameManager); break;
                        case 15: card = new Saver(name, description, boardManager, gameManager); break;
                        default: throw new IOException(nextLine);
                    }
                } else {
                    int rank = Integer.parseInt(data[4]);
                    Suit suit = Suit.valueOf(data[5]);

                    switch (code) {
                        case 0: card = new Standard(name, description, rank, suit, boardManager, gameManager); break;
                        case 1: card = new Ace(name, description, suit, boardManager, gameManager); break;
                        case 4: card = new Four(name, description, suit, boardManager, gameManager); break;
                        case 5: card = new Five(name, description, suit, boardManager, gameManager); break;
                        case 7: card = new Seven(name, description, suit, boardManager, gameManager); break;
                        case 10: card = new Ten(name, description, suit, boardManager, gameManager); break;
                        case 11: card = new Jack(name, description, suit, boardManager, gameManager); break;
                        case 12: card = new Queen(name, description, suit, boardManager, gameManager); break;
                        case 13: card = new King(name, description, suit, boardManager, gameManager); break;
                        default: throw new IOException(nextLine);
                    }
                }

                cardsPool.add(card);
            }
        }

        Collections.shuffle(cardsPool);
    }

    
    public static ArrayList<Card> drawCards() {
        Collections.shuffle(cardsPool);
        ArrayList<Card> cards = new ArrayList<>(cardsPool.subList(0, 4));
        cardsPool.subList(0, 4).clear();
        return cards;
    }

    
    public static void burnCard(Card card) {
        firepit.add(card);
    }

    
    public static void refillPool(ArrayList<Card> cards) {
        cardsPool.addAll(cards);
        Collections.shuffle(cardsPool);
    }

    
    public static int getPoolSize() {
        return cardsPool.size();
    }
    public static ArrayList<Card> getFirepit() {
        return firepit;
    }
    public static ArrayList<Card> getCardsPool() {
		return cardsPool;
	}

}
