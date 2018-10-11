package snakeris.logic;

import snakeris.listener.CellUpdateListener;
import snakeris.logic.cell.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Field {
    private Cell[][] cells;
    private final int width;
    private final int height;
    private final Random foodRandom = new Random();
    private Snake snake;
    private List<FallingBlock> fallingBlocks = new LinkedList<>();

    private final List<CellUpdateListener> listeners = new ArrayList<>();

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        cells = new Cell[width][];
        for (int x = 0; x < width; x++) {
            cells[x] = new Cell[height];
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(this, x, y);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Cell getCell(int x, int y){
        if(x<0 || x>=cells.length || y<0 || y>=cells[x].length)
            return null;
        return cells[x][y];
    }

    public void randomizeFood(){
        List<Cell> emptyCells = new ArrayList<>(width*height);
        int rowLim;
        for (rowLim = 0; rowLim < height; rowLim++) rows: {
            for (int i = 0; i < width; i++) {
                if(cells[i][rowLim].getContent().getName().equals(StaticCellContent.NAME))
                    break rows;
            }
        }
        for (Cell[] col : cells) {
            for (int i = 0; i < rowLim; i++) {
                Cell cell = col[i];
                if(cell.getContent()== EmptyCellContent.instance){
                    emptyCells.add(cell);
                }
            }
        }
        emptyCells.get(foodRandom.nextInt(emptyCells.size()))
                .setContent(FoodCellContent.instance);
    }

    public void addCellListener(CellUpdateListener listener){
        listeners.add(listener);
    }

    void cellUpdated(Cell cell, CellContent old){
        for (CellUpdateListener listener : listeners) {
            listener.onCellUpdate(cell, old);
        }
    }

    public void addFallingBlock(FallingBlock block){
        fallingBlocks.add(block);
    }

    public void action(){
        if(fallingBlocks.isEmpty()) return;

        boolean nothingToTransform = false;
        while (!nothingToTransform && !fallingBlocks.isEmpty()) {
            if(!fallingBlocks.removeIf(FallingBlock::tryTransform)){
                nothingToTransform = true;
            }
        }

        if(fallingBlocks.isEmpty()) return;

        List<FallingBlock> notStopped = new ArrayList<>(fallingBlocks);
        boolean nothingToStop = false;
        while (!nothingToStop && !fallingBlocks.isEmpty()) {
            if(!notStopped.removeIf(FallingBlock::tryStop)){
                nothingToStop = true;
            }
        }

        notStopped.forEach(FallingBlock::fall);

        fallingBlocks.stream()
                .filter(block -> !notStopped.contains(block))
                .forEach(FallingBlock::clearStopped);
    }

    void removeFallingBlock(FallingBlock block){
        fallingBlocks.remove(block);
    }

    public void setSnake(Snake snake) {
        this.snake = snake;
    }
}
