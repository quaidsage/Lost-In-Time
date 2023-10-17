package nz.ac.auckland.se206.controllers;

import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import nz.ac.auckland.se206.Delay;

/**
 * This class represents an audio controller for managing audio playback in a game.
 * It provides methods for playing various sound effects and controlling audio settings.
 */
public class AudioController {

  /**
   * Delays given code by a given number of milliseconds.
   *
   * @param ms milliseconds of delay.
   * @param continuation Code to execute after delay.
   */
  public static void delay(int ms, Runnable continuation) {
    Task<Void> delayTask = Delay.createDelay(ms);
    delayTask.setOnSucceeded(event -> continuation.run());
    Thread delayThread = new Thread(delayTask);
    delayThread.setDaemon(true);
    delayThread.start();
  }

  private MediaView mediaView;
  private MediaPlayer mediaPlayer;
  private Media media;
  private double volume = 0.5;

  /**
   * Constructs an AudioController with a given MediaView for audio playback.
   *
   * @param mediaView The MediaView for audio playback.
   */
  public AudioController(MediaView mediaView) {
    this.mediaView = mediaView;
  }

  private void playMedia(String fileName) {
    media = new Media(getClass().getResource(fileName).toExternalForm());
    mediaPlayer = new MediaPlayer(media);
    mediaView.setMediaPlayer(mediaPlayer);
    mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getStartTime());
    mediaView.getMediaPlayer().setVolume(volume);
    mediaView.getMediaPlayer().play();
  }

  /**
   * Set the volume for audio playback.
   *
   * @param volume The volume level to set (0.0 to 1.0).
   */
  public void setVolume(double volume) {
    this.volume = volume;
  }

  /**
   * Play the lights sound effect.
   */
  public void playLights() {
    playMedia("/sounds/lights.wav");
  }

  /**
   * Play the click sound effect.
   */
  public void playClick() {
    playMedia("/sounds/click.wav");
  }

  /**
   * Play the success sound effect.
   */
  public void playSuccess() {
    playMedia("/sounds/success.wav");
  }

  /**
   * Play the print sound effect.
   */
  public void playPrint() {
    playMedia("/sounds/print.wav");
  }

  /**
   * Play the fail sound effect.
   */
  public void playFail() {
    playMedia("/sounds/fail.mp3");
  }

  /**
   * Play the keypad sound effect.
   */
  public void playKeypad() {
    playMedia("/sounds/keypad.wav");
  }

  /**
   * Play the pattern sound effect.
   */
  public void playPattern() {
    playMedia("/sounds/pattern.wav");
  }

  /**
   * Play the scan sound effect.
   */
  public void playScan() {
    playMedia("/sounds/scan.wav");
  }

  /**
   * Play the lab door sound effect.
   */
  public void playLabDoor() {
    playMedia("/sounds/labdoor.mp3");
  }

  public void playLabDoorClose() {
    this.volume = volume / 3;
    playMedia("/sounds/labdoor.mp3");
    this.volume = volume * 3;
  }

  public void playStorageDoor() {
    playMedia("/sounds/storagedoor.wav");
  }

  public void playStorageDoorClose() {
    this.volume = volume / 5;
    playMedia("/sounds/storagedoor.wav");
    delay(
        350,
        () -> {
          this.volume = volume / 4;
          playMedia("/sounds/storagedoorclose.m4a");
          this.volume = volume * 20;
        });
  }

  /**
   * Stop the currently playing audio.
   */
  public void stop() {
    mediaView.getMediaPlayer().stop();
  }
}
