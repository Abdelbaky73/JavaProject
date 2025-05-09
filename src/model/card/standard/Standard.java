package model.card.standard;

import java.util.ArrayList;

import engine.GameManager;
import engine.board.BoardManager;
import exception.ActionException;
import exception.CannotDiscardException;
import exception.InvalidCardException;
import exception.InvalidMarbleException;
import exception.InvalidSelectionException;
import exception.SplitOutOfRangeException;
import model.card.Card;
import model.player.Marble;

public class Standard extends Card {
    private final int rank;
    private final Suit suit;

    public Standard(String name, String description, int rank, Suit suit, BoardManager boardManager, GameManager gameManager) {
        super(name, description, boardManager, gameManager);
        this.rank = rank;
        this.suit = suit;
    }

    public int getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }
    
    @Override
    public void act(ArrayList<Marble> marbles) throws ActionException, InvalidMarbleException, InvalidSelectionException {
        if (rank < 1 || rank > 13) {
            throw new InvalidCardException("Invalid card rank: " + rank);
        }

        if (marbles == null || marbles.isEmpty()) {
            throw new InvalidMarbleException("No marbles selected for action.");
        }

        if (rank == 1 || rank == 13) { 
            gameManager.fieldMarble();
        } else if (rank == 10 || rank == 12) {
            gameManager.discardCard();
        } else if (rank == 7) { 
            int split = boardManager.getSplitDistance();
            if (split < 0 || split > 7) {
                throw new SplitOutOfRangeException("Invalid split distance.");
            }
            boardManager.moveBy(marbles.get(0), split, false);
            boardManager.moveBy(marbles.get(1), 7 - split, false);
        } else { 
            boardManager.moveBy(marbles.get(0), rank, false);
        }
    }


}
