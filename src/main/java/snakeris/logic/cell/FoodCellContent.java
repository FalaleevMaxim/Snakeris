package snakeris.logic.cell;

import snakeris.logic.Cell;
import snakeris.logic.Field;
import snakeris.logic.Snake;

/**
 * Клетка с едой
 */
public class FoodCellContent implements CellContent {
    public static final String NAME = "Food";

    /**
     * Питательность клетки. Показывает, на сколько блоков вырастет змея, съев его.
     * Может быть положительной (змея растёт) или отрицательной (змея укорачивается), но не может быть 0 (это не имеет смысла)
     */
    public final int nutrition;

    public FoodCellContent() {
        this(1);
    }

    public FoodCellContent(int nutrition) {
        if(nutrition==0) throw new IllegalArgumentException("Nutrition can not be 0");
        this.nutrition = nutrition;
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Когда змея ест еду, она вырастает на {@link #nutrition}, и на поле генерируется новая еда с тем же значением.
     * @param field ссылка на поле
     * @param snake змея
     * @param thisCell ячейка, к которой относится это содержимое
     */
    @Override
    public void eat(Field field, Snake snake, Cell thisCell) {
        snake.grow(nutrition);
        field.onFoodEaten(nutrition);
    }

    @Override
    public boolean transformsBlock() {
        return false;
    }

    @Override
    public boolean stopsFallingBlock() {
        return false;
    }

    /**
     * Если блок упадёт на еду, генерируется новая еда.
     * @param cell
     * @param field ссылка на поле
     */
    @Override
    public void onStaticFall(Cell cell, Field field) {
        field.randomizeFood(nutrition);
    }
}
