package snakeris.ui;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import snakeris.listener.CellUpdateListener;
import snakeris.logic.Cell;
import snakeris.logic.Field;
import snakeris.logic.cell.CellContent;
import snakeris.logic.cell.EmptyCellContent;
import snakeris.logic.cell.SnakeCellContent;

import java.util.HashMap;
import java.util.Map;

public class FieldPaneMapping implements CellUpdateListener {
    private final Pane pane;
    private final int cellSize;
    private final Node[][] fieldNodes;
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

    private void initMappers() {
        mappers.put(EmptyCellContent.NAME, (cell, cellSize) -> null);
        mappers.put(SnakeCellContent.NAME, (cell, cellSize) -> new Rectangle(cellSize, cellSize, Color.DARKGREEN));
    }

    @Override
    public void onCellUpdate(Cell cell, CellContent old) {
        CellNodeMapper mapper = mappers.get(cell.getContent().getName());
        if(mapper==null) throw new UnsupportedOperationException("Unknown type of cell content");
        int x = cell.getX();
        int y = cell.getY();
        Node node = mapper.map(cell, cellSize);
        Node oldNode = fieldNodes[x][y];
        if(oldNode !=null){
            pane.getChildren().remove(oldNode);
            fieldNodes[x][y] = null;
        }
        if(node!=null){
            node.setTranslateX(x*cellSize);
            node.setTranslateY(y*cellSize);
            pane.getChildren().add(node);
        }
        fieldNodes[x][y] = node;
    }
}
