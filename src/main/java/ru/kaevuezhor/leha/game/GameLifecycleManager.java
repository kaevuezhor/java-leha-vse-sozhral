package ru.kaevuezhor.leha.game;

import ru.kaevuezhor.leha.food.FoodManager;
import ru.kaevuezhor.leha.player.Player;

public class GameLifecycleManager {
    private GameEngine engine;
    private Player player;
    private FoodManager foodManager;
    private SoundManager soundManager;

    public GameLifecycleManager(Player player,
                                FoodManager foodManager,
                                SoundManager soundManager) {
        this.player = player;
        this.foodManager = foodManager;
        this.soundManager = soundManager;
    }

    public void startGame() {
        stopGame();
        engine = new GameEngine(player, foodManager, soundManager);
        new Thread(engine::startGame).start();
        soundManager.playBackgroundMusic();
    }

    public void stopGame() {
        if (engine != null) {
            engine.stopGame();
        }
        soundManager.stopBackgroundMusic();
    }

    public void restartGame() {
        stopGame();
        player.reset();
        foodManager.reset();
        startGame();
    }
}