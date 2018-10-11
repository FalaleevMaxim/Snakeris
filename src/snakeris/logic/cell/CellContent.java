package snakeris.logic.cell;

import snakeris.logic.FallingBlock;
import snakeris.logic.Snake;
import snakeris.logic.Cell;
import snakeris.logic.Field;

public interface CellContent {
    String getName();
    void eat(Field field, Snake snake, Cell thisCell);
    boolean transformsBlock();
    boolean stopsFallingBlock();
}
