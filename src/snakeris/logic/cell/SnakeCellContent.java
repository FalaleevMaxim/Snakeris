package snakeris.logic.cell;

import snakeris.logic.Cell;
import snakeris.logic.Field;
import snakeris.logic.Snake;

public class SnakeCellContent implements CellContent {
    public static final String NAME = "Snake";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void eat(Field field, Snake snake, Cell thisCell) {
        System.out.println("Snake eating tail");
        snake.eatenTail(thisCell);
    }

    @Override
    public boolean transformsBlock() {
        return false;
    }

    @Override
    public boolean stopsFallingBlock() {
        return true;
    }
}
