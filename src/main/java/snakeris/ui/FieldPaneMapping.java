package snakeris.ui;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import snakeris.listener.CellUpdateListener;
import snakeris.logic.Cell;
import snakeris.logic.Field;
import snakeris.logic.cell.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Отображает поле {@link Field} в таблицу {@link Node} интерфейса
 */
public class FieldPaneMapping implements CellUpdateListener {
    /**
     * Панель содержащая отображаемые узлы
     */
    private final Pane pane;
    /**
     * Размер стороны ячейки
     */
    private final int cellSize;
    /**
     * Отображаемые узлы
     */
    private final Node[][] fieldNodes;
    /**
     * Мапперы, преобразующие содержимое ячейки в отоюражаемый узел.
     * Ключ - имя типа содержимого ячейки {@link CellContent#getName()}
     * Для каждого типа содержимого ячейки следует добавить маппер.
     */
    private final Map<String, CellNodeMapper> mappers = new HashMap<>();

    public FieldPaneMapping(Field field, Pane pane, int cellSize) {
        this.pane = pane;
        this.cellSize = cellSize;

        fieldNodes = new Node[field.getWidth()][];
        for (int i = 0; i < field.getWidth(); i++) {
            fieldNodes[i] = new Node[field.getHeight()];
        }

        initMappers();

        field.addCellListener(this);
    }

    /**
     * Задаёт мапперы для всех типов содержимого.
     */
    private void initMappers() {
        mappers.put(EmptyCellContent.NAME, (cell, cellSize) -> null);
        mappers.put(SnakeCellContent.NAME, (cell, cellSize) -> createRectangle(cell, Color.DARKGREEN));
        mappers.put(FoodCellContent.NAME, (cell, cellSize1) -> {
            double radius = cellSize1 / 2.;
            Color color;
            FoodCellContent content = (FoodCellContent) cell.getContent();
            if (content.nutrition > 0) color = Color.YELLOW;
            else color = Color.RED;
            Circle circle = new Circle(cell.getX() * cellSize1 + radius, cell.getY() * cellSize1 + radius, radius, color);
            if (Math.abs(content.nutrition) == 1) return circle;
            Text text = new Text(
                    cell.getX() * cellSize1 + cellSize1/3.,
                    cell.getY() * cellSize1 + cellSize1*3/4.,
                    Integer.toString(content.nutrition));
            return new Pane(circle, text);
        });
        mappers.put(FallingBlockCellContent.NAME, (cell, cellSize) -> createRectangle(cell, Color.DARKRED));
        mappers.put(StaticCellContent.NAME, (cell, cellSize) -> createRectangle(cell, Color.BLACK));
    }

    /**
     * Вспомогательный метод, создающий квадрат
      * @param cell ячейка, в соответствии с координатами которой нужно сделать квадрат
     * @param color цвет квадрата
     * @return Квадрат с координатами ячейки и заданным цветом
     */
    private Node createRectangle(Cell cell, Color color) {
        Rectangle rect = new Rectangle(cellSize, cellSize, color);
        rect.setTranslateX(cell.getX() * cellSize);
        rect.setTranslateY(cell.getY() * cellSize);
        return rect;
    }

    /**
     * При обновлении содержимого ячейки использует маппер и обновляет соответствующий отображаемый узел
     * @param cell обновлённая ячейка
     * @param old старое значение содержимого ячейки
     */
    @Override
    public void onCellUpdate(Cell cell, CellContent old) {
        CellNodeMapper mapper = mappers.get(cell.getContent().getName());
        if (mapper == null) throw new UnsupportedOperationException("Unknown type of cell content");
        int x = cell.getX();
        int y = cell.getY();
        Node node = mapper.map(cell, cellSize);
        Node oldNode = fieldNodes[x][y];
        if (oldNode != null) {
            pane.getChildren().remove(oldNode);
            fieldNodes[x][y] = null;
        }
        if (node != null) {
            pane.getChildren().add(node);
        }
        fieldNodes[x][y] = node;
    }
}
