package snakeris.logic.cell;

import snakeris.logic.Cell;
import snakeris.logic.FallingBlock;
import snakeris.logic.Field;
import snakeris.logic.Snake;

/**
 * Клетка падающего блока
 */
public class FallingBlockCellContent implements CellContent {
    public static final String NAME = "Falling";
    private final FallingBlock block;

    /**
     * Показывает, приостановлено ли падение блока.
     */
    private boolean stopped = false;

    /**
     * @param block Падающий блок, к которому относится клетка
     */
    public FallingBlockCellContent(FallingBlock block) {
        this.block = block;
    }

    public FallingBlock getBlock(){
        return block;
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Змея может есть падающий блок. У блока удаляется ячейка, змея растёт на 1.
     * Если удаление ячейки нарушает целостность блока, блок может разделиться на несколько
     * @param field ссылка на поле
     * @param snake змея
     * @param thisCell ячейка, к которой относится это содержимое
     */
    @Override
    public void eat(Field field, Snake snake, Cell thisCell) {
        snake.grow();
        block.cellEaten(thisCell);
    }

    @Override
    public boolean transformsBlock() {
        return false;
    }

    /**
     * Если блок уже остановлен, он останавливает падающие на него блоки
     */
    @Override
    public boolean stopsFallingBlock() {
        return stopped;
    }

    /**
     * Если при удалении рядя в тетрисе статический блок упадёт на падающий блок, падающий блок трансформируется в статический
     * @param cell
     * @param field ссылка на поле
     */
    @Override
    public void onStaticFall(Cell cell, Field field) {
        block.transform();
        field.removeFallingBlock(block);
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}
