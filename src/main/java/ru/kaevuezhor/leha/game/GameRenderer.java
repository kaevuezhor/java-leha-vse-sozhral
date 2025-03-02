package ru.kaevuezhor.leha.game;

import ru.kaevuezhor.leha.food.Food;
import ru.kaevuezhor.leha.food.FoodManager;
import ru.kaevuezhor.leha.food.FoodType;
import ru.kaevuezhor.leha.player.Player;

import javax.swing.*;
import java.awt.*;

import javax.swing.*;
import java.awt.*;

/**
 * Отрисовывает игровое состояние на экране
 */
public class GameRenderer extends JPanel {
    private Player player;       // Ссылка на игрока
    private FoodManager foodManager; // Менеджер еды

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
    }

    // Отрисовка фона
    private void renderBackground(Graphics g) {
        g.setColor(new Color(30, 30, 70));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    // Отрисовка элементов еды
    private void renderFood(Graphics g) {
        for(Food food : foodManager.getFoods()) {
            g.setColor(getFoodColor(food.type));
            g.fillRect(food.x, food.y,
                    GameConfig.FOOD_SIZE,
                    GameConfig.FOOD_SIZE
            );
        }
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
        g.setColor(Color.ORANGE);
        Point pos = player.getPosition();
        int size = player.getSize();
        g.fillOval(pos.x, pos.y, size, size);
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
}