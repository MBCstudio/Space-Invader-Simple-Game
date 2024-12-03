public class FastEnemy extends Enemy{
    public FastEnemy(int x, int y) {
        super(x, y, 3, 32,32,"C:\\Users\\cinek\\Documents\\JavaGame\\Icons\\rock_2.png");
    }

    @Override
    public void move() {
        y+= speed;
    }
}
