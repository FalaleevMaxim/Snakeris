package snakeris.logic.cell;

import snakeris.logic.Cell;
import snakeris.logic.Field;
import snakeris.logic.Snake;

/**
 * Ячейка тела змеи
 */
public class SnakeCellContent implements CellContent {
    public static final String NAME = "Snake";

    /**
     * Ссылка на змею
     */
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

    /**
     * ри поедании хвоста у змеи вызывается соответствующий метод
     * @param field ссылка на поле
     * @param snake змея
     * @param thisCell ячейка, к которой относится это содержимое
     */
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

    /**
     * Упавший на змею статический блок уничтожает блок змеи.
     * @param cell
     * @param field ссылка на поле
     */
    @Override
    public void onStaticFall(Cell cell, Field field) {
        snake.removeBodyBlock(cell);
    }
}
