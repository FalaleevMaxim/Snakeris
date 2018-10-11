package snakeris.logic;

import snakeris.Direction;
import snakeris.logic.cell.EmptyCellContent;
import snakeris.logic.cell.SnakeCellContent;
import snakeris.logic.exception.SnakeDiedException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Snake {
    private final Field field;
    private LinkedList<Cell> bodyParts = new LinkedList<>();
    volatile private Direction dir = Direction.RIGHT;
    private int toGrow = 0;
    private int headX, headY;
    private boolean dead = false;

    public Snake(int initLengh, Field field) {
        if(field==null) throw new NullPointerException("Field is null");
        if(initLengh<=0) {
            throw new IllegalArgumentException("Negative or null length of snake!");
        }
        this.field = field;
        field.setSnake(this);
        for (int i = initLengh-1; i >=0 ; i--) {
            Cell cell = field.getCell(i, 0);
            cell.setContent(new SnakeCellContent());
            bodyParts.add(cell);
        }
        headX = initLengh-1;
        headY = 0;
    }

    public void move(){
        if(dead) throw new SnakeDiedException();
        if(toGrow>0) {
            toGrow--;
        } else {
            bodyParts.getLast().setContent(EmptyCellContent.instance);
            bodyParts.removeLast();
        }
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

    public void grow(){
        toGrow++;
    }

    public int getHeadX() {
        return headX;
    }

    public int getHeadY() {
        return headY;
    }

    public void eatenTail(Cell eaten){
        if(!bodyParts.contains(eaten)) throw new IllegalArgumentException("Cell is not part of snake");
        List<Cell> separated = new ArrayList<>(bodyParts.size()/2);
        Cell removed;
        do{
             removed = bodyParts.removeLast();
             if(!removed.equals(eaten)) {
                 separated.add(removed);
             }
        }while (!removed.equals(eaten));
        new FallingBlock(separated, field);
        grow();
    }

    public void die(){
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }
}
