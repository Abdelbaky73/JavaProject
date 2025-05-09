package model.card.standard;

import java.util.ArrayList;

import model.player.Marble;
import engine.GameManager;
import engine.board.BoardManager;

public class Queen extends Standard {

    public Queen(String name, String description, Suit suit, BoardManager boardManager, GameManager gameManager) {
        super(name, description, 12, suit, boardManager, gameManager);
    }
    public boolean validateMarbleSize(ArrayList<Marble> marbles) {
        if (getRank() == 1 || getRank() == 13) return marbles.size() == 1;       
        if (getRank() == 7) return marbles.size() == 2;                     
        if (getRank() == 10 || getRank() == 12) return marbles.size() == 1;     
        return marbles.size() == 1;                                    
    }
}
