package snakeris.logic.cell;

import snakeris.logic.Cell;
import snakeris.logic.FallingBlock;
import snakeris.logic.Field;
import snakeris.logic.Snake;

public class FallingBlockCellContent implements CellContent {
    public static final String NAME = "Falling";
    private final FallingBlock block;

    private boolean stopped = false;

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

    @Override
    public void eat(Field field, Snake snake, Cell thisCell) {
        snake.grow();
        block.cellEaten(thisCell);
    }

    @Override
    public boolean transformsBlock() {
        return false;
    }

    @Override
    public boolean stopsFallingBlock() {
        return stopped;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}
