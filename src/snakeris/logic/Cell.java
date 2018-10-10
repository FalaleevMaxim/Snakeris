package snakeris.logic;

import snakeris.logic.cell.CellContent;
import snakeris.logic.cell.EmptyCellContent;

public class Cell {
    private final Field field;
    private final int x;
    private final int y;
    private CellContent content = EmptyCellContent.instance;

    public Cell(Field field, int xPos, int yPos) {
        this.field = field;
        this.x = xPos;
        this.y = yPos;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public CellContent getContent() {
        return content;
    }

    public void setContent(CellContent content) {
        CellContent old = this.content;
        this.content = content;
        field.cellUpdated(this, old);
    }
}
