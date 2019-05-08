package spaceinvaders;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class Main extends Application {

    private Pane root = new Pane();
    private double t = 0;
    private Model player = new Player(300, 600, 40, 40, "player", Color.BLUE);

    private Parent createContent() {

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

    private void addAliens() {
        for (int i = 0; i < 5; i++) {
            Alien alien = new Alien(90 + i*100, 150, 30, 30, "alien", Color.RED);

            root.getChildren().add(alien);
        }
    }

    private List<Model> models() {
        return root.getChildren().stream().map(n -> (Model)n).collect(Collectors.toList());
    }

    private void update() {
        t += 0.016;

        models().forEach(alien -> {
            switch (alien.type) {

                case "alienbullet":
                    alien.moveDown();

                    if (alien.getBoundsInParent().intersects(player.getBoundsInParent())) {
                        player.setDead();
                        alien.setDead();
                    }
                    break;

                case "playerbullet":
                    alien.moveUp();

                    models().stream().filter(e -> e.type.equals("alien")).forEach(enemy -> {
                        if (alien.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                            enemy.setDead();
                            alien.setDead();
                        }
                    });
                    break;

                case "alien":
                    if (t > 2) {
                        if (Math.random() < 0.3) {
                            shoot(alien);
                        }
                    }
                    break;
            }
        });

        root.getChildren().removeIf(n -> {
            Model alien = (Model) n;
            return alien.dead;
        });

        if (player.dead) {

        }

        if (t > 2) {
            t = 0;
        }
    }

    private void shoot(Model who) {
        Model bullet = new Bullet((int) who.getTranslateX() + 18, (int) who.getTranslateY(), 5, 20, who.type + "bullet", Color.BLACK);

        root.getChildren().add(bullet);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(createContent());

        Image img = new Image("/resources/background1.png");
        scene.setFill(new ImagePattern(img));

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case A:
                    player.moveLeft();
                    break;
                case D:
                    player.moveRight();
                    break;
                case SPACE:
                    shoot(player);
                    break;
            }
        });

        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}