package ru.kaevuezhor.leha.game;

import ru.kaevuezhor.leha.player.Player;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Обрабатывает пользовательский ввод с клавиатуры
 */
public class InputHandler extends KeyAdapter {
    private Player player; // Ссылка на игрока для управления

    /**
     * Конструктор связывает обработчик с игроком
     */
    public InputHandler(Player player) {
        this.player = player;
    }

    /**
     * Обрабатывает нажатия клавиш
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if(canMove()) {
            handleMovement(e.getKeyCode());
        }
    }

    // Проверяет возможность движения
    private boolean canMove() {
        return player.getCalories() >= GameConfig.MOVEMENT_COST;
    }

    // Обрабатывает перемещение в зависимости от нажатой клавиши
    private void handleMovement(int keyCode) {
        int step = calculateMovementStep();

        switch(keyCode) {
            case KeyEvent.VK_LEFT -> moveLeft(step);
            case KeyEvent.VK_RIGHT -> moveRight(step);
            case KeyEvent.VK_UP -> moveUp(step);
            case KeyEvent.VK_DOWN -> moveDown(step);
        }

        applyMovementCost();
    }

    // Рассчитывает шаг движения с учетом скорости
    private int calculateMovementStep() {
        return (int)(GameConfig.BASE_MOVEMENT_STEP *
                player.getSpeedMultiplier());
    }

    // Методы перемещения с проверкой границ
    private void moveLeft(int step) {
        player.getPosition().x = Math.max(0,
                player.getPosition().x - step);
    }

    private void moveRight(int step) {
        player.getPosition().x = Math.min(
                GameConfig.GAME_WIDTH - player.getSize(),
                player.getPosition().x + step
        );
    }

    private void moveUp(int step) {
        player.getPosition().y = Math.max(0,
                player.getPosition().y - step);
    }

    private void moveDown(int step) {
        player.getPosition().y = Math.min(
                GameConfig.GAME_HEIGHT - player.getSize(),
                player.getPosition().y + step
        );
    }

    // Вычитает калории за движение
    private void applyMovementCost() {
        player.modifyCalories(-GameConfig.MOVEMENT_COST);
    }
}