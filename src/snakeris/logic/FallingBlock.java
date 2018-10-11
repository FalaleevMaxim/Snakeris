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
        this.field = field;
        cells.forEach(cell -> cell.setContent(new FallingBlockCellContent(this)));
        this.cells.addAll(cells.stream()
                .sorted(Comparator.comparingInt(Cell::getY).reversed())
                .collect(Collectors.toList()));
        field.addFallingBlock(this);
        System.out.println("Added falling block to field");
    }

    public boolean tryTransform(){
        System.out.println("Attempt to transform block");
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
            System.out.println("Transforming block");
            transform();
            return true;
        }
        System.out.println("Did not transform block");
        return false;
    }

    private void transform(){
        for (Cell cell : cells) {
            cell.setContent(StaticCellContent.instance);
        }
    }

    public boolean tryStop() {
        System.out.println("Attempt to stop block");
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
        cells.remove(cell);
        if(cells.isEmpty()) field.removeFallingBlock(this);
    }
}
