package ru.kaevuezhor.leha.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Диалоговое окно настроек игры
 */
public class SettingsDialog extends JDialog {
    private JSlider volumeSlider;       // Регулятор громкости
    private JComboBox<String> difficultyCombo; // Выбор сложности
    private JCheckBox soundCheckbox;     // Включение звука
    private boolean settingsConfirmed;  // Флаг подтверждения

    public SettingsDialog(Frame parent) {
        super(parent, "Настройки игры", true);
        initializeComponents();
        setupUI();
    }

    private void initializeComponents() {
        // Создаем компоненты
        volumeSlider = new JSlider(0, 100, 80);
        difficultyCombo = new JComboBox<>(new String[]{"Легко", "Средне", "Сложно"});
        soundCheckbox = new JCheckBox("Включить звук", true);

        // Кнопки управления
        JButton okButton = new JButton("OK");
        okButton.addActionListener(this::handleOk);

        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(e -> dispose());

        // Группируем компоненты
        JPanel content = new JPanel(new GridLayout(4, 2, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        content.add(new JLabel("Громкость:"));
        content.add(volumeSlider);
        content.add(new JLabel("Сложность:"));
        content.add(difficultyCombo);
        content.add(new JLabel("Звуковые эффекты:"));
        content.add(soundCheckbox);
        content.add(okButton);
        content.add(cancelButton);

        add(content);
    }

    private void setupUI() {
        setSize(350, 200);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void handleOk(ActionEvent e) {
        settingsConfirmed = true;
        applySettings();
        dispose();
    }

    /**
     * Применяет выбранные настройки к игре
     */
    private void applySettings() {
        GameConfig.VOLUME = volumeSlider.getValue() / 100f;
        GameConfig.SOUND_ENABLED = soundCheckbox.isSelected();

        switch(difficultyCombo.getSelectedIndex()) {
            case 0 -> setEasyDifficulty();
            case 1 -> setNormalDifficulty();
            case 2 -> setHardDifficulty();
        }
    }

    private void setEasyDifficulty() {
        GameConfig.MOVEMENT_COST = 1;
        GameConfig.POISON_SPAWN_CHANCE = 0.05f;
    }

    private void setNormalDifficulty() {
        GameConfig.MOVEMENT_COST = 2;
        GameConfig.POISON_SPAWN_CHANCE = 0.10f;
    }

    private void setHardDifficulty() {
        GameConfig.MOVEMENT_COST = 3;
        GameConfig.POISON_SPAWN_CHANCE = 0.15f;
    }

    public boolean isSettingsConfirmed() {
        return settingsConfirmed;
    }
}
