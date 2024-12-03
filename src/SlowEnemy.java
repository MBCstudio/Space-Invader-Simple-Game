import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SlowEnemy extends Enemy{
    public SlowEnemy(int x, int y) {

        super(x, y, 1, 64,64,"C:\\Users\\cinek\\Documents\\JavaGame\\Icons\\rock_1.png");
    }

    @Override
    public void move() {
        y+=speed;
    }
}
