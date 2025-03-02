package ru.kaevuezhor.leha.food;

import ru.kaevuezhor.leha.game.GameConfig;

import java.util.Random;

/**
 * Класс представляет объект еды на игровом поле
 */
public class Food {
    public int x;         // X-координата на поле
    public int y;         // Y-координата на поле
    public FoodType type; // Тип еды
    private Random rand;  // Генератор случайных чисел

    /**
     * Конструктор создает еду в случайной позиции
     * @param rand Общий генератор случайных чисел
     */
    public Food(Random rand) {
        this.rand = rand;
        // Генерация позиции с учетом размера еды
        x = rand.nextInt(GameConfig.GAME_WIDTH - GameConfig.FOOD_SIZE);
        y = rand.nextInt(GameConfig.GAME_HEIGHT - GameConfig.FOOD_SIZE);
        type = getRandomFoodType();
    }

    /**
     * Выбирает случайный тип еды на основе вероятностей из GameConfig
     */
    private FoodType getRandomFoodType() {
        float r = rand.nextFloat();
        // Используем обновленные вероятности
        if (r < GameConfig.FOOD_SPAWN_CHANCE_HEALTHY) return FoodType.HEALTHY;
        if (r < GameConfig.FOOD_SPAWN_CHANCE_HEALTHY + GameConfig.FOOD_SPAWN_CHANCE_JUNK) return FoodType.JUNK;
        if (r < GameConfig.FOOD_SPAWN_CHANCE_HEALTHY + GameConfig.FOOD_SPAWN_CHANCE_JUNK + GameConfig.FOOD_SPAWN_CHANCE_ENERGY)
            return FoodType.ENERGY;
        return FoodType.POISON;
    }
}