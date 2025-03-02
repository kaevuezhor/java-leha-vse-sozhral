package ru.kaevuezhor.leha.game;

import javax.swing.*;
import java.awt.event.KeyListener;

public class GameUIManager {
    private final JFrame frame;
    private final GameRenderer renderer;
    private Timer gameTimer;

    public GameUIManager(GameRenderer renderer) {
        this.renderer = renderer;
        this.frame = new JFrame("Леха всё сожрал!");
        initializeFrame();
    }

    private void initializeFrame() {
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
        if (inputHandler != null && frame != null) {
            frame.addKeyListener(inputHandler);
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
