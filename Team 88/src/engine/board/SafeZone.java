package engine.board;

import java.util.ArrayList;
import model.Colour;

public class SafeZone {
    final Colour colour;
    private final ArrayList<Cell> cells;

    public SafeZone(Colour colour) { 
        this.colour = colour;
        cells = new ArrayList<>(4); 
        for (int i = 0; i < 4; i++) {
            cells.add(new Cell(CellType.SAFE)); 
        }
    }

    public Colour getColour() {
        return colour;
    }

    public ArrayList<Cell> getCells() {
        return cells;
    }
}
