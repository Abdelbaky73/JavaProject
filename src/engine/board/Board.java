package engine.board;

import java.util.ArrayList;

import engine.GameManager;
import exception.CannotFieldException;
import exception.IllegalDestroyException;
import exception.IllegalMovementException;
import exception.IllegalSwapException;
import exception.InvalidMarbleException;
import model.Colour;
import model.player.Marble;

@SuppressWarnings("unused")
public class Board implements BoardManager {
    private final ArrayList<Cell> track;
    private final ArrayList<SafeZone> safeZones;
	private final GameManager gameManager;
    private int splitDistance;
    private final ArrayList<Colour> colourOrder;
    private int basePosition;

    public Board(ArrayList<Colour> colourOrder, GameManager gameManager) {
        this.track = new ArrayList<>();
        this.safeZones = new ArrayList<>();
        this.gameManager = gameManager;
        this.colourOrder = colourOrder;
        this.basePosition = 0;
        System.out.println("Initialized Entry cells at positions:");
        for (int i = 0; i < track.size(); i++) {
            if (track.get(i).getCellType() == CellType.ENTRY) {
                System.out.println(i + " - " + getBaseOwnerColour(track.get(i)));
            }
        }
        
        for (int i = 0; i < 100; i++) {
            this.track.add(new Cell(CellType.NORMAL));
            
            if (i % 25 == 0) 
                this.track.get(i).setCellType(CellType.BASE);
            
            else if ((i+2) % 25 == 0) 
                this.track.get(i).setCellType(CellType.ENTRY);
        }

        for(int i = 0; i < 8; i++)
            this.assignTrapCell();

        for (int i = 0; i < 4; i++)
            this.safeZones.add(new SafeZone(colourOrder.get(i)));


        splitDistance = 3;
    }

    public ArrayList<Cell> getTrack() {
        return this.track;
    }

    public ArrayList<SafeZone> getSafeZones() {
        return this.safeZones;
    }
    
    @Override
    public int getSplitDistance() {
        return this.splitDistance;
    }

    public void setSplitDistance(int splitDistance) {
        this.splitDistance = splitDistance;
    }
   

   
    private void assignTrapCell() {
        int randIndex = -1;
        
        do
            randIndex = (int)(Math.random() * 100); 
        while(this.track.get(randIndex).getCellType() != CellType.NORMAL || this.track.get(randIndex).isTrap());
        
        this.track.get(randIndex).setTrap(true);
    }
    
    private ArrayList<Cell> getSafeZone(Colour colour) {
        for (SafeZone zone : safeZones) {
            if (zone.getColour() == colour) {
                return zone.getCells();
            }
        }
        return null; 
    }
    private int getPositionInPath(ArrayList<Cell> path, Marble marble) {
        for (int i = 0; i < path.size(); i++) {
            if (path.get(i).getMarble() == marble) {
                return i;
            }
        }
        return -1; 
    }
    private int getBasePosition(Colour colour) {
        if (colour == null || !colourOrder.contains(colour)) {
            return -1; 
        }
        return colourOrder.indexOf(colour) * 25; 
    }
    private int getEntryPosition(Colour colour) {
        if (colour == Colour.RED) return 98;
        if (colour == Colour.BLUE) return 23;
        if (colour == Colour.YELLOW) return 48;
        if (colour == Colour.GREEN) return 73;
        return -1; 
    }

    private ArrayList<Cell> validateSteps(Marble marble, int steps) throws IllegalMovementException {
        ArrayList<Cell> fullPath = new ArrayList<>();
        Colour marbleColour = marble.getColour();
        ArrayList<Cell> playerSafeZone = getSafeZone(marbleColour);

        
        boolean isOpponentMarble = !marbleColour.equals(gameManager.getActivePlayerColour());
        
        int trackIndex = getPositionInPath(track, marble);

        if (trackIndex != -1) { 
            fullPath.add(track.get(trackIndex));

            int entryIndex = getEntryPosition(marbleColour);
            int stepsToEntry = (entryIndex - trackIndex + 100) % 100;

            if (steps == 0) {
                throw new IllegalMovementException("Cannot move 0 steps.");
            }

            if (steps > 0) {
                if (steps <= stepsToEntry || isOpponentMarble) {
                    for (int i = 1; i <= steps; i++) {
                        int currentIndex = (trackIndex + i) % 100;
                        fullPath.add(track.get(currentIndex));
                    }
                } else {
                    
                    for (int i = 1; i <= stepsToEntry; i++) {
                        int currentIndex = (trackIndex + i) % 100;
                        fullPath.add(track.get(currentIndex));
                    }

                    int remainingSteps = steps - stepsToEntry;

                    if (remainingSteps > playerSafeZone.size()) {
                        throw new IllegalMovementException("Rank of card is too high to fit inside SafeZone.");
                    }

                    
                    for (int i = 0; i < remainingSteps; i++) {
                        fullPath.add(playerSafeZone.get(i));
                    }
                }
            } else { 
                for (int i = 1; i <= Math.abs(steps); i++) {
                    int currentIndex = (trackIndex - i + 100) % 100;
                    fullPath.add(track.get(currentIndex));
                }
            }
        } else {
            int safeIndex = getPositionInPath(playerSafeZone, marble);
            if (safeIndex == -1) {
                throw new IllegalMovementException("Marble cannot be moved. Not on track or SafeZone.");
            }

            fullPath.add(playerSafeZone.get(safeIndex));

            
            if (steps < 0) {
                throw new IllegalMovementException("Cannot move backwards in SafeZone.");
            }

            int targetIndex = safeIndex + steps;
            if (targetIndex >= playerSafeZone.size()) {
                throw new IllegalMovementException("Rank of card is too high inside SafeZone.");
            }

            for (int i = safeIndex + 1; i <= targetIndex; i++) {
                fullPath.add(playerSafeZone.get(i));
            }
        }

        return fullPath;
    }

