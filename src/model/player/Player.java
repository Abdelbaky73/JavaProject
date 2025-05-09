package model.player;

import java.util.ArrayList;

import engine.Game;
import exception.ActionException;
import exception.CannotDiscardException;
import exception.GameException;
import exception.InvalidCardException;
import exception.InvalidMarbleException;
import model.Colour;
import model.card.Card;

public class Player {
    private final String name;
    private final Colour colour;
    private ArrayList<Card> hand;
    private final ArrayList<Marble> marbles; // This represents the Home Zone
    private Card selectedCard;
    private final ArrayList<Marble> selectedMarbles;

    public Player(String name, Colour colour) {
        this.name = name;
        this.colour = colour;
        this.hand = new ArrayList<>();
        this.selectedMarbles = new ArrayList<>();
        this.marbles = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            this.marbles.add(new Marble(colour));
        }

        this.selectedCard = null;
    }

    public String getName() {
        return name;
    }

    public Colour getColour() {
        return colour;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public ArrayList<Marble> getMarbles() {
        return marbles;
    }

    public Card getSelectedCard() {
        return selectedCard;
    }

    public void regainMarble(Marble marble) {
        marbles.add(marble);
    }

    public void sendHome(Marble marble) {
        marbles.add(marble);
    }

    public Marble getOneMarble() {
        return marbles.isEmpty() ? null : marbles.get(0);
    }

    public void selectCard(Card card) throws InvalidCardException {
        if (!hand.contains(card)) {
            throw new InvalidCardException("Card not in player's hand.");
        }
        this.selectedCard = card;
    }

    public void selectMarble(Marble marble) throws InvalidMarbleException {
        if (selectedMarbles.contains(marble)) return;

        if (selectedMarbles.size() >= 2) {
            throw new InvalidMarbleException("Cannot select more than 2 marbles.");
        }

        selectedMarbles.add(marble);
    }

    public void deselectAll() {
        selectedCard = null;
        selectedMarbles.clear();
    }

    public void play() throws GameException {
        if (selectedCard == null) {
            selectedCard = getDefaultCard();
            if (selectedCard == null) {
                throw new InvalidCardException("No card selected and no default card available.");
            }
        }

        if (!selectedCard.validateMarbleSize(selectedMarbles)) {
            throw new InvalidMarbleException("Incorrect number of marbles selected.");
        }

        if (!selectedCard.validateMarbleColours(selectedMarbles)) {
            throw new InvalidMarbleException("Selected marbles have incorrect colours.");
        }

        try {
            selectedCard.act(selectedMarbles);
        } catch (InvalidMarbleException | ActionException e) {
            throw new InvalidMarbleException("Card action failed: " + e.getMessage());
        }

        hand.remove(selectedCard);
        deselectAll();
    }

    private Card getDefaultCard() {
        return hand.isEmpty() ? null : hand.get(0);
    }

    public void fieldMarble(engine.board.BoardManager board) throws InvalidMarbleException {
        if (marbles.isEmpty()) {
            throw new InvalidMarbleException("No available marbles to field.");
        }

        Marble marble = marbles.get(0);

        try {
            board.sendToBase(marble);
            marbles.remove(0);
        } catch (Exception e) {
            throw new InvalidMarbleException("Failed to field marble: " + e.getMessage());
        }
    }
}
