package snakeris.logic;

import snakeris.listener.CellUpdateListener;
import snakeris.logic.cell.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Field {
    private Cell[][] cells;
    private final int width;
    private final int height;
    private final Random foodRandom = new Random();
    private Snake snake;
    private List<FallingBlock> fallingBlocks = new LinkedList<>();

    private final List<CellUpdateListener> cellListeners = new ArrayList<>();
    private final List<Runnable> eatListeners = new ArrayList<>();
    private final List<Consumer<Integer>> rowRemoveListeners = new ArrayList<>();

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
        rowRemoveListeners.add(n -> {
            for (int i = 0; i < n; i++) {
                randomizeFood();
            }
        });
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
        if(emptyCells.isEmpty()) return;
        emptyCells.get(foodRandom.nextInt(emptyCells.size()))
                .setContent(FoodCellContent.instance);
    }

    public void addCellListener(CellUpdateListener listener){
        cellListeners.add(listener);
    }

    public void addEatListener(Runnable listener){
        eatListeners.add(listener);
    }

    public void addRowRemoveListener(Consumer<Integer> listener){
        rowRemoveListeners.add(listener);
    }

    void cellUpdated(Cell cell, CellContent old){
        for (CellUpdateListener listener : cellListeners) {
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
            if(fallingBlocks.removeIf(FallingBlock::tryTransform)){
                removeStaticRows();
            }else {
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

    private void removeStaticRows(){
        int removed = 0;
        for (int y = height-1; y > 0 ;) {
            boolean filled = true;
            for (int x = 0; x < width; x++) {
                if(!cells[x][y].getContent().getName().equals(StaticCellContent.NAME)) {
                    filled = false;
                    break;
                }
            }
            if(filled){
                removeStaticRow(y);
                removed++;
            }else {
                y--;
            }
        }
        int res = removed;
        rowRemoveListeners.forEach(listener -> listener.accept(res));
    }

    private void removeStaticRow(int row) {
        for (int y = row; y>0; y--) {
            for (int x = 0; x < width; x++) {
                CellContent upperCellContent = cells[x][y - 1].getContent();
                if(upperCellContent instanceof StaticCellContent) {
                    cells[x][y].getContent().onStaticFall(cells[x][y], this);
                    cells[x][y-1].setContent(EmptyCellContent.instance);
                    cells[x][y].setContent(StaticCellContent.instance);
                }else if(cells[x][y].getContent() instanceof StaticCellContent){
                    cells[x][y].setContent(EmptyCellContent.instance);
                }
            }
        }
    }

    public void setSnake(Snake snake) {
        this.snake = snake;
    }

    public void onFoodEaten() {
        randomizeFood();
        eatListeners.forEach(Runnable::run);
    }
}
