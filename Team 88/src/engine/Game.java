package engine;

import engine.board.Board;
import engine.board.BoardManager;
import model.Colour;
import model.card.Card;
import model.card.Deck;
import model.player.CPU;
import model.player.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game implements GameManager {
    private final Board board;
    private final ArrayList<Player> players;
    private final ArrayList<Card> firePit;
    private int currentPlayerIndex;
    private int turn;

    
    public Game(String playerName) throws IOException {
        ArrayList<Colour> colors = new ArrayList<>();
        for (String color : Board.getAllColors()) {
            colors.add(Colour.valueOf(color.toUpperCase())); 
        }

        if (colors.size() < 4) {
            throw new IllegalStateException("Not enough colors to start the game!");
        }

        Collections.shuffle(colors);
        this.board = new Board(new ArrayList<>(colors), this);

        
        Deck.loadCardPool(board, this);

        
        players = new ArrayList<>();
        players.add(new Player(playerName, colors.get(0)));

        
        for (int i = 1; i <= 3; i++) {
            if (i < colors.size()) {
                players.add(new CPU("CPU " + i, colors.get(i), board));
            } else {
                System.err.println("Not enough colors for CPU " + i);
            }
        }

       
        for (Player player : players) {
            player.addCardsToHand(Deck.drawCards());
        }

        
        this.currentPlayerIndex = 0;
        this.turn = 0;
        this.firePit = new ArrayList<>();
    }

    
    public Board getBoard() {
        return board;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Card> getFirePit() {
        return firePit;
    }

  

   
}
