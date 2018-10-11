package snakeris.logic.cell;

import snakeris.logic.Cell;
import snakeris.logic.Field;
import snakeris.logic.Snake;

public class SnakeCellContent implements CellContent {
    public static final String NAME = "Snake";

    private final Snake snake;

    public SnakeCellContent(Snake snake) {
        this.snake = snake;
    }

    public Snake getSnake() {
        return snake;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void eat(Field field, Snake snake, Cell thisCell) {
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

    @Override
    public void onStaticFall(Cell cell, Field field) {
        snake.removeBodyBlock(cell);
    }
}
