package model.card.wild;

import java.util.ArrayList;

import engine.GameManager;
import engine.board.BoardManager;
import engine.board.Cell;
import engine.board.CellType;
import exception.ActionException;
import exception.InvalidMarbleException;
import model.Colour;
import model.player.Marble;

public class Burner extends Wild {

    public Burner(String name, String description, BoardManager boardManager, GameManager gameManager) {
        super(name, description, boardManager, gameManager);
    }

    @Override
    public boolean validateMarbleSize(ArrayList<Marble> marbles) {
        return marbles != null && marbles.size() == 1;
    }

    @Override
    public boolean validateMarbleColours(ArrayList<Marble> marbles) {
        if (marbles == null || marbles.isEmpty()) return false;

        Marble target = marbles.get(0);
        Colour activePlayer = gameManager.getActivePlayerColour();
        return target.getColour() != activePlayer;
    }

    @Override
    public void act(ArrayList<Marble> marbles) throws ActionException {
        if (marbles == null || marbles.size() != 1) return;

        Marble target = marbles.get(0);
        if (target == null) return;

        try {
            boardManager.destroyMarble(target);
        } catch (Exception e) {
            
        }

        ArrayList<Cell> track = boardManager.getTrack();
        for (Cell c : track) {
            if (c.getMarble() == target) {
                c.setMarble(null);
                break;
            }
        }

        target.setCell(null);
    }

}
