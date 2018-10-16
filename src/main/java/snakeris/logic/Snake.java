package snakeris.logic;

import snakeris.Direction;
import snakeris.logic.cell.EmptyCellContent;
import snakeris.logic.cell.SnakeCellContent;
import snakeris.logic.exception.SnakeDiedException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Snake {
    /**
     * Ссылка на поле
     */
    private final Field field;
    /**
     * Ячейки поля, являющиеся телом змейки
     * Первый элемент - голова
     */
    private LinkedList<Cell> bodyParts = new LinkedList<>();
    /**
     * Направление движения змейки
     */
    volatile private Direction dir = Direction.RIGHT;
    /**
     * Показывает, должна ли змейка расти при движении.
     * Если больше 0, змейка растёт и значение уменьшается на 1
     */
    private int toGrow = 0;
    /**
     * Координаты головы
     */
    private int headX, headY;
    /**
     * Показывает что змейка мертва. Мёртвая змейка не может двигаться и бросает {@link SnakeDiedException}
     */
    private boolean dead = false;

    /**
     *
     * @param initLengh начальная длина змейки. Должна быть больше 0 хотя бы на 1 меньше ширины поля
     * @param field поле по которому двигается змейка. Не может быть null.
     */
    public Snake(int initLengh, Field field) {
        if(field==null) throw new NullPointerException("Field is null");
        if(initLengh<=0) {
            throw new IllegalArgumentException("Negative or null length of snake!");
        }
        if(initLengh>=field.getWidth()){
            throw new IllegalArgumentException("Snake must be shorter than field width");
        }
        this.field = field;
        field.setSnake(this);
        for (int i = initLengh-1; i >=0 ; i--) {
            Cell cell = field.getCell(i, 0);
            cell.setContent(new SnakeCellContent(this));
            bodyParts.add(cell);
        }
        headX = initLengh-1;
        headY = 0;
    }

    /**
     * Движение змейки на 1 клетку в направлении {@link #dir}.
     * Поменять направление можно с помощью {@link #setDir(Direction).
     *
     * Движение происходит установкой новой головы змейки в соседнюю клетку от текущей головы по направлению движения.
     * Если змейка не должна расти, удаляется последняя ячейка хвоста.
     */
    public void move(){
        if(dead) throw new SnakeDiedException();
        //Если змейка должна расти, счётчик роста уменьшается
        if(toGrow>0) {
            toGrow--;
        } else {
            //Иначе удаляется последняя ячейка хвоста (при этом её содержимое очищается)
            bodyParts.getLast().setContent(EmptyCellContent.instance);
            bodyParts.removeLast();
        }

        //Если змейка должна укоротиться, хвост укорачивается, но змейка не двигается.
        if(toGrow<0){
            toGrow++;
            //Если тело кончилось, змея умирает.
            if (bodyParts.isEmpty()) die();
            return;
        }

        int width = field.getWidth();
        int height = field.getHeight();

        //Получение новых координат головы в зависимости от направления движения.
        // Если достигается граница поля, голова появляется с противоположной стороны поля.
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
        //Получение ячейки по направлению движения
        Cell headCell = field.getCell(headX, headY);
        //Попытка съесть текущее содержимое ячейки. Поведение при поедании определяется в содержимом ячейки
        headCell.getContent().eat(field, this, headCell);
        //Установка ячейки в качестве головы
        headCell.setContent(new SnakeCellContent(this));
        bodyParts.addFirst(headCell);
    }

    /**
     * Меняет направление движения. Новое направление не должно быть противоположным текущему ({@link Direction#isOpposite(Direction)}).
     * @param dir новое направление.
     * @return {@code true} если направление изменено; {@code false} если направление противоположное и не может быть установлено.
     */
    public boolean setDir(Direction dir) {
        if(dir.isOpposite(this.dir)) return false;
        this.dir = dir;
        return true;
    }

    public Direction getDir() {
        return dir;
    }

    /**
     * Указывает, что змейка должнат вырасти при движении.
     */
    public void grow(){
        toGrow++;
    }

    public void grow(int n){
        toGrow+=n;
    }

    public void shorten(){
        toGrow--;
    }

    public void shorten(int n){
        toGrow-=n;
    }

    public int getHeadX() {
        return headX;
    }

    public int getHeadY() {
        return headY;
    }

    /**
     * Вызывается, когда змейка съела кусок своего хвоста.
     * @param eaten Клетка которая съедена.
     * @see SnakeCellContent#eat(Field, Snake, Cell)
     */
    public void eatenTail(Cell eaten){
        removeBodyBlock(eaten);
        grow();
    }

    /**
     * Змейка умирает, и игра заканчивается.
     * @see snakeris.logic.cell.StaticCellContent#eat(Field, Snake, Cell)
     */
    public void die(){
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    /**
     * Убирает блок от змейки. Если после этого блока были ещё блоки в хвосте, хвост превращается в падающий блок
     * @param cell Ячейка, которую нужно удалить
     */
    public void removeBodyBlock(Cell cell) {
        if(!bodyParts.contains(cell)) throw new IllegalArgumentException("Cell is not part of snake");
        // Список блоков, отделённых от змеи
        List<Cell> separated = new ArrayList<>(bodyParts.size()/2);
        Cell removed;
        //Удаление последнего блока из тела змеи и добавление этого блока в separeted, пока не дойдём до удалённого блока cell
        do{
            removed = bodyParts.removeLast();
            if(!removed.equals(cell)) {
                separated.add(removed);
            }
        }while (!removed.equals(cell));
        //Если что-то отделено, оно превращается в падающий блок
        if(!separated.isEmpty()) {
            new FallingBlock(separated, field);
        }
    }
}
