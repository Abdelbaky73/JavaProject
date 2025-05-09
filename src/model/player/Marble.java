package model.player;

import engine.board.Cell;
import engine.board.CellType;
import model.Colour;

public class Marble {
    private final Colour colour;
    private Cell cell;

    public Marble(Colour colour) {
        this.colour = colour;
        this.cell = null;
    }

    public Colour getColour() {
        return this.colour;
    }

    public Cell getCell() {
        return this.cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public boolean isInBase() {
    	return (cell != null) && (cell.getCellType() == CellType.BASE);

    }
}
