package model.card.standard;

import java.util.ArrayList;

import model.player.Marble;
import engine.GameManager;
import engine.board.BoardManager;
import exception.ActionException;
import exception.CannotDiscardException;
import exception.CannotFieldException;
import exception.IllegalDestroyException;
import exception.IllegalMovementException;
import exception.InvalidMarbleException;

public class King extends Standard {

    public King(String name, String description, Suit suit, BoardManager boardManager, GameManager gameManager) {
        super(name, description, 13, suit, boardManager, gameManager);
    }

    @Override
    public boolean validateMarbleSize(ArrayList<Marble> marbles) {
        return marbles != null &&  (marbles.size() == 0 || marbles.size() == 1);
    }

    @Override
    public void act(ArrayList<Marble> marbles) throws ActionException, InvalidMarbleException {
        if (!validateMarbleSize(marbles)) {
            throw new InvalidMarbleException("King card requires 0 or 1 marble");
        }
        
        
        try {
            if (marbles.isEmpty()) {
                gameManager.fieldMarble();
                return; 
            }
        } catch (CannotFieldException | IllegalDestroyException e) {
            // Fielding failed, proceed to option 2
        }
        
        
        if (marbles.size() != 1) {
            throw new InvalidMarbleException("King card requires exactly 1 marble to move");
        }
        
        Marble marble = marbles.get(0);
        
        try {
            boardManager.moveBy(marble, 13, true);
        } catch (IllegalMovementException | IllegalDestroyException e) {
            throw new IllegalMovementException("Failed to move marble with King card: " + e.getMessage());
        }
    }

}
