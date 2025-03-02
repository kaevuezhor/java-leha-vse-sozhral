package ru.kaevuezhor.leha.game;

import javax.sound.sampled.*;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import java.io.*;
import java.net.URL;

import javax.sound.sampled.*;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import java.io.*;
import java.net.URL;

/**
 * Управление звуковыми эффектами и музыкой
 */
public class SoundManager {
    private Clip eatSound;         // Звук поедания еды
    private Clip poisonSound;      // Звук отравления
    private Clip explosionSound;  // Звук взрыва
    private Player bgmPlayer;      // Проигрыватель фоновой музыки
    private boolean musicRunning; // Флаг воспроизведения музыки

    /**
     * Загружает все звуковые ресурсы
     */
    public void loadSounds() {
        loadClip("/eat.wav", "eat");
        loadClip("/diarea.wav", "poison");
        loadClip("/blup.wav", "explosion");
    }

    // Загружает звуковой файл в Clip
    private void loadClip(String path, String type) {
        try {
            URL url = getClass().getResource(path);
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);

            switch(type) {
                case "eat" -> {
                    eatSound = AudioSystem.getClip();
                    eatSound.open(audio);
                }
                case "poison" -> {
                    poisonSound = AudioSystem.getClip();
                    poisonSound.open(audio);
                }
                case "explosion" -> {
                    explosionSound = AudioSystem.getClip();
                    explosionSound.open(audio);
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
        musicRunning = true;
        new Thread(this::musicLoop).start();
    }

    // Цикл воспроизведения музыки
    private void musicLoop() {
        try {
            while(musicRunning) {
                InputStream is = getClass().getResourceAsStream("/theme.mp3");
                bgmPlayer = new Player(is);
                bgmPlayer.play();
            }
        } catch (Exception e) {
            System.err.println("Ошибка музыки: " + e.getMessage());
        }
    }

    /**
     * Останавливает фоновую музыку
     */
    public void stopBackgroundMusic() {
        musicRunning = false;
        if(bgmPlayer != null) bgmPlayer.close();
    }

    // Методы воспроизведения звуковых эффектов
    public void playEatSound() { playClip(eatSound); }
    public void playPoisonSound() { playClip(poisonSound); }
    public void playExplosionSound() { playClip(explosionSound); }

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