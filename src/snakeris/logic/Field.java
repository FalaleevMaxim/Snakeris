package snakeris.logic;

import snakeris.listener.CellUpdateListener;
import snakeris.logic.cell.CellContent;

import java.util.ArrayList;
import java.util.List;

public class Field {
    private Cell[][] cells;
    private final int width;
    private final int height;
    private int foodX, foodY;

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
        return cells[x][y];
    }

    public void randomizeFood(){

    }

    public void addCellListener(CellUpdateListener listener){
        listeners.add(listener);
    }

    void cellUpdated(Cell cell, CellContent old){
        for (CellUpdateListener listener : listeners) {
            listener.onCellUpdate(cell, old);
        }
    }
}
