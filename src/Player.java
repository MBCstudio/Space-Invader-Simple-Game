import java.awt.*;

public class Player {
    private int x;
    private int y;
    private int iconX;
    private int iconY;
    private Image image;

    public Player(int x, int y, int iconX, int iconY,  Image image){
        this.x = x;
        this.y = y;
        this.iconX = iconX;
        this.iconY = iconY;
        this.image = image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getIconX() {
        return iconX;
    }

    public int getIconY() {
        return iconY;
    }

    public void moveRight(int screenWidth){
        if (x< screenWidth-iconX-10){
            x+=2;
        }
    }
    public void moveLetf(){
        if (x>8){
            x-=2;
        }
    }

    public void draw(Graphics g){
        g.drawImage(image, x, y, iconX , iconY, null);
    }
}
