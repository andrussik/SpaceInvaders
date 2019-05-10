package spaceinvaders;


import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends Application {

    private Scene scene;
    private VBox menuBox;
    private List<MenuItem> menuItems = new ArrayList<>();
    private int currentItem;
    private static Pane root;
    private static double t = 0;
    private static int aliensLeft = 0;
    private static final Font FONT = Font.font("", FontWeight.BOLD, 18);
    private static Model player = new Player(300, 600, 40, 40, "player", Color.BLUE);

    private Parent mainMenu() {
        root = new StackPane();
        root.setPrefSize(600, 800);

        MenuItem startGame = new MenuItem("START GAME");
        MenuItem exitGame = new MenuItem("EXIT");

        menuItems.addAll(Arrays.asList(startGame, exitGame));

        menuBox = new VBox();
        menuBox.setSpacing(15);
        menuBox.setAlignment(Pos.CENTER);

        for (MenuItem menuitem : menuItems) {
            menuBox.getChildren().add(menuitem);
        }

        getMenuItem(0).setActive(true);

        root.getChildren().addAll(menuBox);

        return root;
    }

    private Parent startGame() {
        root = new Pane();
        root.setPrefSize(600, 800);
        root.getChildren().add(player);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                update();
            }
        };

        timer.start();
        addAliens();

        return root;
    }

    private void endGame(Boolean won) {
        Text text;

        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        if (won) {
            root.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            text = new Text("WINNER!");
            text.setTextAlignment(TextAlignment.CENTER);
            root.getChildren().add(text);
        } else {
            root.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            text = new Text("LOSER!");
            text.setTextAlignment(TextAlignment.CENTER);
            root.getChildren().add(text);
        }

        pause.setOnFinished(e -> mainMenu());
        pause.play();
    }

    private static class MenuItem extends HBox {
        public String name;
        private TriCircle c1 = new TriCircle(), c2 = new TriCircle();
        private Text text;
        private Runnable script;

        public MenuItem(String name) {
            super(15);
            this.name = name;
            setAlignment(Pos.CENTER);

            text = new Text(name);
            text.setFont(FONT);
            text.setEffect(new GaussianBlur(2));
            getChildren().addAll(c1, text, c2);
            setActive(false);
            setOnActivate(() -> System.out.println(name + " activated"));
        }

        public void setActive(boolean b) {
            c1.setVisible(b);
            c2.setVisible(b);
            text.setFill(b ? Color.WHITE : Color.GREY);
        }

        public void setOnActivate(Runnable r) {
            script = r;
        }

        public void activate() {
            if (script != null)
                script.run();
        }

    }

    private MenuItem getMenuItem(int index) {
        return (MenuItem)menuBox.getChildren().get(index);
    }

    private void menuItemActions(Stage stage) {
        for (MenuItem menuItem : menuItems) {
            if (menuItem.name.equals("START GAME")) {
                menuItem.setOnActivate(() -> startGameScene(stage));
            }
            if (menuItem.name.equals("EXIT")) {
                menuItem.setOnActivate(() -> System.exit(0));
            }
        }
    }

    private static class TriCircle extends Parent {
        public TriCircle() {
            Shape shape1 = Shape.subtract(new Circle(5), new Circle(2));
            shape1.setFill(Color.WHITE);

            Shape shape2 = Shape.subtract(new Circle(5), new Circle(2));
            shape2.setFill(Color.WHITE);
            shape2.setTranslateX(5);

            Shape shape3 = Shape.subtract(new Circle(5), new Circle(2));
            shape3.setFill(Color.WHITE);
            shape3.setTranslateX(2.5);
            shape3.setTranslateY(-5);

            getChildren().addAll(shape1, shape2, shape3);

            setEffect(new GaussianBlur(2));
        }
    }

    public static void addAliens() {
        for (int i = 0; i < 5; i++) {
            Alien alien = new Alien(90 + i*100, 150, 30, 30, "alien", Color.RED);
            aliensLeft++;
            root.getChildren().add(alien);
        }
    }

    private static List<Model> models() {
        return root.getChildren().stream()
                .filter(o -> o instanceof Model)
                .map(n -> (Model)n)
                .collect(Collectors.toList());
    }

    public void update() {
        t += 0.016;

        models().forEach(bullet -> {
            switch (bullet.type) {
                case "alienbullet":
                    bullet.moveDown();

                    if (bullet.getBoundsInParent().intersects(player.getBoundsInParent())) {
                        player.setDead();
                        bullet.setDead();
                        root.getChildren().remove(player);
                        root.getChildren().remove(bullet);
                    }
                    break;
                case "playerbullet":
                    bullet.moveUp();

                    models().stream().filter(e -> e.type.equals("alien")).forEach(enemy -> {
                        if (bullet.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                            enemy.setDead();
                            bullet.setDead();
                            root.getChildren().remove(enemy);
                            root.getChildren().remove(bullet);
                            aliensLeft--;
                        }
                    });
                    break;
                case "alien":
                    if (t > 2) {
                        if (Math.random() < 0.3) {
                            shoot(bullet);
                        }
                    }
                    break;
            }
        });

        if (aliensLeft == 0) {
            endGame(true);
        }
        if (player.dead) {
            endGame(false);
        }

        if (t > 2) {
            t = 0;
        }
    }

    private static void shoot(Model who) {
        Model bullet = new Bullet((int) who.getTranslateX() + 18, (int) who.getTranslateY(),
                5, 20, who.type + "bullet", Color.BLACK);

        root.getChildren().add(bullet);
    }

    private void startGameScene(Stage stage) {
        scene = new Scene(startGame());
        Image img = new Image("/resources/background1.png");
        scene.setFill(new ImagePattern(img));

        stage.setScene(scene);
        stage.show();

        scene.setOnKeyPressed(k -> {
            switch (k.getCode()) {
                case LEFT:
                    player.moveLeft();
                    break;
                case RIGHT:
                    player.moveRight();
                    break;
                case SPACE:
                    shoot(player);
                    break;
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        scene = new Scene(mainMenu());
        Image img = new Image("/resources/background1.png");
        scene.setFill(new ImagePattern(img));
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Space Invaders");

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                if (currentItem > 0) {
                    getMenuItem(currentItem).setActive(false);
                    getMenuItem(--currentItem).setActive(true);
                }
            }

            if (event.getCode() == KeyCode.DOWN) {
                if (currentItem < menuBox.getChildren().size() - 1) {
                    getMenuItem(currentItem).setActive(false);
                    getMenuItem(++currentItem).setActive(true);
                }
            }

            if (event.getCode() == KeyCode.SPACE) {
                menuItemActions(stage);
                getMenuItem(currentItem).activate();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}