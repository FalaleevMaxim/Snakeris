package snakeris.logic;

import snakeris.logic.cell.CellContent;
import snakeris.logic.cell.EmptyCellContent;
import snakeris.logic.cell.FallingBlockCellContent;
import snakeris.logic.cell.StaticCellContent;

import java.util.*;
import java.util.stream.Collectors;

public class FallingBlock {
    private final List<Cell> cells = new ArrayList<>();
    private final Field field;

    public FallingBlock(Collection<Cell> cells, Field field) {
        if(cells.isEmpty()) throw new IllegalArgumentException("Empty falling block");
        this.field = field;
        cells.forEach(cell -> cell.setContent(new FallingBlockCellContent(this)));
        this.cells.addAll(cells.stream()
                .sorted(Comparator.comparingInt(Cell::getY).reversed())
                .collect(Collectors.toList()));
        field.addFallingBlock(this);
        checkAndSeparate();
    }

    public boolean tryTransform(){
        boolean shouldTransform = false;
        for (Cell cell : cells) {
            Cell lower = field.getCell(cell.getX(), cell.getY() + 1);
            if(lower==null) {
                shouldTransform = true;
                break;
            }
            CellContent content = lower.getContent();
            if(content.transformsBlock()) {
                shouldTransform = true;
                break;
            }
        }
        if(shouldTransform){
            transform();
            return true;
        }
        return false;
    }

    public void transform(){
        for (Cell cell : cells) {
            cell.setContent(StaticCellContent.instance);
        }
    }

    public boolean tryStop() {
        boolean canStop = false;
        for (Cell cell : cells) {
            Cell lower = field.getCell(cell.getX(), cell.getY() + 1);
            CellContent content = lower.getContent();
            if(content.stopsFallingBlock()) {
                canStop = true;
                break;
            }
        }
        if(canStop){
            stop();
            return true;
        }
        return false;
    }

    public void fall(){
        cells.replaceAll(cell -> {
            FallingBlockCellContent cont = (FallingBlockCellContent) cell.getContent();
            Cell lower = field.getCell(cell.getX(), cell.getY() + 1);
            lower.setContent(new FallingBlockCellContent(this));
            if(cont.getBlock() == this) {
                cell.setContent(EmptyCellContent.instance);
            }
            return lower;
        });
    }

    private void stop() {
        cells.stream()
                .map(Cell::getContent)
                .map(content -> (FallingBlockCellContent) content)
                .forEach(content -> content.setStopped(true));
    }

    public void clearStopped(){
        for (Cell cell : cells) {
            ((FallingBlockCellContent) cell.getContent()).setStopped(false);
        }
    }

    public Collection<Cell> getCells() {
        return Collections.unmodifiableList(cells);
    }

    public void cellEaten(Cell cell) {
        removeCell(cell);
    }

    public void removeCell(Cell cell){
        cells.remove(cell);
        if(cells.isEmpty()) field.removeFallingBlock(this);
        else checkAndSeparate();
    }

    private void checkAndSeparate(){
        Set<Cell> separated = integrityCheck();
        if(separated.isEmpty()) return;
        cells.removeAll(separated);
        new FallingBlock(separated, field);
    }

    private Set<Cell> integrityCheck(){
        Set<Cell> separated = new HashSet<>(cells);
        integrityCheck(cells.get(0), separated);
        return separated;
    }

    private void integrityCheck(Cell cell, Set<Cell> rest){
        if(cell==null) return;
        if(!cells.contains(cell)) return;
        if(!rest.remove(cell)) return;
        int x = cell.getX();
        int y = cell.getY();
        integrityCheck(field.getCell(x - 1, y), rest);
        integrityCheck(field.getCell(x + 1, y), rest);
        integrityCheck(field.getCell(x, y+1), rest);
        integrityCheck(field.getCell(x, y-1), rest);
    }
}
