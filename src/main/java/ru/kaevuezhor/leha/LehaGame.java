package ru.kaevuezhor.leha;

import ru.kaevuezhor.leha.food.FoodManager;
import ru.kaevuezhor.leha.game.GameConfig;
import ru.kaevuezhor.leha.game.GameEngine;
import ru.kaevuezhor.leha.game.GameLifecycleManager;
import ru.kaevuezhor.leha.game.GameRenderer;
import ru.kaevuezhor.leha.game.GameUIManager;
import ru.kaevuezhor.leha.game.InputHandler;
import ru.kaevuezhor.leha.game.SettingsDialog;
import ru.kaevuezhor.leha.game.SoundManager;
import ru.kaevuezhor.leha.player.Player;
import ru.kaevuezhor.leha.reply.LehaHaiku;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Главное окно игры "Леха всё сожрал!".
 * Управляет созданием интерфейса, обработкой событий и игровой логикой.
 */
public class LehaGame extends JFrame {
    // Основные компоненты игры
    private GameLifecycleManager lifecycleManager;
    private GameUIManager uiManager;
    private GameEngine engine;          // Обрабатывает игровую логику и правила
    private GameRenderer renderer;      // Отвечает за отрисовку графики
    private Player player;              // Объект игрока (Лехи)
    private FoodManager foodManager;    // Управляет генерацией и состоянием еды
    private SoundManager soundManager;  // Контролирует воспроизведение звуков
    private LehaHaiku victoryHaiku;     // Хранит хокку для победного сообщения
    private Timer gameTimer;            // Таймер для обновления игрового цикла

    /**
     * Конструктор игры.
     * Запускает процесс инициализации и отображает игровое окно.
     */
    public LehaGame() {
        initWindowListeners();          // Настройка реакции на закрытие окна
        if(showSettingsDialog()) {      // Показ настроек перед стартом
            initializeComponents();    // Создание игровых объектов
            //setupUI();                  // Настройка внешнего вида окна
            setupManagers();
            startGame();                // Запуск игрового процесса
        }
    }

