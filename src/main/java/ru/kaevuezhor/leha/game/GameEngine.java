package ru.kaevuezhor.leha.game;

import ru.kaevuezhor.leha.food.FoodManager;
import ru.kaevuezhor.leha.player.Player;
import ru.kaevuezhor.leha.sound.SoundManager;

/**
 * Управляет основным игровым процессом и логикой
 */
public class GameEngine implements Runnable {
    private Player player;        // Ссылка на объект игрока
    private FoodManager foodManager; // Менеджер еды
    private SoundManager soundManager; // Менеджер звуков
    private volatile boolean gameRunning; // Флаг активности игры

    /**
     * Конструктор инициализирует игровой движок
     */
    public GameEngine(Player player, FoodManager foodManager, SoundManager soundManager) {
        this.player = player;
        this.foodManager = foodManager;
        this.soundManager = soundManager;
    }

    /**
     * Запускает основной игровой цикл
     */
    public void startGame() {
        gameRunning = true;
        while(gameRunning && isGameActive()) {
            updateGameState();
            sleepForNextFrame();
        }
    }

    public void stopGame() {
        gameRunning = false;
    }

    @Override
    public void run() {
        while (gameRunning && !Thread.currentThread().isInterrupted()) {
            updateGameState();
            sleepForNextFrame();
        }
    }

    // Обновляет состояние игры
    private void updateGameState() {
        foodManager.checkCollisions(player, soundManager);
        checkGameOverConditions();
    }

    // Проверяет условия продолжения игры
    private boolean isGameActive() {
        return player.getCalories() > 0 &&
                player.getCalories() < GameConfig.WIN_CALORIES &&
                player.getSize() < GameConfig.PLAYER_MAX_SIZE;
    }

    // Проверяет условия завершения игры
    private void checkGameOverConditions() {
        if (player.getSize() >= GameConfig.PLAYER_MAX_SIZE ||
                player.getCalories() <= 0 ||
                player.getCalories() >= GameConfig.WIN_CALORIES) {
            gameRunning = false;
        }
    }

    // Задержка между кадрами
    private void sleepForNextFrame() {
        try {
            Thread.sleep(GameConfig.GAME_LOOP_DELAY);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * @return Статус работы игрового движка
     */
    public boolean isGameRunning() {
        return gameRunning;
    }
}