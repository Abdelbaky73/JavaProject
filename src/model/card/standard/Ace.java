package model.card.standard;
import java.util.ArrayList;

import model.player.Marble;
import engine.GameManager;
import engine.board.BoardManager;
import exception.ActionException;
import exception.CannotFieldException;
import exception.InvalidMarbleException;

public  class Ace extends Standard {

    public Ace(String name, String description, Suit suit, BoardManager boardManager, GameManager gameManager) {
        super(name, description, 1, suit, boardManager, gameManager);
    }
    @Override
    public boolean validateMarbleSize(ArrayList<Marble> marbles) {
        return marbles != null && (marbles.size() == 1 || marbles.size() == 0);
    }

    @Override
    public void act(ArrayList<Marble> marbles) throws ActionException, InvalidMarbleException {
        if (marbles == null || marbles.isEmpty()) {
            throw new CannotFieldException("Cannot field: no marbles available.");
        }
        if (!validateMarbleSize(marbles)) {
            throw new InvalidMarbleException("Invalid number of marbles for Ace card.");
        }
        boardManager.moveBy(marbles.get(0), 1, false); 
    }


}
