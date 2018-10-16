package snakeris.ui;

import javafx.scene.Node;
import snakeris.logic.Cell;

/**
 * Функциональный интерфейс для отображения ячейки поля в Node интерфейса
 */
public interface CellNodeMapper {
    Node map(Cell cell, int cellSize);
}
