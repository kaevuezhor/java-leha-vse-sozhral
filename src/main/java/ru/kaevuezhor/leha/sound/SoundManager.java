package ru.kaevuezhor.leha.sound;

import javax.sound.sampled.*;

import ru.kaevuezhor.leha.game.GameConfig;

import java.net.URL;
import java.util.Objects;

/**
 * Управление звуковыми эффектами и музыкой
 */
public class SoundManager {
    private Clip eatSound;         // Звук поедания еды
    private Clip poisonSound;      // Звук отравления
    private Clip explosionSound;  // Звук взрыва

    private Clip themeSound;       // Фоновая музыка
    private Clip healthySound; // Новая переменная для звука healthy

    private Clip energySound; //

    private Clip regularPoisonSound;

    private Clip winSound;

    private Clip lostSound;

    private Clip junkSound;


    /**
     * Загружает все звуковые ресурсы
     */
    public void loadSounds() {
        loadClip("/eat.wav", "eat");
        loadClip("/diarea.wav", "poison");
        loadClip("/blup.wav", "explosion");
        loadClip("/theme.wav", "theme");
        loadClip("/healthy.wav", "healthy"); // Новая строка
        loadClip("/energy.wav", "energy"); // Новая строка
        loadClip("/poison_regular.wav", "regular_poison");
        loadClip("/win.wav", "win"); // Новая строка
        loadClip("/perezhral.wav", "lost"); // Новая строка
        loadClip("/junk.wav", "junk"); // Новая строка
    }

    // Загружает звуковой файл в Clip
    private void loadClip(String path, String type) {
        try {
            URL url = getClass().getResource(path);
            AudioInputStream audio = AudioSystem.getAudioInputStream(Objects.requireNonNull(url));

            switch(type) {
                case "eat" -> {
                    eatSound = AudioSystem.getClip();
                    eatSound.open(audio);
                }
                case "healthy" -> { // Новая ветка для healthy
                    healthySound = AudioSystem.getClip();
                    healthySound.open(audio);
                }
                case "poison" -> {
                    poisonSound = AudioSystem.getClip();
                    poisonSound.open(audio);
                }
                case "explosion" -> {
                    explosionSound = AudioSystem.getClip();
                    explosionSound.open(audio);
                }
                case "theme" -> {
                    themeSound = AudioSystem.getClip();
                    themeSound.open(audio);
                }
                case "energy" -> {
                    energySound = AudioSystem.getClip();
                    energySound.open(audio);
                }
                case "regular_poison" -> {
                    regularPoisonSound = AudioSystem.getClip();
                    regularPoisonSound.open(audio);
                }
                case "win" -> {
                    winSound = AudioSystem.getClip();
                    winSound.open(audio);
                }
                case "lost" -> {
                    lostSound = AudioSystem.getClip();
                    lostSound.open(audio);
                }
                case "junk" -> {
                    junkSound = AudioSystem.getClip();
                    junkSound.open(audio);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки звука: " + e.getMessage());
        }
    }

    /**
     * Воспроизводит фоновую музыку в цикле
     */
    public void playBackgroundMusic() {
        playClip(themeSound);
    }

    /**
     * Останавливает фоновую музыку
     */
    public void stopBackgroundMusic() {
        themeSound.stop();
    }

    // Методы воспроизведения звуковых эффектов
    public void playEatSound() { playClip(eatSound); }
    public void playPoisonSound() { playClip(poisonSound); }
    public void playExplosionSound() { playClip(explosionSound); }

    public void playHealthySound() {
        playClip(healthySound);
    }

    public void playEnergySound() {
        playClip(energySound);
    }

    public void playRegularPoisonSound() {
        playClip(regularPoisonSound);
    }

    public void playWinSound() {
        playClip(winSound);
    }

    public void playLostSound() {
        playClip(lostSound);
    }

    public void playJunkSound() {
        playClip(junkSound);
    }

    // Общий метод воспроизведения звука
    public void playClip(Clip clip) {
        if(clip != null && GameConfig.SOUND_ENABLED) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(GameConfig.VOLUME) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);

            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void stopAllSounds() {
        if(eatSound != null) eatSound.stop();
        if(poisonSound != null) poisonSound.stop();
        if(explosionSound != null) explosionSound.stop();
        stopBackgroundMusic();
    }
}