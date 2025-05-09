package model.card.standard;

import java.util.ArrayList;

import model.player.Marble;
import engine.GameManager;
import engine.board.BoardManager;
import exception.ActionException;
import exception.CannotDiscardException;
import exception.IllegalDestroyException;
import exception.IllegalMovementException;
import exception.InvalidMarbleException;

public class Seven extends Standard {

    public Seven(String name, String description, Suit suit, BoardManager boardManager, GameManager gameManager) {
        super(name, description, 7, suit, boardManager, gameManager);
    }
    @Override
    public boolean validateMarbleSize(ArrayList<Marble> marbles) {
        if (marbles == null) return false;
        
        
        if (getRank() == 7) return marbles.size() == 1 || marbles.size() == 2;
        if (getRank() == 1 || getRank() == 13) return marbles.size() == 0;
        if (getRank() == 10 || getRank() == 12) return marbles.size() == 0;
        return marbles.size() == 1;
    }
    
    
    @Override
    public void act(ArrayList<Marble> marbles) throws ActionException, InvalidMarbleException {
        if (!validateMarbleSize(marbles)) {
            throw new InvalidMarbleException("Seven card requires 1 or 2 marbles");
        }

        if (marbles.size() == 1) {
            try {
                boardManager.moveBy(marbles.get(0), 7, false);
            } catch (IllegalMovementException | IllegalDestroyException e) {
                throw new IllegalMovementException("Failed to move marble with Seven card: " + e.getMessage());
            }
            return;
        }

        
        if (marbles.size() == 2) {
            int splitDistance;
            try {
                splitDistance = boardManager.getSplitDistance();
            } catch (Exception e) {
                throw new CannotDiscardException("Could not get split distance: " + e.getMessage());
            }

           
            if (splitDistance < 1 || splitDistance > 6) {
                throw new CannotDiscardException("Invalid split distance: " + splitDistance);
            }

            try {
                boardManager.moveBy(marbles.get(0), splitDistance, false);
                
                
                boardManager.moveBy(marbles.get(1), 7 - splitDistance, false);
            } catch (IllegalMovementException | IllegalDestroyException e) {
                throw new CannotDiscardException("Failed to split move marbles with Seven card: " + e.getMessage());
            }
        }
    }



}
