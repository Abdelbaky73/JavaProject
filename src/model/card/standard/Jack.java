package model.card.standard;

import java.util.ArrayList;

import model.Colour;
import model.player.Marble;
import engine.GameManager;
import engine.board.BoardManager;
import exception.ActionException;
import exception.CannotDiscardException;
import exception.IllegalDestroyException;
import exception.IllegalMovementException;
import exception.IllegalSwapException;
import exception.InvalidMarbleException;
import exception.InvalidSelectionException;

public class Jack extends Standard {

    public Jack(String name, String description, Suit suit, BoardManager boardManager, GameManager gameManager) {
        super(name, description, 11, suit, boardManager, gameManager);
    }
    @Override
    public boolean validateMarbleSize(ArrayList<Marble> marbles) {
    	return marbles.size() == 1 || marbles.size() == 2;
    }


    @Override
    public boolean validateMarbleColours(ArrayList<Marble> marbles) {
        if (marbles == null || marbles.isEmpty()) {
            return false;
        }
        
        Colour activePlayerColour = gameManager.getActivePlayerColour();
        
        
        if (marbles.size() == 1) {
            return marbles.get(0).getColour() == activePlayerColour;
        }
        
        
        if (marbles.size() == 2) {
            boolean hasActivePlayerMarble = false;
            boolean hasOtherPlayerMarble = false;
            
            for (Marble marble : marbles) {
                if (marble.getColour() == activePlayerColour) {
                    hasActivePlayerMarble = true;
                } else {
                    hasOtherPlayerMarble = true;
                }
            }
            
            return hasActivePlayerMarble && hasOtherPlayerMarble;
        }
        
        
        return false;
    }
  
    public void act(ArrayList<Marble> marbles) throws ActionException, InvalidMarbleException {
        if (marbles != null && marbles.size() == 2) {
            try {
                boardManager.swap(marbles.get(0), marbles.get(1));
                return;
            } catch (IllegalSwapException e) {
                
            }
        }
        
        if (marbles != null && marbles.size() == 1) {
            try {
                
                boardManager.moveBy(marbles.get(0), 11, false);
            } catch (IllegalMovementException | IllegalDestroyException e) {
                throw new IllegalMovementException("Failed to move marble with Jack card");
            }
        } else {
            throw new InvalidMarbleException("Jack card requires either 2 marbles to swap or 1 marble to move");
        }
    }






}
