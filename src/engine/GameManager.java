package engine;

import model.Colour;
import model.card.Card;
import model.player.Marble;
import model.player.Player;
import engine.board.Board;
import exception.CannotDiscardException;
import exception.CannotFieldException;
import exception.IllegalDestroyException;
import exception.InvalidCardException;

public interface GameManager {
	public void sendHome(Marble marble);
	
	public void fieldMarble() throws CannotFieldException, IllegalDestroyException;
	
	public void discardCard(Colour colour) throws CannotDiscardException;
	
	public void discardCard() throws CannotDiscardException;
	
	public Colour getActivePlayerColour();
	
	public Colour getNextPlayerColour();
	
	

}


   

