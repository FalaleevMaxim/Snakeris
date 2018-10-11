package snakeris.listener;

import snakeris.logic.Cell;
import snakeris.logic.cell.CellContent;

public interface CellUpdateListener {
    void onCellUpdate(Cell cell, CellContent old);
}
