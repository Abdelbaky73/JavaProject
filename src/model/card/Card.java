package model.card;

import java.util.ArrayList;

import model.player.Marble;
import engine.GameManager;
import engine.board.BoardManager;
import exception.ActionException;
import exception.GameException;
import exception.InvalidMarbleException;
import exception.InvalidSelectionException;

public abstract class Card {
	private final String name;
    private final String description;
    protected BoardManager boardManager;
    protected GameManager gameManager;

    public Card(String name, String description, BoardManager boardManager, GameManager gameManager) {
        this.name = name;
        this.description = description;
        this.boardManager = boardManager;
        this.gameManager = gameManager;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    public boolean validateMarbleColours(ArrayList<Marble> marbles) {
        if (marbles.size() == 0) return true;

        return marbles.stream().allMatch(
                marble -> marble.getColour() == gameManager.getActivePlayerColour()
        );
    }

    public abstract void act(ArrayList<Marble> marbles)
            throws ActionException, InvalidMarbleException, InvalidSelectionException;
    
    public boolean validateMarbleSize(ArrayList<Marble> marbles) {
    	  return marbles != null && marbles.size() == 1;
    	}
    public void play(ArrayList<Marble> marbles) throws GameException {
        if (!validateMarbleSize(marbles)) {
            throw new InvalidMarbleException("Incorrect number of marbles selected.");
        }

        if (!validateMarbleColours(marbles)) {
            throw new InvalidMarbleException("Selected marbles have incorrect colours.");
        }

        act(marbles);
    }

    
    
}
