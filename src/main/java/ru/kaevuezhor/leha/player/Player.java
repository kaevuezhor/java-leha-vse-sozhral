package ru.kaevuezhor.leha.player;

import ru.kaevuezhor.leha.game.GameConfig;
import ru.kaevuezhor.leha.game.SoundManager;

import java.awt.Point;

/**
 * Класс представляет игрока и управляет его состоянием
 */
public class Player {
    private Point position;       // Текущая позиция на поле
    private int size = GameConfig.PLAYER_START_SIZE;             // Текущий размер (диаметр)
    private int calories = GameConfig.PLAYER_START_CALORIES;         // Текущее количество калорий
    private int lives = GameConfig.PLAYER_START_LIVES;            // Количество жизней
    private float speedMultiplier; // Множитель скорости движения
    /**
     * Конструктор инициализирует игрока
     * @param startX Начальная X-координата
     * @param startY Начальная Y-координата
     */
    public Player(int startX, int startY) {
        position = new Point(startX, startY);
        reset();
    }

    /**
     * Обновляет множитель скорости на основе текущего размера
     */
    public void updateSpeed() {
        float sizeRatio = (float)(size - GameConfig.PLAYER_MIN_SIZE) /
                (GameConfig.PLAYER_MAX_SIZE - GameConfig.PLAYER_MIN_SIZE);
        speedMultiplier = 1.0f - sizeRatio * GameConfig.PLAYER_SPEED_REDUCTION;
    }

    /**
     * Сбрасывает состояние игрока к начальным значениям
     */
    public void reset() {
        position.setLocation(
                GameConfig.GAME_WIDTH/2,
                GameConfig.GAME_HEIGHT/2
        );
        size = GameConfig.PLAYER_START_SIZE;
        calories = GameConfig.PLAYER_START_CALORIES;
        lives = GameConfig.PLAYER_START_LIVES;
        updateSpeed();
    }

    // Getters and Setters
    public Point getPosition() { return position; }
    public int getSize() { return size; }
    public int getCalories() { return calories; }
    public int getLives() { return lives; }
    public float getSpeedMultiplier() { return speedMultiplier; }

    public void setSize(int size) { this.size = size; }
    public void setCalories(int calories) { this.calories = calories; }
    public void setLives(int lives) { this.lives = lives; }

    /**
     * Изменяет количество калорий
     * @param delta Значение изменения (+/-)
     */
    public void modifyCalories(int delta) { calories += delta; }

    /**
     * Изменяет количество жизней
     * @param delta Значение изменения (+/-)
     */
    public void modifyLives(int delta) { lives += delta; }

    /**
     * Обрабатывает эффект отравления при нулевых жизнях
     */
    public void handlePoisoning(SoundManager sound) {
        if (lives <= 0) {
            sound.playPoisonSound();
            modifyCalories(-GameConfig.POISON_CALORIES_PENALTY);
            lives = GameConfig.PLAYER_START_LIVES; // Сброс жизней
        }
    }
}