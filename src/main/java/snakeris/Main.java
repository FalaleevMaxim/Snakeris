package snakeris;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import snakeris.logic.Field;
import snakeris.logic.Snake;
import snakeris.logic.cell.StaticCellContent;
import snakeris.ui.FieldPaneMapping;

import java.util.Optional;

public class Main extends Application {

    private static final int GRID_SIZE = 20;
    private static final int WIDTH = 20;
    private static final int HEIGHT = 30;
    private static final double SNAKE_SPEED = 1.5;
    private static final double BLOCKS_SPEED = 1.5;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }


    private Pane root;
    private Field field;
    private FieldPaneMapping fieldPaneMapping;
    private Snake snake;
    private volatile Direction dir = Direction.RIGHT;
    private volatile boolean dirChanged = false;
    private volatile boolean active = true;
    private int score = 0;

    private Parent createContent(){
        field = new Field(WIDTH,HEIGHT);
        root = new Pane();
        root.setPrefSize(WIDTH*GRID_SIZE, HEIGHT*GRID_SIZE);
        drawGrid();
        fieldPaneMapping = new FieldPaneMapping(field, root, GRID_SIZE);
        snake = new Snake(15, field);
        field.randomizeFood();
        field.addEatListener(() -> setScore(score + 1));
        field.addRowRemoveListener(n -> setScore(score + (2*100+(n-1)*100)*n/2));

        AnimationTimer timer = new AnimationTimer() {
            long lastSnakeMove = 0;
            long lastAction = 0;
            @Override
            public void handle(long now) {
                if(!active) return;
                if(lastSnakeMove ==0){
                    lastSnakeMove = now;
                    return;
                }
                if(lastAction ==0){
                    lastAction = now;
                    return;
                }
                if(now> lastAction +(1000000000L)/BLOCKS_SPEED){
                    lastAction = now;
                    field.action();
                }
                if(dirChanged || now> lastSnakeMove +(1000000000L)/SNAKE_SPEED){
                    lastSnakeMove = now;
                    snake.setDir(dir);
                    dirChanged = false;
                    snake.move();
                    if(snake.isDead()){
                        this.stop();
                        Platform.runLater(Main.this::notifyGameOver);
                    }
                }
            }
        };
        timer.start();
        return root;
    }

    private void notifyGameOver() {
        Dialog<ButtonType> d = new Alert(Alert.AlertType.ERROR, "Игра окончена! Перезапустить?", ButtonType.FINISH, ButtonType.NEXT);
        d.setHeaderText("Счёт: "+score);
        d.setTitle("Game over!");
        Optional<ButtonType> button = d.showAndWait();
        if(button.orElse(ButtonType.FINISH).equals(ButtonType.FINISH)){
            Platform.exit();
            return;
        }
        start(primaryStage);
    }

    @Override
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        setScore(0);
        Scene scene = new Scene(createContent());
        primaryStage.setScene(scene);
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()){
                case LEFT:
                case A:
                    setDir(Direction.LEFT);
                    break;
                case RIGHT:
                case D:
                    setDir(Direction.RIGHT);
                    break;
                case UP:
                case W:
                    setDir(Direction.TOP);
                    break;
                case DOWN:
                case S:
                    setDir(Direction.BOTTOM);
                    break;
                case SPACE:
                    active = !active;
                default:
                    break;
            }
        });
        primaryStage.show();
    }

    public void setDir(Direction dir) {
        if(!active) return;
        if(dir.isOpposite(snake.getDir())) return;
        this.dir = dir;
        dirChanged = true;
    }

    private void drawGrid(){
        for (int i = 0; i <= WIDTH; i++) {
            Line line = new Line(i*GRID_SIZE, 0, i*GRID_SIZE, HEIGHT*GRID_SIZE);
            line.setStroke(Color.valueOf("#EEEEEE"));
            root.getChildren().add(line);
        }
        for (int i = 0; i <= HEIGHT; i++) {
            Line line = new Line(0, i*GRID_SIZE, WIDTH*GRID_SIZE, i*GRID_SIZE);
            line.setStroke(Color.valueOf("#EEEEEE"));
            root.getChildren().add(line);
        }
    }

    public void setScore(int score) {
        this.score = score;
        primaryStage.setTitle("Snakeris\tСчёт: "+score);
    }
}
