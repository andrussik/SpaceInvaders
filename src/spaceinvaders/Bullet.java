package spaceinvaders;

import javafx.scene.paint.Color;

public class Bullet extends Model {

    public Bullet(int x, int y, int w, int h, String type, Color color) {
        super(x, y, w, h, type, color);
        super.filler("/resources/alien.png");
    }
}
