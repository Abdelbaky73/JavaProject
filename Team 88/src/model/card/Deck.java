package model.card;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import model.card.standard.Ace;
import model.card.standard.Five;
import model.card.standard.Four;
import model.card.standard.Jack;
import model.card.standard.King;
import model.card.standard.Queen;
import model.card.standard.Seven;
import model.card.standard.Standard;
import model.card.standard.Suit;
import model.card.standard.Ten;
import model.card.wild.Burner;
import model.card.wild.Saver;
import engine.GameManager;
import engine.board.BoardManager;

public class Deck {
    public static final String CARDS_FILE = "Cards.csv";
    private static ArrayList<Card> cardsPool = new ArrayList<>(); 

    public static void loadCardPool(BoardManager boardManager, GameManager gameManager) throws IOException {
        cardsPool = new ArrayList<>(); 
        BufferedReader br = new BufferedReader(new FileReader(CARDS_FILE));
        
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length < 4) {
                    System.out.println("Skipping invalid CSV entry: " + line);
                }
                    int code = Integer.parseInt(data[0].trim());
                    int frequency = Integer.parseInt(data[1].trim());
                    String name = data[2].trim();
                    String description = data[3].trim();

                    for (int i = 0; i < frequency; i++) {
                        Card card = null;

                        if (code == 14) {
                            card = new Burner(name, description, boardManager, gameManager);
                        } else if (code == 15) {
                            card = new Saver(name, description, boardManager, gameManager);
                        } else {
                            if (data.length >= 6) {
                                int rank = Integer.parseInt(data[4].trim());
                                Suit suit = Suit.valueOf(data[5].trim().toUpperCase());
                                if (rank == 1) {
                                    card = new Ace(name, description, suit, boardManager, gameManager);
                                } else if (rank == 4) {
                                    card = new Four(name, description, suit, boardManager, gameManager);
                                } else if (rank == 5) {
                                    card = new Five(name, description, suit, boardManager, gameManager);
                                } else if (rank == 7) {  
                                    card = new Seven(name, description, suit, boardManager, gameManager);
                                } else if (rank == 10) {  
                                    card = new Ten(name, description, suit, boardManager, gameManager);
                                } else if (rank == 11) {
                                    card = new Jack(name, description, suit, boardManager, gameManager);
                                } else if (rank == 12) {
                                    card = new Queen(name, description, suit, boardManager, gameManager);
                                } else if (rank == 13) {
                                    card = new King(name, description, suit, boardManager, gameManager);
                                } else {
                                    card = new Standard(name, description, rank, suit, boardManager, gameManager);
                                }

                            } else {
                                System.out.println("Skipping standard card (missing rank/suit): " + line);
                            }
                        }

                        if (card != null) {
                            cardsPool.add(card);
                        }
                    }
                
            }
        
    }

    public static ArrayList<Card> drawCards() {
        ArrayList<Card> drawnCards = new ArrayList<>();
    	Collections.shuffle(cardsPool);
        for (int i = 0; i <4 ; i++) {
            drawnCards.add(cardsPool.remove(0));
        }
        return drawnCards;
    }
}
