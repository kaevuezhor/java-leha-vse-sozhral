package ru.kaevuezhor.leha;

import ru.kaevuezhor.leha.food.FoodManager;
import ru.kaevuezhor.leha.game.GameConfig;
import ru.kaevuezhor.leha.game.GameEngine;
import ru.kaevuezhor.leha.game.GameLifecycleManager;
import ru.kaevuezhor.leha.game.GameRenderer;
import ru.kaevuezhor.leha.game.GameUIManager;
import ru.kaevuezhor.leha.game.InputHandler;
import ru.kaevuezhor.leha.game.SettingsDialog;
import ru.kaevuezhor.leha.sound.SoundManager;
import ru.kaevuezhor.leha.player.Player;
import ru.kaevuezhor.leha.reply.LehaHaiku;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

public class LehaGame extends JFrame {
    private static final Logger logger = Logger.getLogger(LehaGame.class.getName());
    private GameLifecycleManager lifecycleManager;
    private GameUIManager uiManager;
    private GameEngine engine;
    private GameRenderer renderer;
    private Player player;
    private FoodManager foodManager;
    private SoundManager soundManager;
    private LehaHaiku victoryHaiku;
    private Timer gameTimer;

    public LehaGame() {
        logger.info("=== Инициализация главного окна ===");
        initWindowListeners();
        logger.info("Диалог настроек открыт");
        if (showSettingsDialog()) {
            logger.info("Настройки подтверждены. Начинаю инициализацию");
            initializeComponents();
            setupUI();
            setupManagers();
            startGame();
        } else {
            logger.warning("Игра не запущена: отмена настроек");
        }
    }

    private void initWindowListeners() {
        logger.info("Настройка обработчика закрытия окна");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdownGame();
                logger.info("Окно закрыто");
            }
        });
    }

    private boolean showSettingsDialog() {
        logger.info("Отображение диалога настроек");
        SettingsDialog dialog = new SettingsDialog(null);
        dialog.setVisible(true);
        return dialog.isSettingsConfirmed();
    }

    private void initializeComponents() {
        logger.info("=== Инициализация компонентов ===");
        player = new Player(GameConfig.GAME_WIDTH / 2, GameConfig.GAME_HEIGHT / 2);
        logger.info("Игрок создан (x: " + player.getPosition().x + ", y: " + player.getPosition().y + ")");

        foodManager = new FoodManager();
        soundManager = new SoundManager();
        if (renderer != null) {
            remove(renderer);
        }
        renderer = new GameRenderer(player, foodManager);
        engine = new GameEngine(player, foodManager, soundManager);

        foodManager.spawnFood(GameConfig.INITIAL_FOOD_COUNT);
        logger.info("Спавн начальной еды: " + GameConfig.INITIAL_FOOD_COUNT);

        soundManager.loadSounds();
        logger.info("Звуки загружены");

        add(renderer);
        addKeyListener(new InputHandler(player));
        logger.info("Обработчик ввода зарегистрирован");
        revalidate();
        repaint();
    }

    private void setupManagers() {
        logger.info("Настройка игровых менеджеров");
        lifecycleManager = new GameLifecycleManager(player, foodManager, soundManager);
        uiManager = new GameUIManager(this, renderer);
        uiManager.showGame();
        setupInputHandling();
    }

    private void setupInputHandling() {
        InputHandler inputHandler = new InputHandler(player);
        uiManager.setupInput(inputHandler);
        logger.info("Ввод настроен через GameUIManager");
    }

    private void setupUI() {
        logger.info("Настройка пользовательского интерфейса");
        setTitle("Леха всё сожрал!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
        setFocusable(true);
        logger.info("Окно отображено (размер: " + getWidth() + "x" + getHeight() + ")");
    }

    private void startGame() {
        logger.info("=== Запуск игрового цикла ===");
        if (engine != null) {
            engine.stopGame();
            logger.info("Предыдущий GameEngine остановлен");
        }
        if (gameTimer != null) {
            gameTimer.stop();
            logger.info("Предыдущий таймер остановлен");
        }

        soundManager.playBackgroundMusic();
        logger.info("Фоновая музыка запущена");

        engine = new GameEngine(player, foodManager, soundManager);
        new Thread(engine::startGame).start();
        logger.info("GameEngine запущен в новом потоке");

        gameTimer = new Timer(GameConfig.GAME_LOOP_DELAY, e -> {
            if (!engine.isGameRunning()) {
                logger.severe("Игровой движок остановлен! Причина неизвестна");
                gameTimer.stop();
                showGameOver();
            }
            renderer.repaint();
            logger.fine("Кадр перерисован");
        });
        gameTimer.start();
        logger.info("Таймер запущен (интервал: " + GameConfig.GAME_LOOP_DELAY + " мс)");
        player.reset();
        logger.info("Игрок перезапущен (размер: " + player.getSize() + ", калории: " + player.getCalories() + ")");
    }

    private void showGameOver() {
        logger.info("=== Игра завершена ===");
        soundManager.stopBackgroundMusic();
        setEnabled(false);
        logger.info("Окно заблокировано");

        Timer delayTimer = new Timer(GameConfig.GAME_OVER_DELAY, e -> {
            String message = buildGameOverMessage();
            logger.info("Сообщение завершения: " + message);

            int choice = JOptionPane.showOptionDialog(
                    this,
                    message,
                    "Игра окончена",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{"Новая игра", "Выход"},
                    0
            );

            setEnabled(true);
            if (choice == 0) {
                logger.info("Выбран перезапуск");
                restartGame();
            } else {
                logger.info("Выбран выход");
                dispose();
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
        logger.info("Таймер завершения запущен");
    }

    private String buildGameOverMessage() {
        logger.info("Формирование сообщения завершения");
        if (player.getSize() >= GameConfig.PLAYER_MAX_SIZE) {
            soundManager.playExplosionSound();
            logger.info("Поражение: превышение максимального размера");
            return "Леха лопнул от обжорства!";
        } else if (player.getCalories() >= GameConfig.WIN_CALORIES) {
            soundManager.playWinSound();
            victoryHaiku = LehaHaiku.getRandom();
            logger.info("Победа: достигнут лимит калорий");
            return formatHaikuMessage(victoryHaiku);
        } else {
            logger.warning("Принудительное завершение без условий победы/поражения");
            return "Лёха устал и уснул на пустой желудок...";
        }
    }

    private String formatHaikuMessage(LehaHaiku haiku) {
        logger.info("Форматирование хокку: " + haiku.getText());
        return "<html>" +
                "<div style='text-align: center; font-size: 14px;'>" +
                "Леха все сожрал!<br>" +
                haiku.getText().replace("\n", "<br>") +
                "</div></html>";
    }

    private void restartGame() {
        logger.info("=== Перезапуск игры ===");
        shutdownGame();
        victoryHaiku = null;
        player.reset();
        foodManager.reset();



        initializeComponents();
        setupUI();
        startGame();

        //renderer.repaint();
        //engine = new GameEngine(player, foodManager, soundManager);
        setExtendedState(JFrame.NORMAL);
        setVisible(true);
        toFront();
        requestFocusInWindow();
        logger.info("Новая игра запущена");
    }

    private void shutdownGame() {
        logger.info("=== Безопасное завершение ===");
        if (engine != null) engine.stopGame();
        if (gameTimer != null) gameTimer.stop();
        soundManager.stopAllSounds();
        logger.info("Ресурсы высвобождены");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            logger.info("Запуск приложения через SwingUtilities.invokeLater");
            new LehaGame();
        });
    }
}