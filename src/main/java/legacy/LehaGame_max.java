package legacy;

import javazoom.jl.player.Player;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class LehaGame_max extends JPanel implements KeyListener, Runnable {
    private int totalCalories;
    private int lives;
    private int playerSize;
    private ArrayList<Food> foods;
    private Point playerPos;
    private Random rand;
    private float speedMultiplier;
    private static final int MAX_SIZE = 100;
    private static final int MIN_SIZE = 30;
    private volatile boolean gameRunning;
    private JFrame parentFrame;

    // Звуковые ресурсы
    private Clip eatSound;
    private Clip diareaSound;
    private Clip explosionSound;
    private Player bgmPlayer;
    private Thread musicThread;
    private volatile boolean musicRunning;

    private class Food {
        int x, y;
        FoodType type;

        Food() {
            x = rand.nextInt(750);
            y = rand.nextInt(550);
            type = getRandomFoodType();
        }

        private FoodType getRandomFoodType() {
            float r = rand.nextFloat();
            if (r < 0.35f) return FoodType.HEALTHY;
            if (r < 0.70f) return FoodType.JUNK;
            if (r < 0.85f) return FoodType.ENERGY;
            return FoodType.POISON;
        }
    }

    enum FoodType { HEALTHY, JUNK, POISON, ENERGY }

    public LehaGame_max(JFrame frame) {
        this.parentFrame = frame;
        loadSounds();
        initializeGame();
        setPreferredSize(new Dimension(800, 600));
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
    }

    // Инициализация игры
    private void initializeGame() {
        totalCalories = 1000;
        lives = 3;
        playerSize = 50;
        foods = new ArrayList<>();
        playerPos = new Point(400, 300);
        rand = new Random();
        speedMultiplier = 1.0f;
        gameRunning = true;
        updateSpeed();
        spawnFood(10);
    }

    // Загрузка звуков
    private void loadSounds() {
        try {
            // Звук еды
            URL eatSoundUrl = getClass().getResource("/eat.wav");
            AudioInputStream eatAudio = AudioSystem.getAudioInputStream(eatSoundUrl);
            eatSound = AudioSystem.getClip();
            eatSound.open(eatAudio);

            // Звук отравления
            URL diareaSoundUrl = getClass().getResource("/diarea.wav");
            AudioInputStream diareaAudio = AudioSystem.getAudioInputStream(diareaSoundUrl);
            diareaSound = AudioSystem.getClip();
            diareaSound.open(diareaAudio);

            // Звук взрыва
            URL explosionSoundUrl = getClass().getResource("/blup.wav");
            AudioInputStream explosionAudio = AudioSystem.getAudioInputStream(explosionSoundUrl);
            explosionSound = AudioSystem.getClip();
            explosionSound.open(explosionAudio);

        } catch (Exception e) {
            System.out.println("Ошибка загрузки звуков: " + e.getMessage());
        }
    }

    // Фоновая музыка
    private void playBackgroundMusic() {
        musicRunning = true;
        musicThread = new Thread(() -> {
            try {
                InputStream is = getClass().getResourceAsStream("/theme.mp3");
                while (musicRunning) {
                    bgmPlayer = new Player(is);
                    bgmPlayer.play();
                    is = getClass().getResourceAsStream("/theme.mp3");
                }
            } catch (Exception e) {
                System.out.println("Ошибка музыки: " + e.getMessage());
            }
        });
        musicThread.start();
    }

    private void stopBackgroundMusic() {
        musicRunning = false;
        if (bgmPlayer != null) bgmPlayer.close();
    }

    // Основной игровой цикл
    @Override
    public void run() {
        while (gameRunning && totalCalories > 0 && totalCalories < 3000) {
            checkCollisions();
            repaint();
            try { Thread.sleep(50); }
            catch (InterruptedException e) {}
        }
        showGameOver();
    }

    // Обработка столкновений
    private void checkCollisions() {
        Rectangle playerRect = new Rectangle(playerPos.x, playerPos.y, playerSize, playerSize);

        for (int i = foods.size()-1; i >= 0; i--) {
            Food food = foods.get(i);
            Rectangle foodRect = new Rectangle(food.x, food.y, 20, 20);

            if (playerRect.intersects(foodRect)) {
                applyFoodEffect(food.type);
                playEatSound();
                foods.remove(i);
            }
        }

        if (foods.size() < 5) spawnFood(3);
    }

    // Эффекты от еды
    private void applyFoodEffect(FoodType type) {
        switch (type) {
            case HEALTHY:
                totalCalories += 150;
                playerSize += 2;
                break;
            case JUNK:
                totalCalories += 180;
                playerSize += 25;
                break;
            case POISON:
                lives--;
                playerSize = Math.max(MIN_SIZE, playerSize - 30);
                if (lives <= 0) handlePoisoning();
                break;
            case ENERGY:
                totalCalories += 300;
                break;
        }
        playerSize = Math.min(MAX_SIZE, playerSize);
        if (playerSize >= MAX_SIZE) gameRunning = false;
        updateSpeed();
    }

    // Обработка отравления
    private void handlePoisoning() {
        playDiareaSound();
        JOptionPane.showMessageDialog(parentFrame, "Лёха получает отравление!", "Внимание!", JOptionPane.WARNING_MESSAGE);
        lives = 3;
        totalCalories = Math.max(0, totalCalories - 1000);
        if (totalCalories <= 0) gameRunning = false;
    }

    // Воспроизведение звуков
    private void playEatSound() {
        if (eatSound != null) {
            eatSound.setFramePosition(0);
            eatSound.start();
        }
    }

    private void playDiareaSound() {
        if (diareaSound != null) {
            diareaSound.setFramePosition(0);
            diareaSound.start();
        }
    }

    private void playExplosionSound() {
        if (explosionSound != null) {
            explosionSound.setFramePosition(0);
            explosionSound.start();
        }
    }

    // Отрисовка игры
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(30, 30, 70));
        g.fillRect(0, 0, 800, 600);

        for (Food food : foods) {
            switch (food.type) {
                case HEALTHY: g.setColor(Color.GREEN); break;
                case JUNK: g.setColor(Color.ORANGE); break;
                case POISON: g.setColor(Color.RED); break;
                case ENERGY: g.setColor(Color.YELLOW); break;
            }
            g.fillRect(food.x, food.y, 20, 20);
        }

        g.setColor(Color.ORANGE);
        g.fillOval(playerPos.x, playerPos.y, playerSize, playerSize);

        g.setColor(Color.WHITE);
        g.drawString("Калории: " + totalCalories + "/3000", 10, 20);
        g.drawString("Жизни: " + lives, 10, 40);
        g.drawString("Скорость: " + (int)(speedMultiplier*100) + "%", 10, 60);
        g.drawString("Размер: " + playerSize, 10, 80);
    }

    // Завершение игры
    private void showGameOver() {
        stopBackgroundMusic();
        gameRunning = false;

        String message;
        if (playerSize >= MAX_SIZE) {
            playExplosionSound();
            message = "Леха лопнул!";
        } else if (totalCalories >= 3000) {
            message = "Лёха все сожрал!";
        } else {
            message = "Лёха устал и уснул";
        }

        Timer timer = new Timer(1000, e -> {
            int choice = JOptionPane.showOptionDialog(parentFrame,
                    message, "Игра окончена",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, new Object[]{"Новая игра", "Выход"}, 0);

            if (choice == 0) startNewGame();
            else parentFrame.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Леха всё сожрал!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        LehaGame_max game = new LehaGame_max(frame);
        frame.add(game);
        frame.pack();
        frame.setVisible(true);
        game.playBackgroundMusic();
        game.startNewGame();
    }

    // Остальные методы
    private void updateSpeed() {
        float sizeRatio = (float)(playerSize - MIN_SIZE) / (MAX_SIZE - MIN_SIZE);
        speedMultiplier = 1.0f - sizeRatio * 0.8f;
    }

    public void startNewGame() {
        stopBackgroundMusic();
        removeKeyListener(this);
        initializeGame();
        addKeyListener(this);
        new Thread(this).start();
        playBackgroundMusic();
    }

    private void spawnFood(int count) {
        for (int i = 0; i < count; i++) {
            foods.add(new Food());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int step = (int)(15 * speedMultiplier);
        int calorieCost = 2;

        if (totalCalories >= calorieCost) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    playerPos.x = Math.max(0, playerPos.x - step);
                    totalCalories -= calorieCost;
                    break;
                case KeyEvent.VK_RIGHT:
                    playerPos.x = Math.min(760 - playerSize, playerPos.x + step);
                    totalCalories -= calorieCost;
                    break;
                case KeyEvent.VK_UP:
                    playerPos.y = Math.max(0, playerPos.y - step);
                    totalCalories -= calorieCost;
                    break;
                case KeyEvent.VK_DOWN:
                    playerPos.y = Math.min(560 - playerSize, playerPos.y + step);
                    totalCalories -= calorieCost;
                    break;
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}