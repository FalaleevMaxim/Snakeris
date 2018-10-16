package snakeris.logic.cell;

import snakeris.logic.Cell;
import snakeris.logic.Field;
import snakeris.logic.Snake;

/**
 * Содержимое ячейки
 */
public interface CellContent {
    /**
     * Имя типа содержимого
     */
    String getName();

    /**
     * Действие, когда змея ест клетку
     * @param field ссылка на поле
     * @param snake змея
     * @param thisCell ячейка, к которой относится это содержимое
     */
    void eat(Field field, Snake snake, Cell thisCell);

    /**
     * Заставляет ли эта клетка падающий блок трансформироваться в статический?
     * Пока этим свойством обладают только статические блоки
     * @see StaticCellContent
     */
    boolean transformsBlock();

    /**
     * Останавливает ли эта клетка падающие блоки?
     * Этим свойством обладают блоки змеи и падающие блоки, уже остановленные змеёй
     */
    boolean stopsFallingBlock();

    /**
     * Что произойдёт, если на эту клетку упадёт статический блок при удалении ряда?
     * @param thisCell ячейка, к которой относится это содержимое
     * @param field ссылка на поле
     */
    void onStaticFall(Cell thisCell, Field field);
}
