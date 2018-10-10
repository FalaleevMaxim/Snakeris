package snakeris.logic;

import snakeris.Direction;
import snakeris.logic.cell.EmptyCellContent;
import snakeris.logic.cell.SnakeCellContent;

import java.util.LinkedList;

public class Snake {
    private final Field field;
    private LinkedList<Cell> bodyParts = new LinkedList<>();
    volatile private Direction dir = Direction.RIGHT;
    private int headX, headY;

    public Snake(int initLengh, Field field) {
        if(field==null) throw new NullPointerException("Field is null");
        if(initLengh<=0) {
            throw new IllegalArgumentException("Negative or null length of snake!");
        }
        this.field = field;
        for (int i = initLengh-1; i >=0 ; i--) {
            Cell cell = field.getCell(i, 0);
            cell.setContent(new SnakeCellContent());
            bodyParts.add(cell);
        }
        headX = initLengh-1;
        headY = 0;
    }

    public void move(){
        bodyParts.getLast().setContent(EmptyCellContent.instance);
        bodyParts.removeLast();

        int width = field.getWidth();
        int height = field.getHeight();

        switch (dir) {
            case LEFT:
                if(headX ==0) {
                    headX = width -1;
                }
                else headX--;
                break;
            case RIGHT:
                if(headX ==width-1) headX = 0;
                else headX++;
                break;
            case TOP:
                if(headY ==0) headY = height-1;
                else headY--;
                break;
            case BOTTOM:
                if(headY ==height-1) headY = 0;
                else headY++;
                break;
        }
        Cell headCell = field.getCell(headX, headY);
        headCell.getContent().eat(field, this, headCell);
        headCell.setContent(new SnakeCellContent());
        bodyParts.addFirst(headCell);
    }

    public boolean setDir(Direction dir) {
        if(dir.isOpposite(this.dir)) return false;
        this.dir = dir;
        return true;
    }

    public Direction getDir() {
        return dir;
    }

    public int getHeadX() {
        return headX;
    }

    public int getHeadY() {
        return headY;
    }
}
