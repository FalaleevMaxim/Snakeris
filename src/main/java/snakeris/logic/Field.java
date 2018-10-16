package snakeris.logic;

import snakeris.listener.CellUpdateListener;
import snakeris.logic.cell.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * Игровое поле
 */
public class Field {
    /**
     * Массив ячеек поля размером width*height.
     * Обращение cells[x][y] таким образом, это массив столбцов
     */
    private Cell[][] cells;
    /**
     * Ширина поля (размер массива столбцов {@link #cells})
     */
    private final int width;
    /**
     * Высота поля (размер столбца в массиве {@link #cells})
     */
    private final int height;
    /**
     * Используется для генерации положения еды
     */
    private final Random foodRandom = new Random();
    /**
     * Ссылка на змейку. В этом классе пока не нужна т.к. управляется из основного класа {@link snakeris.Main}
     */
    private Snake snake;
    /**
     * Список падающих блоков на поле. Поле отвечает за падение блоков.
     */
    private List<FallingBlock> fallingBlocks = new LinkedList<>();
    /**
     * Слушатели обновления ячеек. Каждый раз при обновлении содержимого ячейки оповещаются все слушатели.
     * @see #cellUpdated(Cell, CellContent)
     * @see snakeris.ui.FieldPaneMapping
     */
    private final List<CellUpdateListener> cellListeners = new ArrayList<>();
    /**
     * Слушатели, оповещаемые когда змейка ест еду. Принимают питательность съеденной еды
     * @see #onFoodEaten(int)
     */
    private final List<Consumer<Integer>> eatListeners = new ArrayList<>();
    /**
     * Слушатели, срабатывающие при удалении заполненных рядов в тетрисе.
     * Принимают количество рядов, которое было изменено
     */
    private final List<Consumer<Integer>> rowRemoveListeners = new ArrayList<>();

    public Field(int width, int height) {
        if(width<3 || height<3) throw new IllegalArgumentException("Field size must be 3 or bigger");
        this.width = width;
        this.height = height;
        cells = new Cell[width][];
        for (int x = 0; x < width; x++) {
            cells[x] = new Cell[height];
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(this, x, y);
            }
        }

        //Когда на поле убираются ряды тетриса, нужно генерировать столько еды, сколько рядов убрано
        rowRemoveListeners.add(n -> {
            for (int i = 0; i < n-1; i++) {
                randomizeFood(1);
            }
            //Последняя сгенерированная еда имеет питательность равную количеству удалённых рядов.
            randomizeFood(n);
        });
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Возвращает ячейку по координатам. Инкапсулирует массив {@link #cells}.
     * Массив не должен меняться, меняться может содержимое ячеек.
     */
    public Cell getCell(int x, int y){
        if(x<0 || x>=cells.length || y<0 || y>=cells[x].length)
            return null;
        return cells[x][y];
    }

    /**
     * Устанавливает в случайную свободную ячейку еду с заданной питательностью
     * @param nutrition питательность генерируемой еды
     * @see FoodCellContent#nutrition
     */
    public void randomizeFood(int nutrition){
        List<Cell> emptyCells = new ArrayList<>(width*height);
        int rowLim;
        for (rowLim = 0; rowLim < height; rowLim++) rows: {
            for (int i = 0; i < width; i++) {
                if(cells[i][rowLim].getContent().getName().equals(StaticCellContent.NAME))
                    break rows;
            }
        }
        for (Cell[] col : cells) {
            for (int i = 0; i < rowLim; i++) {
                Cell cell = col[i];
                if(cell.getContent()== EmptyCellContent.instance){
                    emptyCells.add(cell);
                }
            }
        }
        if(emptyCells.isEmpty()) return;
        emptyCells.get(foodRandom.nextInt(emptyCells.size()))
                .setContent(new FoodCellContent(nutrition));
    }

    public void addCellListener(CellUpdateListener listener){
        cellListeners.add(listener);
    }

    public void addEatListener(Consumer<Integer> listener){
        eatListeners.add(listener);
    }

    public void addRowRemoveListener(Consumer<Integer> listener){
        rowRemoveListeners.add(listener);
    }

    void cellUpdated(Cell cell, CellContent old){
        for (CellUpdateListener listener : cellListeners) {
            listener.onCellUpdate(cell, old);
        }
    }

    public void addFallingBlock(FallingBlock block){
        fallingBlocks.add(block);
    }

    /**
     * Запускает падение блоков по таймеру
     */
    public void action(){
        if(fallingBlocks.isEmpty()) return;

        //Трансформирует блоки, пока что-то трансформируется
        // Если в первую итерацию трансформировался блок, в следующую итерацию на него могут упасть другие блоки и т.д.
        boolean nothingToTransform = false;
        while (!nothingToTransform && !fallingBlocks.isEmpty()) {
            if(fallingBlocks.removeIf(FallingBlock::tryTransform)){
                removeStaticRows();
            }else {
                nothingToTransform = true;
            }
        }

        //Если все блоки трансформировались, больше делать нечего
        if(fallingBlocks.isEmpty()) return;

        //Останавливает блоки, пока что-то останавливается
        //Та же логика, что и выше при трансформировании
        List<FallingBlock> notStopped = new ArrayList<>(fallingBlocks);
        boolean nothingToStop = false;
        while (!nothingToStop && !fallingBlocks.isEmpty()) {
            if(!notStopped.removeIf(FallingBlock::tryStop)){
                nothingToStop = true;
            }
        }

        //Все оставшиеся не остановившиеся блоки падают
        notStopped.forEach(FallingBlock::fall);

        //Все остановленные блоки чистят флаги об остановке
        fallingBlocks.stream()
                .filter(block -> !notStopped.contains(block))
                .forEach(FallingBlock::clearStopped);
    }

    /**
     * Удаляет падающий блок если он трансформировался или был съеден полностью
     * @param block удаляемый блок
     */
    public void removeFallingBlock(FallingBlock block){
        fallingBlocks.remove(block);
    }

    /**
     * Удаляет все собранные в тетрисе ряды
     */
    private void removeStaticRows(){
        int removed = 0;
        for (int y = height-1; y > 0 ;) {
            boolean filled = true;
            for (int x = 0; x < width; x++) {
                if(!cells[x][y].getContent().getName().equals(StaticCellContent.NAME)) {
                    filled = false;
                    break;
                }
            }
            if(filled){
                removeStaticRow(y);
                removed++;
            }else {
                y--;
            }
        }
        int res = removed;
        rowRemoveListeners.forEach(listener -> listener.accept(res));
    }

    /**
     * Удаляет собранный в тетрисе ряд и смещает все вышестоящие статические блоки вниз
     * @param row удаляемый ряд
     */
    private void removeStaticRow(int row) {
        for (int y = row; y>0; y--) {
            for (int x = 0; x < width; x++) {
                CellContent upperCellContent = cells[x][y - 1].getContent();
                if(upperCellContent instanceof StaticCellContent) {
                    cells[x][y].getContent().onStaticFall(cells[x][y], this);
                    cells[x][y-1].setContent(EmptyCellContent.instance);
                    cells[x][y].setContent(StaticCellContent.instance);
                }else if(cells[x][y].getContent() instanceof StaticCellContent){
                    cells[x][y].setContent(EmptyCellContent.instance);
                }
            }
        }
    }

    public void setSnake(Snake snake) {
        this.snake = snake;
    }

    public void onFoodEaten(int nutrition) {
        randomizeFood(nutrition);
        eatListeners.forEach(eat -> eat.accept(nutrition));
    }
}
