package snakeris.logic.cell;

import snakeris.logic.Cell;
import snakeris.logic.Field;
import snakeris.logic.Snake;

/**
 * Статический блок, образуемый при приземлении падающего блока
 */
public class StaticCellContent implements CellContent {
    public static final String NAME = "Static";
    public static final StaticCellContent instance = new StaticCellContent();

    private StaticCellContent() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Змея умирает при попытке съесть статический блок
     * @param field ссылка на поле
     * @param snake змея
     * @param thisCell ячейка, к которой относится это содержимое
     */
    @Override
    public void eat(Field field, Snake snake, Cell thisCell) {
        snake.die();
    }

    /**
     * Блок при падении на статический, сам трансформируется в статический
     */
    @Override
    public boolean transformsBlock() {
        return true;
    }

    /**
     * Вызов этого метода невозможен, т.к. перед ним вызывается {@link #transformsBlock()}, и падающий блок становится статическим
     */
    @Override
    public boolean stopsFallingBlock() {
        return true;
    }

    /**
     * Вызов этого метода невозможен т.к. при удалении ряда падают сначала нижние ряды, потом верхние.
     * @param cell
     * @param field ссылка на поле
     */
    @Override
    public void onStaticFall(Cell cell, Field field) {

    }
}
