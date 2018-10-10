package snakeris;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.Random;

public class Main extends Application {

    private static final int GRID_SIZE = 30;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private static final double SNAKE_SPEED = 1.5;

    public static void main(String[] args) {
        launch(args);
    }


    private Pane root;
    private Snake snake;
    private AnimationTimer timer;
    private volatile Direction dir = Direction.RIGHT;
    private volatile boolean dirChanged = false;
    private Random random = new Random();
    private int foodPos;
    private Circle food;


    private Parent createContent(){
        root = new Pane();
        root.setPrefSize(WIDTH, HEIGHT);
        drawGrid();
        snake = new Snake(4, root, GRID_SIZE, WIDTH/ GRID_SIZE, HEIGHT/ GRID_SIZE);

        timer = new AnimationTimer() {
            long last = 0;
            @Override
            public void handle(long now) {
                if(last==0){
                    last = now;
                    return;
                }
                if(dirChanged || now>last+(1000000000L)/SNAKE_SPEED){
                    last = now;
                    snake.setDir(dir);
                    dirChanged = false;
                    snake.move();
                }
            }
        };
        timer.start();
        return root;
    }

    @Override
    public void start(Stage primaryStage){
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("TetriSnake");
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
                default:
                    break;
            }
        });
        primaryStage.show();
    }

    public void setDir(Direction dir) {
        if(dir.isOpposite(snake.getDir())) return;
        this.dir = dir;
        dirChanged = true;
    }

    private void drawGrid(){
        int xLim = WIDTH - WIDTH%GRID_SIZE;
        int yLim = HEIGHT - HEIGHT%GRID_SIZE;
        for (int i = 0; i <= WIDTH / GRID_SIZE; i++) {
            Line line = new Line(i*GRID_SIZE, 0, i*GRID_SIZE, yLim);
            line.setStroke(Color.valueOf("#EEEEEE"));
            root.getChildren().add(line);
        }
        for (int i = 0; i <= HEIGHT / GRID_SIZE; i++) {
            Line line = new Line(0, i*GRID_SIZE, xLim, i*GRID_SIZE);
            line.setStroke(Color.valueOf("#EEEEEE"));
            root.getChildren().add(line);
        }
    }
}
