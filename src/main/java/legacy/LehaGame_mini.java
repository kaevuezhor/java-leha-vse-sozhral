package legacy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class LehaGame_mini extends JPanel implements KeyListener, Runnable {
    private int totalCalories = 1000;
    private int lives = 3;
    private int playerSize = 30;
    private boolean isTurbo = false;
    private ArrayList<Food> foods = new ArrayList<>();
    private Point playerPos = new Point(400, 300);
    private Random rand = new Random();
    private long startTime = System.currentTimeMillis();

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
            if (r < 0.35f) return FoodType.HEALTHY;    // 35%
            if (r < 0.70f) return FoodType.JUNK;       // 35%
            if (r < 0.80f) return FoodType.ANTIDOTE;   // 10%
            if (r < 0.95f) return FoodType.ENERGY;     // 15%
            return FoodType.POISON;                    // 5%
        }
    }

    enum FoodType { HEALTHY, JUNK, POISON, ENERGY, ANTIDOTE }

    public LehaGame_mini() {
        setPreferredSize(new Dimension(800, 600));
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
        spawnFood(10);
        new Thread(this).start();
    }

    private void spawnFood(int count) {
        for (int i = 0; i < count; i++) {
            foods.add(new Food());
        }
    }

    @Override
    public void run() {
        while (totalCalories > 0 && lives > 0 && totalCalories < 3000) {
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
                foods.remove(i);
            }
        }

        if (foods.size() < 5) spawnFood(3);
    }

    private void applyFoodEffect(FoodType type) {
        switch (type) {
            case HEALTHY:
                totalCalories += 150;
                playerSize += 2;
                break;
            case JUNK:
                totalCalories += 250;
                break;
            case POISON:
                totalCalories += 400;
                break;
            case ENERGY:
                isTurbo = true;
                new Timer(3000, e -> isTurbo = false).start();
                break;
            case ANTIDOTE:
                lives = Math.min(3, lives + 1);
                break;
        }
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

        // Рисуем еду
        for (Food food : foods) {
            switch (food.type) {
                case HEALTHY: g.setColor(Color.GREEN); break;
                case JUNK: g.setColor(Color.ORANGE); break;
                case POISON: g.setColor(Color.RED); break;
                case ENERGY: g.setColor(Color.YELLOW); break;
                case ANTIDOTE: g.setColor(Color.WHITE); break;
            }
            g.fillRect(food.x, food.y, 20, 20);
        }

        // Рисуем Леху
        g.setColor(Color.ORANGE);
        g.fillOval(playerPos.x, playerPos.y, playerSize, playerSize);

        // Рисуем интерфейс
        g.setColor(Color.WHITE);
        g.drawString("Калории: " + totalCalories + "/3000", 10, 20);
        g.drawString("Жизни: " + lives, 10, 40);
        g.drawString("Турбо: " + (isTurbo ? "АКТИВНО" : "выкл"), 10, 60);
    }

    private void showGameOver() {
        String message;
        if (totalCalories >= 3000) {
            message = "Лёха все сожрал!";
        } else if (totalCalories <= 0) {
            message = "Лёха устал и уснул";
        } else {
            message = "Леха лопнул :(";
        }

        JOptionPane.showMessageDialog(this, message, "Игра окончена", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int step = isTurbo ? 10 : 5;
        int calorieCost = isTurbo ? 3 : 1;

        if (totalCalories >= calorieCost) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    playerPos.x = Math.max(0, playerPos.x - step);
                    totalCalories -= calorieCost;
                    break;
                case KeyEvent.VK_RIGHT:
                    playerPos.x = Math.min(760, playerPos.x + step);
                    totalCalories -= calorieCost;
                    break;
                case KeyEvent.VK_UP:
                    playerPos.y = Math.max(0, playerPos.y - step);
                    totalCalories -= calorieCost;
                    break;
                case KeyEvent.VK_DOWN:
                    playerPos.y = Math.min(560, playerPos.y + step);
                    totalCalories -= calorieCost;
                    break;
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Леха всё сожрал!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new LehaGame_mini());
        frame.pack();
        frame.setVisible(true);
    }
}