package spaceinvaders;


import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public abstract class Model extends Rectangle {

    boolean dead = false;
    final String type;

    Model(int x, int y, int w, int h, String type, Color color) {
        super(w, h);

        this.type = type;
        setTranslateX(x);
        setTranslateY(y);
    }

    public void filler(String path){
        Image img = new Image(path);
        this.setFill(new ImagePattern(img));
    }

    public void setDead(){
        dead = true;
    }

    public void setAlive(){
        dead = false;
    }

    public String getType(){
        return type;
    }

    void moveLeft() {
        setTranslateX(getTranslateX() - 7);
    }


    void moveRight() {
        setTranslateX(getTranslateX() + 7);
    }


    void moveUp() {
        setTranslateY(getTranslateY() - 7);
    }

    void moveDown() {
        setTranslateY(getTranslateY() + 7);
    }

}
