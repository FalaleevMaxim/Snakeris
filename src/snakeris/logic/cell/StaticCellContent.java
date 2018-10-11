package snakeris.logic.cell;

import snakeris.logic.Cell;
import snakeris.logic.Field;
import snakeris.logic.Snake;

public class StaticCellContent implements CellContent {
    public static final String NAME = "Static";
    public static final StaticCellContent instance = new StaticCellContent();

    private StaticCellContent() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void eat(Field field, Snake snake, Cell thisCell) {
        snake.die();
    }

    @Override
    public boolean transformsBlock() {
        return true;
    }

    @Override
    public boolean stopsFallingBlock() {
        return true;
    }
}