    private void validatePath(Marble marble, ArrayList<Cell> fullPath, boolean destroy)
            throws IllegalMovementException {

        if (marble == null || fullPath == null || fullPath.isEmpty()) {
            throw new IllegalMovementException("Invalid marble or path provided");
        }

        Colour activePlayer = gameManager.getActivePlayerColour();
        int blockingCount = 0;

        for (int i = 1; i < fullPath.size(); i++) {
            Cell cell = fullPath.get(i);
            Marble occupying = cell.getMarble();
            boolean isTarget = (i == fullPath.size() - 1);
            CellType type = cell.getCellType();

            
            if (type == CellType.ENTRY && occupying != null) {
                boolean nextIsSafe = (i + 1 < fullPath.size()) && fullPath.get(i + 1).getCellType() == CellType.SAFE;

                if (!isTarget && nextIsSafe) {
                    throw new IllegalMovementException("Cannot bypass occupied Entry when entering Safe Zone.");
                }

                if (isTarget && destroy && occupying.getColour() == marble.getColour()) {
                    throw new IllegalMovementException("Cannot destroy your own marble on Entry cell");
                }
            }



            
            if (type == CellType.SAFE && occupying != null) {
                throw new IllegalMovementException("Cannot bypass or land on Safe Zone marbles");
            }

            
            if (type == CellType.BASE) {
                Colour baseOwner = getBaseOwnerColour(cell);

                if (occupying != null && occupying.getColour() == baseOwner) {
                    throw new IllegalMovementException("Cannot enter or bypass base cell occupied by its owner.");
                }

                if (!isTarget && occupying != null) {
                    throw new IllegalMovementException("Cannot pass through an occupied base cell.");
                }
            }

           
            if (occupying != null) {
                if (!destroy && occupying.getColour() == activePlayer) {
                    throw new IllegalMovementException("Cannot bypass or land on active player's marble");
                }

                if (!destroy && i < fullPath.size() - 1) {  
                    blockingCount++;
                    if (blockingCount > 1) {
                        throw new IllegalMovementException("Path is blocked by multiple marbles");
                    }
                }

            }

        }

        
        Cell targetCell = fullPath.get(fullPath.size() - 1);
        Marble targetMarble = targetCell.getMarble();

        if (!destroy && targetMarble != null) {
            Colour targetColour = targetMarble.getColour();
            if (targetColour == activePlayer &&
                (targetCell.getCellType() != CellType.BASE || getBaseOwnerColour(targetCell) == activePlayer)) {
                throw new IllegalMovementException("Cannot land on your own marble");
            }
        }
    }
    
