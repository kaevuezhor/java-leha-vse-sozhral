package ru.kaevuezhor.leha.game;

/**
 * Класс содержит все настройки и константы игры для удобной балансировки
 */
public class GameConfig {
    // Вероятности появления типов еды (в сумме должны давать 1.0)
    public static final float FOOD_SPAWN_CHANCE_HEALTHY = 0.50f; // 50%
    public static final float FOOD_SPAWN_CHANCE_JUNK = 0.25f;    // 25%
    public static final float FOOD_SPAWN_CHANCE_ENERGY = 0.15f;  // 15%
    public static final float FOOD_SPAWN_CHANCE_POISON = 0.10f;  // 10%

    // Начальные параметры игрока
    public static final int PLAYER_START_CALORIES = 1000; // Стартовые калории
    public static final int PLAYER_START_SIZE = 50;       // Начальный размер
    public static final int PLAYER_MIN_SIZE = 30;         // Минимальный размер
    public static final int PLAYER_MAX_SIZE = 100;        // Максимальный размер
    public static final int PLAYER_START_LIVES = 3;       // Количество жизней
    public static final float PLAYER_SPEED_REDUCTION = 0.8f; // Снижение скорости при росте

    // Эффекты от不同类型的 еды
    public static final int HEALTHY_FOOD_CALORIES = 150; // Калории здоровой еды
    public static final int HEALTHY_FOOD_SIZE = 2;       // Увеличение размера

    public static final int JUNK_FOOD_CALORIES = 180;    // Калории фастфуда
    public static final int JUNK_FOOD_SIZE = 25;         // Увеличение размера

    public static final int ENERGY_FOOD_CALORIES = 300;  // Калории энергетика

    public static final int POISON_SIZE_PENALTY = 30;     // Уменьшение размера от яда
    public static final int POISON_CALORIES_PENALTY = 1000; // Потеря калорий при отравлении
    public static final int POISON_LIVES_PENALTY = 1; // -1 жизнь за яд

    // Параметры управления
    public static final int BASE_MOVEMENT_STEP = 15;     // Базовый шаг движения
    public static int MOVEMENT_COST = 2;           // Стоимость калорий за шаг

    // Размеры игрового поля
    public static final int GAME_WIDTH = 800;            // Ширина экрана
    public static final int GAME_HEIGHT = 600;           // Высота экрана
    public static final int FOOD_SIZE = 20;              // Размер элемента еды

    // Настройки генерации еды
    public static final int INITIAL_FOOD_COUNT = 10;     // Еда при старте
    public static final int MIN_FOOD_ON_SCREEN = 5;      // Минимум еды на экране
    public static final int FOOD_RESPAWN_COUNT = 3;      // Количество при респавне

    // Тайминги (в миллисекундах)
    public static final int GAME_LOOP_DELAY = 50;        // Задержка игрового цикла
    public static final int GAME_OVER_DELAY = 1000;      // Задержка перед Game Over

    // Условия победы
    public static final int WIN_CALORIES = 3000;         // Калории для победы
    // Добавляем динамические настройки
    public static float VOLUME = 0.8f;
    public static boolean SOUND_ENABLED = true;
    public static float POISON_SPAWN_CHANCE = 0.10f;

    // Модифицируем вероятность появления яда
    public static float getFoodSpawnChancePoison() {
        return POISON_SPAWN_CHANCE;
    }
}