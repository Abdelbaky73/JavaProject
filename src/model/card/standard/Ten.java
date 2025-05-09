package model.card.standard;

import java.util.ArrayList;

import model.card.Card;
import model.player.Marble;
import model.player.Player;
import engine.Game;
import engine.GameManager;
import engine.board.BoardManager;
import exception.ActionException;
import exception.CannotDiscardException;
import exception.IllegalDestroyException;
import exception.IllegalMovementException;
import exception.InvalidMarbleException;

public class Ten extends Standard {

    public Ten(String name, String description, Suit suit, BoardManager boardManager, GameManager gameManager) {
        super(name, description, 10, suit, boardManager, gameManager);
    }

    @Override
    public boolean validateMarbleSize(ArrayList<Marble> marbles) {
        return marbles == null || marbles.size() <= 1;
    }

    @Override
    public void act(ArrayList<Marble> marbles) throws CannotDiscardException {
        if (marbles != null && !marbles.isEmpty()) {
            try {
                boardManager.moveBy(marbles.get(0), 10, false); 
            } catch (IllegalMovementException | IllegalDestroyException e) {
                throw new RuntimeException("Failed to move marble in Ten card", e);
            }
            return; 
        }
        Game game = null;
        if (gameManager instanceof Game) {
            game = (Game) gameManager;
        }
        if (game == null) {
            throw new IllegalStateException("gameManager is not an instance of Game.");
        }

        ArrayList<Player> players = game.getPlayers();
        int currentPlayerIndex = game.getCurrentPlayerIndex();
        int nextPlayerIndex = (currentPlayerIndex + 1) % players.size();
        Player nextPlayer = players.get(nextPlayerIndex);

        ArrayList<Card> nextPlayerHand = nextPlayer.getHand();
        if (!nextPlayerHand.isEmpty()) {
            int randomIndex = (int) (Math.random() * nextPlayerHand.size());
            nextPlayerHand.remove(randomIndex);
        } else {
            throw new CannotDiscardException("Next player has no cards to discard.");
        }
    }

    


}




