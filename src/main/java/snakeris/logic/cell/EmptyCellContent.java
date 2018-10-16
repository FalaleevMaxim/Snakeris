package snakeris.logic.cell;

import snakeris.logic.Cell;
import snakeris.logic.Field;
import snakeris.logic.Snake;

/**
 * Пустое содержимое ячейки. Не содержит состояния, поэтому является синглтоном
 */
public class EmptyCellContent implements CellContent {
    public static final String NAME = "Empty";

    public static final EmptyCellContent instance = new EmptyCellContent();

    private EmptyCellContent() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void eat(Field field, Snake snake, Cell thisCell) {
    }

    @Override
    public boolean transformsBlock() {
        return false;
    }

    @Override
    public boolean stopsFallingBlock() {
        return false;
    }

    @Override
    public void onStaticFall(Cell cell, Field field) {

    }
}
