package ru.kaevuezhor.leha.game;

import ru.kaevuezhor.leha.LehaGame;

import javax.swing.*;
import java.awt.event.KeyListener;

public class GameUIManager {
    private final JFrame frame;
    private final GameRenderer renderer;
    private Timer gameTimer;

    public GameUIManager(JFrame mainFrame, GameRenderer renderer) {
        this.frame = mainFrame; // Принимаем существующий фрейм
        this.renderer = renderer;
        initializeUI();
    }

    private void initializeUI() {
        frame.add(renderer);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
    }

    private void initializeFrame() {
        frame.setTitle("Леха всё сожрал!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
    }

    public void showGame() {
        frame.setVisible(true);
    }

    public void startGameLoop(Runnable gameUpdate) {
        gameTimer = new Timer(GameConfig.GAME_LOOP_DELAY, e -> gameUpdate.run());
        gameTimer.start();
    }

    public void showGameOverDialog(String message, Runnable onRestart) {
        JOptionPane.showOptionDialog(
                frame,
                message,
                "Игра окончена",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{"Новая игра", "Выход"},
                0
        );
    }

    // Добавляем метод для настройки ввода
    public void setupInput(InputHandler inputHandler) {
        if (inputHandler != null) {
            frame.addKeyListener(inputHandler); // Добавляем обработчик напрямую к основному фрейму
        }
    }

    public void clearInputListeners() {
        for (KeyListener listener : frame.getKeyListeners()) {
            frame.removeKeyListener(listener);
        }
    }

    public void requestGameFocus() {
        frame.requestFocusInWindow();
    }
}