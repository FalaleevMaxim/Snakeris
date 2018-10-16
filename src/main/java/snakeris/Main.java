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
import snakeris.logic.cell.FoodCellContent;
import snakeris.logic.cell.StaticCellContent;
import snakeris.ui.FieldPaneMapping;

import java.util.Optional;

public class Main extends Application {
    /**
     * Размер ячейки поля в писелях
     */
    private static final int GRID_SIZE = 20;
    /**
     * Ширина поля в ячейках
     */
    private static final int WIDTH = 20;
    /**
     * Высота поля в ячейках
     */
    private static final int HEIGHT = 30;
    /**
     * Скорость движения змейки (ячеек в секунду)
     */
    private static final double SNAKE_SPEED = 1.5;
    /**
     * Скорость движения падающих блоков (ячеек в секунду)
     */
    private static final double BLOCKS_SPEED = 1.5;

    public static void main(String[] args) {
        launch(args);
    }



    private Stage primaryStage;
    private Pane root;
    private Field field;
    private FieldPaneMapping fieldPaneMapping;
    private Snake snake;
    private volatile Direction dir = Direction.RIGHT;
    /**
     * Обновлялось ли направление после последнего движения змейки
     */
    private volatile boolean dirChanged = false;
    /**
     * Идёт игра или пауза
     */
    private volatile boolean active = true;
    /**
     * Счёт
     */
    private int score = 0;

    /**
     * Создание и заполнение игрового поля
     * @return корневой элмент поля
     */
    private Parent createContent(){
        field = new Field(WIDTH,HEIGHT);
        root = new Pane();
        root.setPrefSize(WIDTH*GRID_SIZE, HEIGHT*GRID_SIZE);
        drawGrid();
        fieldPaneMapping = new FieldPaneMapping(field, root, GRID_SIZE);
        snake = new Snake(5, field);
        field.addEatListener(n -> setScore(score + n));
        field.addRowRemoveListener(n -> setScore(score + (2*100+(n-1)*100)*n/2));

        /*for (int y = HEIGHT-1; y >=HEIGHT-3 ; y--) {
            for (int x = 0; x < WIDTH; x++) {
                if(x!=5) field.getCell(x,y).setContent(StaticCellContent.instance);
            }
        }*/

        field.randomizeFood(1);
        field.randomizeFood(-1);

        //Таймер отсчитывает время, по которому происходит движение змейки и блоков.
        AnimationTimer timer = new AnimationTimer() {
            /**
             * Время последнего движения змейки
             */
            long lastSnakeMove = 0;
            /**
             * Время последнего падения блоков
             */
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
                //Если прошло достаточно времени, запускается падение блоков
                if(now> lastAction +(1000000000L)/BLOCKS_SPEED){
                    lastAction = now;
                    field.action();
                }
                //Если обновилось направление змейки или прошло достаточно времени с последнего движения, ход змейки
                if(dirChanged || now> lastSnakeMove +(1000000000L)/SNAKE_SPEED){
                    lastSnakeMove = now;
                    snake.setDir(dir);
                    dirChanged = false;
                    snake.move();
                    //Если змейка умерла, таймер останавливается и игра заканчивается
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

    /**
     * Диалог "Game over!"
     */
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

    /**
     * Инициализация и запуск игры
     * @param primaryStage
     */
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

    /**
     * Устанавливает направление змейки по нажатию клавиши
     * @param dir
     */
    public void setDir(Direction dir) {
        if(!active) return;
        if(dir.isOpposite(snake.getDir())) return;
        this.dir = dir;
        dirChanged = true;
    }

    /**
     * Рисует сетку
     */
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

    /**
     * Устанавливает очки и отображает их в заголовке окна
     * @param score
     */
    public void setScore(int score) {
        this.score = score;
        primaryStage.setTitle("Snakeris           Счёт: "+score);
    }
}
