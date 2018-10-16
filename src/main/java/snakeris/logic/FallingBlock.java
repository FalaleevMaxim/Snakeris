package snakeris.logic;

import snakeris.logic.cell.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Падающий блок.
 */
public class FallingBlock {
    /**
     * Список ячеек блока, отсортированный по возрастанию высоты
     */
    private final List<Cell> cells = new ArrayList<>();
    /**
     * Ссылка на поле
     */
    private final Field field;

    /**
     * Создаёт падающий блок из заданных ячеек и регистрируется в поле.
     * Если переданные ячейки не смежные, может создасться не один блок.
     * Например, змея может пройти через границу поля и откусить хвост:
     *  тогда одна часть хвоста будет на одной стороне, другая на другой, и это будут разные блоки
     * @param cells Принадлежащие блоку ячейки (в любом порядке)
     * @param field ссылка на поле
     * @throws IllegalArgumentException если список ячеек пустой
     */
    public FallingBlock(Collection<Cell> cells, Field field) {
        if (cells.isEmpty()) throw new IllegalArgumentException("Empty falling block");
        this.field = field;
        cells.forEach(cell -> cell.setContent(new FallingBlockCellContent(this)));
        //Ячейки сортируются по возрастанию высоты (убыванию Y) для правильного порядка при падении блока.
        this.cells.addAll(cells.stream()
                .sorted(Comparator.comparingInt(Cell::getY).reversed())
                .collect(Collectors.toList()));
        field.addFallingBlock(this);
        //Проверка целостности блока. Может разделить блок на два если есть отделённая группа ячеек
        checkAndSeparate();
    }

    /**
     * Проверяет, нужно ли трансформироваться в статический блок при падении
     *  и трансформируется при возможности
     * @return {@code true} если блок трансформировался
     */
    public boolean tryTransform() {
        boolean shouldTransform = false;
        for (Cell cell : cells) {
            Cell lower = field.getCell(cell.getX(), cell.getY() + 1);
            if (lower == null) {
                shouldTransform = true;
                break;
            }
            CellContent content = lower.getContent();
            if (content.transformsBlock()) {
                shouldTransform = true;
                break;
            }
        }
        if (shouldTransform) {
            transform();
            return true;
        }
        return false;
    }

    /**
     * Трансформирует блок в статический
     */
    public void transform() {
        for (Cell cell : cells) {
            cell.setContent(StaticCellContent.instance);
        }
    }

    /**
     * Проверяет, останавливается ли блок при падении
     * @return true сли блок остановлен
     */
    public boolean tryStop() {
        boolean canStop = false;
        for (Cell cell : cells) {
            Cell lower = field.getCell(cell.getX(), cell.getY() + 1);
            CellContent content = lower.getContent();
            if (content.stopsFallingBlock()) {
                canStop = true;
                break;
            }
        }
        if (canStop) {
            stop();
            return true;
        }
        return false;
    }

    /**
     * Блок останавливается и не падает
     */
    private void stop() {
        cells.stream()
                .map(Cell::getContent)
                .map(content -> (FallingBlockCellContent) content)
                .forEach(content -> content.setStopped(true));
    }

    /**
     * Блок падает на 1 вниз
     */
    public void fall() {
        cells.replaceAll(cell -> {
            FallingBlockCellContent cont = (FallingBlockCellContent) cell.getContent();
            Cell lower = field.getCell(cell.getX(), cell.getY() + 1);
            FoodCellContent food = lower.getContent() instanceof FoodCellContent
                    ? (FoodCellContent) lower.getContent() : null;
            lower.setContent(new FallingBlockCellContent(this));
            if (cont.getBlock() == this) {
                cell.setContent(EmptyCellContent.instance);
            }
            if (food!=null) field.randomizeFood(food.nutrition);
            return lower;
        });
    }

    /**
     * Удаляет с клеток флаги о том что блок остановился
     */
    public void clearStopped() {
        for (Cell cell : cells) {
            ((FallingBlockCellContent) cell.getContent()).setStopped(false);
        }
    }

    public Collection<Cell> getCells() {
        return Collections.unmodifiableList(cells);
    }

    /**
     * даляет ячейку когда её съеда змея.
     * @param cell съеденная ячейка
     */
    public void cellEaten(Cell cell) {
        removeCell(cell);
    }

    /**
     * Удаляет ячейку блока. Проверяет целостность блока и разделяет на части при необходимости.
     * @param cell удаляемая ячейка
     */
    public void removeCell(Cell cell) {
        cells.remove(cell);
        if (cells.isEmpty()) field.removeFallingBlock(this);
        else checkAndSeparate();
    }

    /**
     * Проверяет целостность блока и разделяет его на два при необходимости
     */
    private void checkAndSeparate() {
        Set<Cell> separated = integrityCheck();
        if (separated.isEmpty()) return;
        cells.removeAll(separated);
        new FallingBlock(separated, field);
    }

    /**
     * Проверяет целостность блока.
     * @return отделённые клетки, которые нужно выделить в отдельный блок
     */
    private Set<Cell> integrityCheck() {
        Set<Cell> separated = new HashSet<>(cells);
        integrityCheck(cells.get(0), separated);
        return separated;
    }

    /**
     * Рекурсивная фунция, обходит блоки от начального, проверяя ячейки в 4 направлениях
     * @param cell текущая ячейка для проверки
     * @param rest непроверенные ячейки блока
     */
    private void integrityCheck(Cell cell, Set<Cell> rest) {
        if (cell == null) return;
        if (!cells.contains(cell)) return;
        if (!rest.remove(cell)) return;
        int x = cell.getX();
        int y = cell.getY();
        integrityCheck(field.getCell(x - 1, y), rest);
        integrityCheck(field.getCell(x + 1, y), rest);
        integrityCheck(field.getCell(x, y + 1), rest);
        integrityCheck(field.getCell(x, y - 1), rest);
    }
}
