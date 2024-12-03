import java.awt.*;

public class Scoreboard {
    private long startTime;
    private int scoreboardScore = 0;

    public Scoreboard() {
        startTime = System.currentTimeMillis();
    }

    public void setScoreboardScore(int scoreboardScore) {
        this.scoreboardScore = scoreboardScore;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Score: " + scoreboardScore , 10, 50);
        g.drawString("Time: " + (getElapsedTime() / 1000) + "s", 10, 70);
    }
}
