package nz.ac.auckland.se206.controllers;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class AudioController {

  private MediaView mediaView;
  private MediaPlayer mediaPlayer;
  private Media media;

  public AudioController(MediaView mediaView) {
    this.mediaView = mediaView;
  }

  public void playClick() {
    media = new Media(getClass().getResource("/sounds/click.wav").toExternalForm());
    mediaPlayer = new MediaPlayer(media);
    mediaView.setMediaPlayer(mediaPlayer);
    mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getStartTime());
    mediaView.getMediaPlayer().play();
  }
}
