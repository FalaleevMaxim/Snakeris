package snakeris;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.LinkedList;

public class Snake {
    private final Pane root;
    private LinkedList<Rectangle> bodyParts = new LinkedList<>();
    private int rectSize;
    volatile private Direction dir = Direction.RIGHT;
    private int xCellLim, yCellLim;
    private int headXCell, headYCell;

    public Snake(int initLengh, Pane root, int rectSize, int xCellLim, int yCellLim) {
        if(rectSize<=0) {
            throw new IllegalArgumentException("Negative or null rectangle size!");
        }
        if(initLengh<=0) {
            throw new IllegalArgumentException("Negative or null length of snake!");
        }
        if(xCellLim<=initLengh || yCellLim<=initLengh) {
            throw new IllegalArgumentException("Field width and height must be at least 1 cell greater than snake initial size");
        }
        this.root = root;
        this.rectSize = rectSize;
        this.xCellLim = xCellLim;
        this.yCellLim = yCellLim;
        for (int i = initLengh-1; i >=0 ; i--) {
            bodyParts.add(new Rectangle(i*rectSize, 0, rectSize, rectSize));
        }
        headXCell = initLengh-1;
        headYCell = 0;
        root.getChildren().addAll(bodyParts);
    }

    public void move(){
        Rectangle newHead = new Rectangle(rectSize, rectSize);
        root.getChildren().remove(bodyParts.removeLast());
        switch (dir) {
            case LEFT:
                headXCell--;
                break;
            case RIGHT:
                headXCell++;
                break;
            case TOP:
                headYCell--;
                break;
            case BOTTOM:
                headYCell++;
                break;
        }
        setRectPos(newHead, headXCell, headYCell);

        bodyParts.addFirst(newHead);
        root.getChildren().add(newHead);
    }

    private void setRectPos(Rectangle rect, int xCell, int yCell){
        rect.setTranslateX(xCell*rectSize);
        rect.setTranslateY(yCell*rectSize);
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
        return headXCell;
    }

    public int getHeadY() {
        return headYCell;
    }
}
