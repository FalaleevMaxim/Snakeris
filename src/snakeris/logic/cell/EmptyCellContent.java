package snakeris.logic.cell;

import snakeris.logic.Cell;
import snakeris.logic.Field;
import snakeris.logic.Snake;

public class EmptyCellContent implements CellContent {
    public static final EmptyCellContent instance = new EmptyCellContent();
    public static final String NAME = "Empty";

    private EmptyCellContent() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void eat(Field field, Snake snake, Cell thisCell) {
    }
}
