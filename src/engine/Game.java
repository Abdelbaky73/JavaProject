package engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import engine.board.Board;
import exception.CannotDiscardException;
import exception.CannotFieldException;
import exception.GameException;
import exception.IllegalDestroyException;
import exception.InvalidCardException;
import exception.InvalidMarbleException;
import exception.InvalidSelectionException;
import exception.SplitOutOfRangeException;
import model.Colour;
import model.card.Card;
import model.card.Deck;
import model.player.*;
 
@SuppressWarnings("unused")
public class Game implements GameManager {
    private final Board board;
    private final ArrayList<Player> players;
	private int currentPlayerIndex;
    private final ArrayList<Card> firePit;
    private int turn;

    public Game(String playerName) throws IOException {
        turn = 0;
        currentPlayerIndex = 0;
        firePit = new ArrayList<>();

        ArrayList<Colour> colourOrder = new ArrayList<>();
        
        colourOrder.addAll(Arrays.asList(Colour.values()));
        
        Collections.shuffle(colourOrder);
        
        this.board = new Board(colourOrder, this);
        
        Deck.loadCardPool(this.board, (GameManager)this);
        
        this.players = new ArrayList<>();
        this.players.add(new Player(playerName, colourOrder.get(0)));
        
        for (int i = 1; i < 4; i++) 
            this.players.add(new CPU("CPU " + i, colourOrder.get(i), this.board));
        
        for (int i = 0; i < 4; i++) 
            this.players.get(i).setHand(Deck.drawCards());
        
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
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    
    public void selectCard(Card card) throws InvalidCardException {
        getCurrentPlayer().selectCard(card);
    }
    
    public void selectMarble(Marble marble) throws InvalidMarbleException {
        getCurrentPlayer().selectMarble(marble);
    }
    public void deselectAll() {
        getCurrentPlayer().deselectAll();
    }
    public void editSplitDistance(int splitDistance) throws SplitOutOfRangeException {
        if (splitDistance < 1 || splitDistance > 6)
            throw new SplitOutOfRangeException("Split distance must be between 1 and 6.");
        board.setSplitDistance(splitDistance);
    }
    public boolean canPlayTurn() {
        return getCurrentPlayer().getHand().size() >= turn;
    }


    public void playPlayerTurn() throws GameException {
        Player current = getCurrentPlayer();

        if (current.getSelectedCard() == null) {
            if (!current.getHand().isEmpty()) {
                Card card = current.getHand().get(0);
                try {
                    current.selectCard(card);
                } catch (InvalidCardException e) {
                    throw new SplitOutOfRangeException("Failed to auto-select card from hand.");
                }
            } else {
                throw new InvalidCardException("No cards available to play.");
            }
        }

        try {
            current.play();
        } catch (Exception e) {
            throw new CannotDiscardException("Error occurred during playPlayerTurn.");
        }
    }


  
    public void endPlayerTurn() {
    	
        Player current = getCurrentPlayer();
        Card card = current.getSelectedCard();
        current.getHand().remove(card); 
        firePit.add(card);              
        Deck.burnCard(card);            
      
            
        
        current.deselectAll();

        
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        
        if (currentPlayerIndex == 0) {
            turn++;
        }

        
        if (turn == 4) {
            turn = 0;
            for (Player p : players) {
            	if (Deck.getCardsPool().size() < 4) {
            	    Deck.refillPool(firePit);
            	    firePit.clear(); 
            	}

                p.setHand(Deck.drawCards());
            }
        }
    }

    public Colour checkWin() {
        for (Player p : players) {
            boolean won = board.getSafeZones().stream()
                .filter(zone -> zone.getColour() == p.getColour())
                .allMatch(zone -> zone.getCells().stream()
                    .allMatch(cell -> cell.getMarble() != null && cell.getMarble().getColour() == p.getColour())
                );
            if (won) return p.getColour();
        }
        return null;
    }
    @Override
    public void sendHome(Marble marble) {
        if (marble == null) {
            throw new IllegalStateException("Cannot send null marble home.");
        }

        for (Player p : players) {
            if (p.getColour() == marble.getColour()) {
                marble.setCell(null); // Clear cell link to fully detach it
                p.regainMarble(marble);
                return;
            }
        }

        throw new IllegalStateException("No player found with colour: " + marble.getColour());
    }


    public void fieldMarble() throws CannotFieldException, IllegalDestroyException {
        Player current = getCurrentPlayer();
        Marble marble = current.getOneMarble();  

        if (marble == null) {
            throw new CannotFieldException("No marble available in Home Zone.");
        }

        board.sendToBase(marble);               
        current.getMarbles().remove(marble); 
    }



    public void discardCard(Colour colour) throws CannotDiscardException {
        for (Player p : players) {
            if (p.getColour() == colour) {
                if (p.getHand().isEmpty()) {
                    throw new CannotDiscardException("No cards to discard for player of color: " + colour);
                }

                Random random = new Random();
                int randomIndex = random.nextInt(p.getHand().size());
                Card card = p.getHand().get(randomIndex);
                p.getHand().remove(randomIndex); 
                Deck.burnCard(card);
                return;
            }
        }

        throw new CannotDiscardException("No player with color " + colour + " found.");
    }

    public void discardCard() throws CannotDiscardException {
        ArrayList<Player> candidates = new ArrayList<>();

        for (Player p : players) {
            if (p != getCurrentPlayer() && !p.getHand().isEmpty()) {
                candidates.add(p);
            }
        }

        if (candidates.isEmpty()) {
            throw new CannotDiscardException("No opponent has enough cards to discard.");
        }

        Random random = new Random();
        Player chosen = candidates.get(random.nextInt(candidates.size()));

        int cardIndex = random.nextInt(chosen.getHand().size());
        Card card = chosen.getHand().remove(cardIndex);
        Deck.burnCard(card);
    }


    public Colour getActivePlayerColour() {
        return getCurrentPlayer().getColour();
    }
    public Colour getNextPlayerColour() {
        int next = (currentPlayerIndex + 1) % players.size();
        return players.get(next).getColour();
    }
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    
    
    
    

    
    

    
    


  
    
    
    
}