    private void move(Marble marble, ArrayList<Cell> fullPath, boolean destroy)
            throws IllegalDestroyException {

        if (fullPath == null || fullPath.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        Cell startCell = fullPath.get(0);
        Cell targetCell = fullPath.get(fullPath.size() - 1);

        
        if (!destroy && targetCell.getMarble() != null && !targetCell.isTrap()) {
            throw new IllegalDestroyException("Cannot move into an occupied cell without destruction.");
        }

        
        startCell.setMarble(null);
        marble.setCell(null);

        
        if (destroy) {
            for (int i = 1; i < fullPath.size(); i++) {
                Cell cell = fullPath.get(i);
                Marble target = cell.getMarble();
                if (target != null) {
                    validateDestroy(cell);
                }
            }
            
            
            for (int i = 1; i < fullPath.size(); i++) {
                Cell cell = fullPath.get(i);
                Marble target = cell.getMarble();
                if (target != null) {
                    cell.setMarble(null);
                    target.setCell(null);
                    gameManager.sendHome(target);
                }
            }
        }

        
        if (targetCell.isTrap()) {
            targetCell.setMarble(null);  
            targetCell.setTrap(false);
            assignTrapCell();
            gameManager.sendHome(marble);
        } else {
            targetCell.setMarble(marble);
            marble.setCell(targetCell);
        }
    }




    private void validateDestroy(Cell cell) throws IllegalDestroyException {
        if (cell == null) {
            throw new IllegalDestroyException("Cell is null.");
        }
        

        Marble marble = cell.getMarble();
        if (marble == null) {
            throw new IllegalDestroyException("No marble to destroy.");
        }
        if (marble.getColour() == gameManager.getActivePlayerColour()) {
            throw new IllegalDestroyException("Cannot destroy your own marble.");
        }


       
        if (marble.getColour() == gameManager.getActivePlayerColour()) {
            throw new IllegalDestroyException("Cannot destroy your own marble.");
        }

        if (cell.getCellType() == CellType.SAFE) {
            throw new IllegalDestroyException("Cannot destroy marble in SAFE zone.");
        }

        if (cell.getCellType() == CellType.BASE && getBaseOwnerColour(cell) == marble.getColour()) {
            throw new IllegalDestroyException("Cannot destroy marble in its own BASE cell.");
        }

        if (cell.getCellType() == CellType.ENTRY) {
            throw new IllegalDestroyException("Cannot destroy marble in ENTRY cell.");
        }
    }


    
    private void validateSwap(Marble marble1, Marble marble2) throws IllegalSwapException {
        int index1 = getPositionInPath(track, marble1);
        int index2 = getPositionInPath(track, marble2);

        if (index1 == -1 || index2 == -1) {
            throw new IllegalSwapException("Both marbles must be on the track to swap.");
        }

        if (marble1.getColour() == marble2.getColour()) {
            throw new IllegalSwapException("Cannot swap two marbles of the same player.");
        }

        Cell cell1 = track.get(index1);
        Cell cell2 = track.get(index2);

        
        Colour activeColour = gameManager.getActivePlayerColour();
        boolean marble1IsActive = marble1.getColour() == activeColour;
        boolean marble2IsActive = marble2.getColour() == activeColour;

        
        if (!marble1IsActive && cell1.getCellType() == CellType.BASE && 
            cell1.getMarble() != null && cell1.getMarble().getColour() == marble1.getColour()) {
            throw new IllegalSwapException("Cannot swap with opponent's marble when it is safe in its own Base Cell.");
        }

        if (!marble2IsActive && cell2.getCellType() == CellType.BASE && 
            cell2.getMarble() != null && cell2.getMarble().getColour() == marble2.getColour()) {
            throw new IllegalSwapException("Cannot swap with opponent's marble when it is safe in its own Base Cell.");
        }
    }

    private void validateDestroy(int positionInPath) throws IllegalDestroyException {
        if (positionInPath < 0 || positionInPath >= track.size()) {
            throw new IllegalDestroyException("Position out of bounds.");
        }

        Cell cell = track.get(positionInPath);
        Marble marble = cell.getMarble();

        
        if (marble == null) {
            return;
        }

        if (cell.getCellType() == CellType.SAFE) {
            throw new IllegalDestroyException("Cannot destroy a marble in a SAFE cell.");
        }

        if (cell.getCellType() == CellType.BASE && getBaseOwnerColour(cell) == marble.getColour()) {
            throw new IllegalDestroyException("Cannot destroy a marble in its own BASE cell.");
        }
    }
    private void validateFielding(Cell baseCell) throws CannotFieldException {
        
        Marble occupying = baseCell.getMarble();

        
        Colour currentPlayerColour = gameManager.getActivePlayerColour();

        
        if (occupying != null && occupying.getColour() == currentPlayerColour) {
            throw new CannotFieldException("Base Cell already contains your own marble.");
        }
    }

    private void validateSaving(int positionInSafeZone, int positionOnTrack) throws InvalidMarbleException {
        if (positionInSafeZone != -1) {
            throw new InvalidMarbleException("Marble is already in Safe Zone");
        }  
        if (positionOnTrack == -1) {
            throw new InvalidMarbleException("Marble is not on track");
        }
    }
    @Override
    public void moveBy(Marble marble, int steps, boolean destroy)
    
            throws IllegalMovementException, IllegalDestroyException {
        ArrayList<Cell> fullPath = validateSteps(marble, steps);
        validatePath(marble, fullPath, destroy);
        move(marble, fullPath, destroy);
    }
    @Override
    public void swap(Marble marble1, Marble marble2)
            throws IllegalSwapException {
        validateSwap(marble1, marble2);
        int index1 = getPositionInPath(track, marble1);
        int index2 = getPositionInPath(track, marble2);
        
        Cell cell1 = track.get(index1);
        Cell cell2 = track.get(index2);
        
        cell1.setMarble(marble2);
        cell2.setMarble(marble1);
    }


    @Override
    public void sendToBase(Marble marble) throws CannotFieldException, IllegalDestroyException {
        int baseIndex = getBasePosition(marble.getColour());
        Cell baseCell = track.get(baseIndex);

        Marble occupying = baseCell.getMarble();

        if (occupying != null) {
            if (occupying.getColour() == marble.getColour()) {
                throw new CannotFieldException("Base Cell already contains your own marble.");
            } else {
                gameManager.sendHome(occupying); 
            }
        }

        baseCell.setMarble(marble); 
    }





    @Override
    public void sendToSafe(Marble marble)
            throws InvalidMarbleException {
        ArrayList<Cell> path = this.track;
        ArrayList<Cell> safeZone = getSafeZone(marble.getColour());

        int posSafe = getPositionInPath(safeZone, marble);
        int posTrack = getPositionInPath(path, marble);
        validateSaving(posSafe, posTrack);

        for (Cell c : safeZone) {
            if (c.getMarble() == null) {
                c.setMarble(marble);
                track.get(posTrack).setMarble(null);
                return;
            }
        }
    }
    @Override
    public ArrayList<Marble> getActionableMarbles() {
        Colour current = gameManager.getActivePlayerColour();
        ArrayList<Marble> marbles = new ArrayList<>();

        for (Cell c : track) {
            Marble m = c.getMarble();
            if (m != null && m.getColour() == current) {
                marbles.add(m);
            }
        }

        for (Cell c : getSafeZone(current)) {
            Marble m = c.getMarble();
            if (m != null) {
                marbles.add(m);
            }
        }

        return marbles;
    }

    public boolean isBaseOccupiedByDifferentPlayer(Colour currentPlayerColour) {
        Cell baseCell = track.get(basePosition); 
        if (baseCell.getMarble() != null) {
            Colour marbleColour = baseCell.getMarble().getColour();
            return !marbleColour.equals(currentPlayerColour);  
        }
        return false; 
    }
    
    private Colour getBaseOwnerColour(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.BASE) {
            System.out.println("Not a base cell");
            return null;
        }
        
        int index = track.indexOf(cell);
        System.out.println("Base cell index: " + index);
        
        if (index == 0) return Colour.RED;
        if (index == 25) return Colour.BLUE;
        if (index == 50) return Colour.YELLOW;
        if (index == 75) return Colour.GREEN;
        
        System.out.println("Warning: Found BASE cell at unexpected index: " + index);
        return null;
    }    

