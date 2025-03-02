package ru.kaevuezhor.leha.food;

import ru.kaevuezhor.leha.game.GameConfig;
import ru.kaevuezhor.leha.sound.SoundManager;
import ru.kaevuezhor.leha.player.Player;

import java.awt.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Управляет генерацией и взаимодействием еды на игровом поле
 */
public class FoodManager {
    private ArrayList<Food> foods; // Список активной еды
    private Random rand;           // Генератор случайных чисел

    /**
     * Конструктор инициализирует менеджер еды
     */
    public FoodManager() {
        foods = new ArrayList<>();
        rand = new Random();
    }

    /**
     * Создает указанное количество еды
     * @param count Количество элементов для создания
     */
    public void spawnFood(int count) {
        for(int i = 0; i < count; i++) {
            foods.add(new Food(rand));
        }
    }

    /**
     * Сбрасывает состояние к начальному
     */
    public void reset() {
        foods.clear();
        spawnFood(GameConfig.INITIAL_FOOD_COUNT);
    }

    /**
     * Проверяет столкновения игрока с едой
     * @param player Объект игрока
     * @param sound Менеджер звуков для воспроизведения эффектов
     */
    public void checkCollisions(Player player, SoundManager sound) {
        Rectangle playerRect = createPlayerRectangle(player);
        for(int i = foods.size()-1; i >= 0; i--) {
            Food food = foods.get(i);
            if(checkCollision(playerRect, food)) {
                applyFoodEffect(food.type, player, sound);
                foods.remove(i);
            }
        }
        maintainMinimumFood();
    }

    // Создает прямоугольник для проверки коллизий игрока
    private Rectangle createPlayerRectangle(Player player) {
        return new Rectangle(
                player.getPosition().x,
                player.getPosition().y,
                player.getSize(),
                player.getSize()
        );
    }

    // Проверяет пересечение с конкретным элементом еды
    private boolean checkCollision(Rectangle playerRect, Food food) {
        Rectangle foodRect = new Rectangle(
                food.x,
                food.y,
                GameConfig.FOOD_SIZE,
                GameConfig.FOOD_SIZE
        );
        return playerRect.intersects(foodRect);
    }

    // Поддерживает минимальное количество еды на экране
    private void maintainMinimumFood() {
        if(foods.size() < GameConfig.MIN_FOOD_ON_SCREEN) {
            spawnFood(GameConfig.FOOD_RESPAWN_COUNT);
        }
    }

    // Применяет эффект от съеденной еды
    private void applyFoodEffect(FoodType type, Player player, SoundManager sound) {
        switch (type) {
            case HEALTHY -> handleHealthyFood(player, sound);
            case JUNK -> handleJunkFood(player, sound);
            case POISON -> handlePoisonFood(player, sound);
            case ENERGY -> handleEnergyFood(player, sound);
        }
        player.setSize(Math.min(GameConfig.PLAYER_MAX_SIZE, player.getSize()));
    }

    // Обработчики для каждого типа еды
    private void handleHealthyFood(Player player, SoundManager sound) {
        player.modifyCalories(GameConfig.HEALTHY_FOOD_CALORIES);
        player.setSize(player.getSize() + GameConfig.HEALTHY_FOOD_SIZE);
        sound.playEatSound();
        sound.playHealthySound();
    }

    private void handleJunkFood(Player player, SoundManager sound) {
        player.modifyCalories(GameConfig.JUNK_FOOD_CALORIES);
        player.setSize(player.getSize() + GameConfig.JUNK_FOOD_SIZE);
        sound.playEatSound();
        sound.playJunkSound();
    }

    private void handlePoisonFood(Player player, SoundManager sound) {
        player.modifyLives(-GameConfig.POISON_LIVES_PENALTY);
        player.setSize(Math.max(
                GameConfig.PLAYER_MIN_SIZE,
                player.getSize() - GameConfig.POISON_SIZE_PENALTY
        ));
        if (player.getLives() > 0) {
            sound.playRegularPoisonSound();
        }
        player.handlePoisoning(sound); // Добавляем вызов обработки
    }

    private void handleEnergyFood(Player player, SoundManager sound) {
        player.modifyCalories(GameConfig.ENERGY_FOOD_CALORIES);
        sound.playEatSound();
        sound.playEnergySound();
    }

    /**
     * @return Текущий список еды на поле
     */
    public ArrayList<Food> getFoods() {
        return foods;
    }
}