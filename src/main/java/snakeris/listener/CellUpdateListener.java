package snakeris.listener;

import snakeris.logic.Cell;
import snakeris.logic.cell.CellContent;

/**
 * Слушатель, отрабатывающий при обновлении содержимого ячейки
 */
public interface CellUpdateListener {
    /**
     * @param cell обновлённая ячейка
     * @param old старое значение содержимого ячейки
     */
    void onCellUpdate(Cell cell, CellContent old);
}