    @Override
    public void destroyMarble(Marble marble) throws IllegalDestroyException {
        System.out.println("\n=== DESTRUCTION ATTEMPT ===");
        
        if (marble == null) {
            throw new IllegalDestroyException("Cannot destroy null marble");
        }
        Cell cell = findMarbleCell(marble);
        
        CellType cellType = cell.getCellType();
        int position = track.indexOf(cell);
        
        System.out.println("Marble Color: " + marble.getColour());
        System.out.println("Cell Type: " + cellType);
        System.out.println("Track Position: " + position);
        
        if (cellType != CellType.NORMAL) {
            System.out.println("PROTECTED CELL DETECTED");
            if (cellType == CellType.BASE) {
                System.out.println("Base Owner: " + getBaseOwnerColour(cell));
            }
        }

        if (cellType == CellType.SAFE) {
            throw new IllegalDestroyException("Cannot destroy in Safe Zone");
        }
        if (cellType == CellType.BASE) {
            throw new IllegalDestroyException("Cannot destroy in Base Cell"); 
        }
        if (cellType == CellType.ENTRY) {
            throw new IllegalDestroyException("Cannot destroy in Entry cell");
        }

        System.out.println("PROCEEDING with destruction");
        cell.setMarble(null);
        marble.setCell(null);
        gameManager.sendHome(marble);
    }
    private Cell findMarbleCell(Marble marble) {
        System.out.println("Searching for marble in track...");
        for (Cell cell : track) {
            if (marble.equals(cell.getMarble())) {
                System.out.println("Found in track at index " + track.indexOf(cell));
                return cell;
            }
        }
        
        System.out.println("Searching safe zones...");
        for (SafeZone zone : safeZones) {
            for (Cell cell : zone.getCells()) {
                if (marble.equals(cell.getMarble())) {
                    System.out.println("Found in " + zone.getColour() + " safe zone");
                    return cell;
                }
            }
        }
        
        System.out.println("Marble not found anywhere!");
        return null;
    }
    
}
