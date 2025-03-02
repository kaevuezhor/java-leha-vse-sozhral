package ru.kaevuezhor.leha.game;

import ru.kaevuezhor.leha.food.Food;
import ru.kaevuezhor.leha.food.FoodManager;
import ru.kaevuezhor.leha.food.FoodType;
import ru.kaevuezhor.leha.player.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static ru.kaevuezhor.leha.game.GameConfig.FOOD_SIZE;

/**
 * Отрисовывает игровое состояние на экране
 */
public class GameRenderer extends JPanel {

    private Image playerImage;        // Изображение игрока
    private Image healthyImage;       // Здоровая еда
    private Image junkImage;          // Фастфуд
    private Image poisonImage;        // Яд
    private Image energyImage;        // Энергетик

    private Image backgroundImage; // Новое поле для фона

    private Player player;       // Ссылка на игрока
    private FoodManager foodManager; // Менеджер еды

    private long currentTime; // Текущее время для отображения


    /**
     * Конструктор инициализирует панель отрисовки
     */
    public GameRenderer(Player player, FoodManager foodManager) {
        this.player = player;
        this.foodManager = foodManager;
        setPreferredSize(new Dimension(
                GameConfig.GAME_WIDTH,
                GameConfig.GAME_HEIGHT
        ));

        try {
            backgroundImage = ImageIO.read(getClass().getResource("/background.png"));
            playerImage = ImageIO.read(getClass().getResource("/leha.png"));
            healthyImage = ImageIO.read(getClass().getResource("/healthy.png"));
            junkImage = ImageIO.read(getClass().getResource("/junk.png"));
            poisonImage = ImageIO.read(getClass().getResource("/poison.png"));
            energyImage = ImageIO.read(getClass().getResource("/energy.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentTime(long time) {
        this.currentTime = time;
    }

    /**
     * Основной метод отрисовки компонентов
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderBackground(g);
        renderFood(g);
        renderPlayer(g);
        renderUI(g);
        drawTimer(g);
    }

    // Отрисовка фона
    private void renderBackground(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        if (backgroundImage != null) {
            int x = (GameConfig.GAME_WIDTH - backgroundImage.getWidth(null)) / 2;
            int y = (GameConfig.GAME_HEIGHT - backgroundImage.getHeight(null)) / 2;
            g2d.drawImage(backgroundImage, x, y, null);
        } else {
            // Запасной цвет
        }

        g2d.dispose();
    }

    // Отрисовка элементов еды
    private void renderFood(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        for (Food food : foodManager.getFoods()) {
            Image foodImage = switch (food.type) {
                case HEALTHY -> healthyImage;
                case JUNK -> junkImage;
                case POISON -> poisonImage;
                case ENERGY -> energyImage;
            };

            // Масштабируем изображение до нужного размера
            Image scaledImage = foodImage.getScaledInstance(FOOD_SIZE, FOOD_SIZE, Image.SCALE_SMOOTH);
            g2d.drawImage(scaledImage, food.x, food.y, null);
        }
        g2d.dispose();
    }

    // Возвращает цвет для разных типов еды
    private Color getFoodColor(FoodType type) {
        return switch(type) {
            case HEALTHY -> Color.GREEN;
            case JUNK -> Color.ORANGE;
            case POISON -> Color.RED;
            case ENERGY -> Color.YELLOW;
        };
    }

    // Отрисовка игрока
    private void renderPlayer(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        Point pos = player.getPosition();
        int size = player.getSize();

        // Масштабируем изображение
        Image scaled = playerImage.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        g2d.drawImage(scaled, pos.x, pos.y, null);
        g2d.dispose();
    }

    // Отрисовка интерфейса пользователя
    private void renderUI(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));

        // Калории
        g.drawString("Калории: " + player.getCalories() +
                "/" + GameConfig.WIN_CALORIES, 10, 20);

        // Жизни
        g.drawString("Жизни: " + player.getLives(), 10, 40);

        // Скорость
        g.drawString("Скорость: " +
                (int)(player.getSpeedMultiplier()*100) + "%", 10, 60);

        // Размер
        g.drawString("Размер: " + player.getSize(), 10, 80);
    }

    private void drawTimer(Graphics g) {
        int seconds = (int) (currentTime / 1000);
        int millis = (int) (currentTime % 1000);
        String timeText = String.format("%02d.%03d", seconds, millis);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));

        FontMetrics metrics = g.getFontMetrics();
        int x = getWidth() - metrics.stringWidth(timeText) - 20; // Отступ справа 20px
        int y = metrics.getHeight() + 20; // Отступ сверху 20px

        g.drawString(timeText, x, y);
    }
}