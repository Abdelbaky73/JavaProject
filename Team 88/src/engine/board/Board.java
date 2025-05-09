package engine.board;
import engine.board.Cell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Colour;
import engine.GameManager;

public class Board implements BoardManager {
    private final GameManager gameManager;
    private final ArrayList<Cell> track;
    private final ArrayList<SafeZone> safeZones;
    private int splitDistance;

    public Board(ArrayList<Colour> colourOrder, GameManager gameManager) {
        this.gameManager = gameManager;
        this.track  = new ArrayList<>(100);
        this.safeZones = new ArrayList<>(4);
        this.splitDistance = 3;
        setupTrack();
        assignTrapCell(); 
        setupSafeZones(colourOrder);
    }
    private void setupSafeZones(ArrayList<Colour> colourOrder) {
        for (int i = 0; i < 4; i++) {
            safeZones.add(new SafeZone(colourOrder.get(i))); 
        }
    }
    private void assignTrapCell() {
        List<Integer> normalCellIndices = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            if (track.get(i).getCellType() == CellType.NORMAL) {
                normalCellIndices.add(i);
            }
        }
        Collections.shuffle(normalCellIndices);
        int trapsToPlace = Math.min(8, normalCellIndices.size()); 
        
        for (int i = 0; i < trapsToPlace; i++) {
            int trapIndex = normalCellIndices.get(i);
            track.get(trapIndex).setTrap(true);
        }
    }
    private void setupTrack() {
        for (int i = 0; i < 100; i++) {
            if (i == 0 || i == 25 || i == 50 || i == 75) {
                track.add(new Cell(CellType.BASE));  
            } else if (i == 23 || i == 48 || i == 73 || i == 98) {
                track.add(new Cell(CellType.ENTRY));  
            } else {
                track.add(new Cell(CellType.NORMAL));
            }
        }
    }

    
    public int getSplitDistance() {
        return splitDistance;
    }

    public void setSplitDistance(int splitDistance) {
        this.splitDistance = splitDistance;
    }

    public ArrayList<Cell> getTrack() {
        return track;
    }

    public ArrayList<SafeZone> getSafeZones() {
        return safeZones;
    }

    public static List<String> getAllColors() {
        List<String> colors = new ArrayList<>();
        for (Colour colour : Colour.values()) {
            colors.add(colour.name());
        }
        return colors;
    }
}
