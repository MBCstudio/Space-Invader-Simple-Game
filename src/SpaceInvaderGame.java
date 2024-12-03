import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SpaceInvaderGame extends JFrame implements KeyListener, Runnable{
    //ustanienia planszy
    private static final int WIDTH = 500;
    private static final int HEIGHT = 700;

    //obsługa przyciskow
    private boolean[] keys;

    //uczestnicy gry
    private Player player;
    private List<Enemy> enemies;
    private List<Laser> lasers;
    private Scoreboard scoreboard;

    //podwojne buforowanie
    private BufferedImage buffer;

    //watki
    private Thread playerThread;
    private Thread enemiesSpawnThread; //watek do doadawania nowych przeciwnikow
    private Thread bulletsTherad; //watek to działania buletów

    //ustawniena gry
    private boolean isRunning = true;
    private int score =0;
    private int newEnemiesTimeSpawn = 1000;

    //icons & bacground
    private Image bgImage;

    public SpaceInvaderGame(){
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //Center app
        setLocationRelativeTo(null);

        keys = new boolean[256];
        buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        try{
            //getting bg img
            bgImage = ImageIO.read(new File("C:\\Users\\cinek\\Documents\\JavaGame\\Icons\\space-invader-bg.jpg"));
        }catch (IOException e){
            e.printStackTrace();
        }

        try{
            //getting img
            BufferedImage image = ImageIO.read(new File("C:\\Users\\cinek\\Documents\\JavaGame\\Icons\\space-invaders.png"));
            //creating player
            player = new Player(WIDTH/2-16, HEIGHT-50, image.getWidth(), image.getHeight(), image);
        }catch (IOException e){
            e.printStackTrace();
        }

        scoreboard = new Scoreboard();

        //uzywamy schnchronizowanej listy w celu zabezpiecznie watków (modifikacja listy na ktorej sa watki w trakcji iterowania po niej)
        enemies = Collections.synchronizedList(new ArrayList<>());
        lasers = Collections.synchronizedList(new ArrayList<>());

        addKeyListener(this);
        setFocusable(true);

        playerThread = new Thread(this::movePlayer);
        enemiesSpawnThread = new Thread(this::spawnEnemies);
        bulletsTherad = new Thread(this::shotBullets);

        playerThread.start();
        enemiesSpawnThread.start();
        bulletsTherad.start();
    }

    private void movePlayer(){
        while (isRunning){
            if (keys[KeyEvent.VK_RIGHT]){
                player.moveRight(WIDTH);
            }
            if (keys[KeyEvent.VK_LEFT]){
                player.moveLetf();
            }

            try{
                Thread.sleep(10);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void spawnEnemies(){
        while (isRunning){
            spawnNewEnimies();
            score++;
            scoreboard.setScoreboardScore(score);

            try {
                Thread.sleep(newEnemiesTimeSpawn);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void spawnNewEnimies(){
        //losowanie przeciwnika
        Random random = new Random();
        int enemyType = random.nextInt(2);

        if (enemyType == 0){
            //kordynaty do losowania umiejscowienia
            int x = 30;
            int y = WIDTH - 30;
            FastEnemy fastEnemy = new FastEnemy(random.nextInt(y-x+1)+x, 0);
            enemies.add(fastEnemy);
            Thread fastEnemyThread = new Thread(fastEnemy);
            fastEnemyThread.start();
        }else {
            //kordynaty do losowania umiejscowienia
            int x = 45;
            int y = WIDTH - 45;
            SlowEnemy slowEnemy = new SlowEnemy(random.nextInt(y-x+1)+x, 0);
            enemies.add(slowEnemy);
            Thread slowEnemyThread = new Thread(slowEnemy);
            slowEnemyThread.start();
        }
    }

    private void checkCollision(){
        synchronized (enemies){
            for (Enemy enemy:enemies){
                boolean isCollison = enemy.checkCollicion(player);
                if (isCollison == true){
                    //isRunning = true;
                    isRunning = false;
                }
            }
        }
    }

    private void shotBullets(){
        while (isRunning){
            if (keys[KeyEvent.VK_SPACE]){
                spawnBullets();
            }

            try{
                Thread.sleep(100);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void isBulletHitRock(){
        //synchronizujemy arraylisty ponieważ musimy kontorlować działanie na wątkach
        synchronized (enemies) {
            synchronized (lasers) {
                Iterator<Enemy> enemyIterator = enemies.iterator();
                while (enemyIterator.hasNext()) {
                    Enemy enemy = enemyIterator.next();

                    Iterator<Laser> laserIterator = lasers.iterator();

                    while (laserIterator.hasNext()) {
                        Laser laser = laserIterator.next();
                        boolean isHit = laser.collisionBulletRock(enemy);
                        if (isHit) {
                            enemyIterator.remove();  // Usunięcie wroga
                            laserIterator.remove();  // Usunięcie pocisku
                            score += 2;
                            scoreboard.setScoreboardScore(score);
                        }
                    }
                }
            }
        }
    }

    private void spawnBullets(){
        //TO DO dokonczyc wystrzeliwanie laserów w srodku tej klasy dodac ikone strzalu)
        try{
            //getting img
            BufferedImage image = ImageIO.read(new File("C:\\Users\\cinek\\Documents\\JavaGame\\Icons\\paintball.png"));
            //creating player
            Laser laser = new Laser(player.getX(), player.getY(), image, image.getWidth(), image.getHeight());
            lasers.add(laser);
            Thread laserThread = new Thread(laser);
            laserThread.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void changeSpeedOfRocks(){
        int change = (int) ((scoreboard.getElapsedTime()/1000)/10);
        newEnemiesTimeSpawn -=change;
        if (newEnemiesTimeSpawn < 600){
            newEnemiesTimeSpawn = 1000;
        }
        //System.out.println(change);
    }

    @Override
    public void paint(Graphics g){
        //podmiana bufforów (buffor swaping)
        renderToBuffer(); //renderujujemy bufor
        g.drawImage(buffer,0,0,this); //podmieniamy buffor
    }

    private void renderToBuffer(){
        //renderowanie 'tylnego' bufora
        Graphics2D g = buffer.createGraphics();

        //settings of bg
        g.drawImage(bgImage,0,0, WIDTH,HEIGHT,this);

        //draw player
        player.draw(g);
        //g.drawRect(player.getX(), player.getY(), player.getIconX(), player.getIconY());

        //draw enemy
        for (Enemy enemy : enemies){
            enemy.draw(g);
            //g.drawRect(enemy.x, enemy.y, enemy.getIconWidth(), enemy.getIconHeight());
        }

        //draw lares
        for (Laser laser : lasers){
            laser.draw(g);
        }

        //draw scoreboard
        scoreboard.draw(g);

        g.dispose();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //kiedy przyciskamy mozemy pobierac informacje
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{
            SpaceInvaderGame game = new SpaceInvaderGame();
            game.setVisible(true);
            new Thread(game).start();
        });
    }

    @Override
    public void run() {
        while (isRunning) {
            checkCollision();
            isBulletHitRock();
            changeSpeedOfRocks();
            //repaint aby zaktualizować komponent
            repaint();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (isRunning == false){
            int answer = JOptionPane.showConfirmDialog(this,
                    "Czy chcesz zagrać jeszcze raz?",
                    "Koniec gry!",
                    JOptionPane.YES_NO_OPTION);

            if (answer == JOptionPane.YES_NO_OPTION){
                SwingUtilities.invokeLater(() ->{
                    SpaceInvaderGame game = new SpaceInvaderGame();
                    game.setVisible(true);
                    new Thread(game).start();
                });
            }else {
               dispose();
            }
        }
    }
}

