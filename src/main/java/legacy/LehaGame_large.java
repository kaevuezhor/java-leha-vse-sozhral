package legacy;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class LehaGame_large extends JPanel implements KeyListener, Runnable {
    private int totalCalories;
    private int lives;
    private int playerSize;
    private ArrayList<Food> foods;
    private Point playerPos;
    private Random rand;
    private long startTime;
    private float speedMultiplier;
    private static final int MAX_SIZE = 100;
    private static final int MIN_SIZE = 30;
    private volatile boolean gameRunning;
    private JFrame parentFrame;
    private Clip eatSound;

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
            if (r < 0.25f) return FoodType.HEALTHY;
            if (r < 0.50f) return FoodType.JUNK;
            if (r < 0.65f) return FoodType.ANTIDOTE;
            if (r < 0.85f) return FoodType.ENERGY;
            return FoodType.POISON;
        }
    }

    enum FoodType { HEALTHY, JUNK, POISON, ENERGY, ANTIDOTE }

    public LehaGame_large(JFrame frame) {
        this.parentFrame = frame;
        loadSounds();
        initializeGame();
        setPreferredSize(new Dimension(800, 600));
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
    }

    private void loadSounds() {
        try {
            URL soundUrl = getClass().getResource("/eat.wav");
            if (soundUrl == null) throw new IOException("Файл звука не найден!");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundUrl);
            eatSound = AudioSystem.getClip();
            eatSound.open(audioIn);
        } catch (Exception e) {
            System.out.println("Ошибка звука: " + e.getMessage());
        }
    }

    private void playEatSound() {
        if (eatSound != null) {
            eatSound.setFramePosition(0);
            eatSound.start();
        }
    }

    private void initializeGame() {
        totalCalories = 1000;
        lives = 3;
        playerSize = 50;
        foods = new ArrayList<>();
        playerPos = new Point(400, 300);
        rand = new Random();
        startTime = System.currentTimeMillis();
        speedMultiplier = 1.0f;
        gameRunning = true;
        updateSpeed();
        spawnFood(10);
    }

    private void updateSpeed() {
        float sizeRatio = (float)(playerSize - MIN_SIZE) / (MAX_SIZE - MIN_SIZE);
        speedMultiplier = 1.0f - sizeRatio * 0.8f;
    }

    public void startNewGame() {
        gameRunning = false;
        removeKeyListener(this);
        initializeGame();
        addKeyListener(this);
        new Thread(this).start();
    }

    private void spawnFood(int count) {
        for (int i = 0; i < count; i++) {
            foods.add(new Food());
        }
    }

    @Override
    public void run() {
        while (gameRunning && totalCalories > 0 && lives > 0
                && totalCalories < 3000) { // Убрано условие размера из цикла
            checkCollisions();
            checkTime();
            repaint();
            try { Thread.sleep(50); }
            catch (InterruptedException e) {}
        }
        showGameOver();
    }

    private void checkCollisions() {
        Rectangle playerRect = new Rectangle(
                playerPos.x, playerPos.y, playerSize, playerSize
        );

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

    private void applyFoodEffect(FoodType type) {
        switch (type) {
            case HEALTHY -> {
                totalCalories += 150;
                playerSize += 2;
            }
            case JUNK -> {
                totalCalories += 250;
                playerSize += 15;
            }
            case POISON -> {
                lives = Math.max(0, lives - 1);
                playerSize = Math.max(MIN_SIZE, playerSize - 20);
            }
            case ENERGY -> totalCalories += 600;
            case ANTIDOTE -> lives = Math.min(3, lives + 1);
        }
        playerSize = Math.min(MAX_SIZE, playerSize);
        playerSize = Math.max(MIN_SIZE, playerSize);

        // Непосредственная проверка размера после изменений
        if (playerSize >= MAX_SIZE) {
            gameRunning = false;
        }

        updateSpeed();
    }

    private void checkTime() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        if (elapsed >= 180) lives = 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(30, 30, 70));
        g.fillRect(0, 0, 800, 600);

        for (Food food : foods) {
            switch (food.type) {
                case HEALTHY -> g.setColor(Color.GREEN);
                case JUNK -> g.setColor(Color.ORANGE);
                case POISON -> g.setColor(Color.RED);
                case ENERGY -> g.setColor(Color.YELLOW);
                case ANTIDOTE -> g.setColor(Color.WHITE);
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

    private void showGameOver() {
        gameRunning = false;
        String message;
        if (playerSize >= MAX_SIZE) {
            message = "Леха лопнул!";
        } else if (totalCalories >= 3000) {
            message = "Лёха все сожрал!";
        } else if (totalCalories <= 0) {
            message = "Лёха устал и уснул";
        } else {
            message = "Леха проиграл";
        }

        int choice = JOptionPane.showOptionDialog(parentFrame,
                message, "Игра окончена", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null,
                new Object[]{"Новая игра", "Выход"}, 0);

        if (choice == 0) startNewGame();
        else parentFrame.dispose();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int step = (int)(15 * speedMultiplier);
        int calorieCost = 2;

        if (totalCalories >= calorieCost) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    playerPos.x = Math.max(0, playerPos.x - step);
                    totalCalories -= calorieCost;
                }
                case KeyEvent.VK_RIGHT -> {
                    playerPos.x = Math.min(760 - playerSize, playerPos.x + step);
                    totalCalories -= calorieCost;
                }
                case KeyEvent.VK_UP -> {
                    playerPos.y = Math.max(0, playerPos.y - step);
                    totalCalories -= calorieCost;
                }
                case KeyEvent.VK_DOWN -> {
                    playerPos.y = Math.min(560 - playerSize, playerPos.y + step);
                    totalCalories -= calorieCost;
                }
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Леха всё сожрал!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        LehaGame_large game = new LehaGame_large(frame);
        frame.add(game);
        frame.pack();
        frame.setVisible(true);
        game.startNewGame();
    }
}