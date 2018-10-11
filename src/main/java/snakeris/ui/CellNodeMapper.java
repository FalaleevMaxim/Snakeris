package snakeris.ui;

import javafx.scene.Node;
import snakeris.logic.Cell;

public interface CellNodeMapper {
    Node map(Cell cell, int cellSize);
}
