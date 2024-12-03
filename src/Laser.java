import java.awt.*;

public class Laser extends Thread{
    private int x;
    private int y;
    private int speed = 2;
    private Image imgLaser;
    private boolean isVisible = true;
    private int imgX;
    private int imgY;

    public Laser(int x, int y, Image imgLaser, int imgX, int imgY){
        this.x =x;
        this.y = y;
        this.imgLaser = imgLaser;
        this.imgX = imgX;
        this.imgY = imgY;
    }

    private void move(){
        y-=speed;
    }

    public boolean collisionBulletRock(Enemy enemy){
        Rectangle bulletRect = new Rectangle(x,y,imgX, imgY);
        Rectangle enemyRect = new Rectangle(enemy.x, enemy.y, enemy.IconWidth, enemy.IconHeight);

        if (bulletRect.intersects(enemyRect)){
            isVisible = false;
            enemy.isVisible = false;
            return true;
        }else {
            return false;
        }
    }

    private void isInFrame(){
        if (y<0){
            isVisible = false;
        }
    }

    public void draw(Graphics g){
        g.drawImage(imgLaser, x,y,null);
    }

    @Override
    public void run() {
        while (isVisible) {
            move();
            isInFrame();
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