    /**
     * Настраивает обработчик закрытия окна.
     * Гарантирует корректное завершение процессов при закрытии игры.
     */
    private void initWindowListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdownGame();  // Остановка всех систем перед выходом
            }
        });
    }

    /**
     * Показывает диалог настроек игры.
     * @return true, если игрок подтвердил настройки, false - если отменил
     */
    private boolean showSettingsDialog() {
        SettingsDialog dialog = new SettingsDialog(null);  // Создание диалога
        dialog.setVisible(true);                           // Показ диалога
        return dialog.isSettingsConfirmed();              // Проверка подтверждения
    }

    /**
     * Инициализирует игровые компоненты:
     * - Создает игрока
     * - Настраивает менеджеры еды и звуков
     * - Подготавливает системы отрисовки и ввода
     */
    private void initializeComponents() {
        // Создание игрока в центре экрана
        player = new Player(
                GameConfig.GAME_WIDTH / 2,
                GameConfig.GAME_HEIGHT / 2
        );

        // Инициализация систем управления
        foodManager = new FoodManager();          // Генератор еды
        soundManager = new SoundManager();        // Звуковая система

        // Игровые подсистемы
        renderer = new GameRenderer(player, foodManager);  // Отрисовка
        engine = new GameEngine(player, foodManager, soundManager);  // Логика

        // Подготовка к старту
        foodManager.spawnFood(GameConfig.INITIAL_FOOD_COUNT);  // Первая партия еды
        soundManager.loadSounds();                 // Загрузка звуковых файлов

        // Добавление компонентов на экран
        add(renderer);                             // Панель отрисовки в окно
        addKeyListener(new InputHandler(player));  // Обработка клавиатуры
    }

    private void setupManagers() {
        lifecycleManager = new GameLifecycleManager(player, foodManager, soundManager);
        uiManager = new GameUIManager(new GameRenderer(player, foodManager));
        uiManager.showGame();
        setupInputHandling();
    }

    private void setupInputHandling() {
        InputHandler inputHandler = new InputHandler(player);
        uiManager.setupInput(inputHandler);
    }

    /**
     * Настраивает параметры игрового окна:
     * - Заголовок и размеры
     * - Расположение и видимость
     * - Запрет изменения размера
     */
    private void setupUI() {
        setTitle("Леха всё сожрал!");     // Название в заголовке
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Выход при закрытии
        pack();                          // Автоподбор размера под содержимое
        setLocationRelativeTo(null);      // Центрирование на экране
        setVisible(true);                 // Делаем окно видимым
        setResizable(false);              // Фиксированный размер окна
    }

    /**
     * Запускает основной игровой цикл:
     * - Останавливает предыдущие сессии
     * - Запускает фоновую музыку
     * - Активирует игровой движок и таймер обновлений
     */
    private void startGame() {
        // Очистка предыдущего состояния
        if(engine != null) engine.stopGame();
        if(gameTimer != null) gameTimer.stop();

        soundManager.playBackgroundMusic();  // Фоновая музыка
        //lifecycleManager.startGame();
        
        // Запуск движка в отдельном потоке
        engine = new GameEngine(player, foodManager, soundManager);
        new Thread(engine::startGame).start();

        // Настройка таймера для обновления экрана
        gameTimer = new Timer(GameConfig.GAME_LOOP_DELAY, e -> {
            if(!engine.isGameRunning()) {    // Проверка состояния игры
                gameTimer.stop();
                showGameOver();              // Обработка завершения
            }
            renderer.repaint();              // Перерисовка кадра
        });
        gameTimer.start();  // Старт игрового цикла
    }

    /**
     * Обрабатывает завершение игры:
     * - Останавливает музыку
     * - Блокирует окно
     * - Показывает итоговое сообщение
     */
    private void showGameOver() {
        soundManager.stopBackgroundMusic();  // Тишина!

        setEnabled(false);  // Блокировка взаимодействия

        // Задержка перед показом сообщения
        Timer delayTimer = new Timer(GameConfig.GAME_OVER_DELAY, e -> {
            String message = buildGameOverMessage();  // Формирование текста

            // Диалог с выбором действия
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

            setEnabled(true);  // Разблокировка окна

            // Обработка выбора игрока
            if (choice == 0) {
                restartGame();   // Рестарт
            } else {
                dispose();       // Выход
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    /**
     * Формирует сообщение о результате игры.
     * @return Готовый текст для диалогового окна
     */
    private String buildGameOverMessage() {
        if (player.getSize() >= GameConfig.PLAYER_MAX_SIZE) {
            soundManager.playExplosionSound();  // Звук взрыва
            return "Леха лопнул от обжорства!";
        } else if (player.getCalories() >= GameConfig.WIN_CALORIES) {
            // Генерация хокку при первом достижении победы
            if (victoryHaiku == null) {
                victoryHaiku = LehaHaiku.getRandom();
            }
            return formatHaikuMessage(victoryHaiku);  // Стилизованное сообщение
        }
        return "Лёха устал и уснул на пустой желудок...";  // Поражение
    }

    /**
     * Форматирует текст хокку для красивого отображения.
     * @param haiku Объект с текстом хокку
     * @return HTML-код для диалогового окна
     */
    private String formatHaikuMessage(LehaHaiku haiku) {
        return "<html><div style='text-align: center; width: 300px;'>"
                + "<h2 style='color: #FF6B6B;'>ЛЁХА ВСЁ СЖРАЛ!</h2>"
                + "<div style='margin: 15px 0;'>"
                + "<i>" + haiku.getText().replace("\n", "<br>") + "</i>"
                + "</div></div></html>";
    }

    /**
     * Перезапускает игру с начальными параметрами.
     * Обнуляет прогресс и создает новую игровую сессию.
     */
    private void restartGame() {
        shutdownGame();  // Очистка текущего состояния

        // Создание новой игры
        new LehaGame().setVisible(true);
        if(engine != null) engine.stopGame();

        // Сброс параметров
        victoryHaiku = null;     // Очистка хокку
        player.reset();          // Сброс состояния игрока
        foodManager.reset();     // Перегенерация еды
        engine = new GameEngine(player, foodManager, soundManager);  // Новый движок
        startGame();             // Запуск цикла
    }

    /**
     * Точка входа в приложение.
     * Запускает игру в специальном потоке для Swing.
     */
    public static void main(String[] args) {
        // Корректный запуск Swing-приложения
        SwingUtilities.invokeLater(() -> {
            new LehaGame().setVisible(true);  // Создание и показ окна
        });
    }

    /**
     * Безопасно завершает работу всех систем игры.
     * Останавливает таймеры, звуки и освобождает ресурсы.
     */
    private void shutdownGame() {
        if(engine != null) engine.stopGame();  // Остановка логики
        if(gameTimer != null) gameTimer.stop(); // Остановка таймера
        if (soundManager != null) {
            soundManager.stopAllSounds();      // Выключение звуков
        }
        dispose();  // Закрытие окна
    }
}