package snakeris.logic.cell;

import snakeris.logic.Cell;
import snakeris.logic.Field;
import snakeris.logic.Snake;

public class FoodCellContent implements CellContent {
    public static final FoodCellContent instance = new FoodCellContent();
    public static final String NAME = "Food";

    private FoodCellContent() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void eat(Field field, Snake snake, Cell thisCell) {
        snake.grow();
        field.onFoodEaten();
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
        field.randomizeFood();
    }
}