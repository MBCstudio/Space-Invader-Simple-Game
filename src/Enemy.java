import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public abstract class Enemy extends Thread{
    protected int x;
    protected int y;
    protected int speed;
    protected String enemyIconURL;
    protected Image enemyIcon;
    protected int IconWidth;
    protected int IconHeight;
    protected boolean isVisible = true;
    //private int notVisibleRocks = 0;

    public Enemy(int x, int y, int speed, int IconWidth, int IconHeight, String enemyIconURL){
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.IconWidth = IconWidth;
        this.IconHeight = IconHeight;
        this.enemyIconURL = enemyIconURL;
    }

    public abstract void move();
    public boolean checkCollicion(Player player){
        Rectangle playerArea = new Rectangle(player.getX(), player.getY(), player.getIconX(), player.getIconY());
        Rectangle enemyArea = new Rectangle(x, y, IconWidth, IconHeight);

        if (playerArea.intersects(enemyArea)){
            //System.out.println("Dotknieto skłay");
            return true;
        }else {
            return false;
        }
    }

    public void draw(Graphics g) {
        try {
            //getting bg img
            enemyIcon = ImageIO.read(new File(enemyIconURL));
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(enemyIcon, x, y, null);
    }

    public int getIconWidth() {
        return IconWidth;
    }

    public int getIconHeight() {
        return IconHeight;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    private void isEndThread(){
        if (y < 0){
            isVisible = false;
        }
    }

    public void run(){
        while (isVisible){
            move();
            isEndThread(); //sprawdzenie czy obiekt jeszcze jest na ekranie

            //jezli obiekt wyleciał to konczymy watek
            if (!isVisible){
                break;
            }

            try {
                Thread.sleep(10);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
