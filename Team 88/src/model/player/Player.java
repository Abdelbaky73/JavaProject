package model.player;

import java.util.ArrayList;
import java.util.List;

import model.Colour;
import model.card.Card;

public class Player {
	private final String name;
	final Colour colour;
	private ArrayList<Card> hand;
	private ArrayList<Marble> marbles;
	private Card selectedCard;
	private final ArrayList<Marble> selectedMarbles;
	
	public Player(String name, Colour colour){
		this.name = name;
		this.colour = colour;
		hand = new ArrayList<Card>();
		selectedMarbles = new ArrayList<Marble>();
		marbles = new ArrayList<Marble>(); 
		for (int i = 0; i < 4; i++) {
            this.marbles.add(new Marble(colour));
        }
		selectedCard = null;
		
		 
}
	public void addCardsToHand(List<Card> drawnCards) {
        this.hand.addAll(drawnCards);
    }

	public ArrayList<Card> getHand() {
		return hand;
	}

	public void setHand(ArrayList<Card> hand) {
		this.hand = hand;
	}


	public String getName() {
		return name;
	}

	public Colour getColour() {
		return colour;
	}

	public Card getSelectedCard() {
		return selectedCard;
	}
	public ArrayList<Marble> getMarbles() {
		return marbles;
	}
	
	
	
	
}
