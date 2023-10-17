package nz.ac.auckland.se206;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class AnimationManager {
  public static ImageView imgStorageDoor;
  public static Rectangle rectLabLeftDoor;
  public static Rectangle rectLabRightDoor;
  public static ImageView imgLabPaper;
  public static TextArea txtLabRecipe;

  /**
   * Delays given code by a given number of milliseconds.
   *
   * @param ms milliseconds of delay
   * @param continuation Code to execute after delay
   */
  public static void delay(int ms, Runnable continuation) {
    Task<Void> delayTask = Delay.createDelay(ms);
    delayTask.setOnSucceeded(event -> continuation.run());
    Thread delayThread = new Thread(delayTask);
    delayThread.setDaemon(true);
    delayThread.start();
  }

  /** TODO JAVADOCS */
  public static void openStorageDoor() {
    App.audio.playScan();
    imgStorageDoor.setVisible(true);
    imgStorageDoor.setLayoutX(11);
    final Timeline timeline = new Timeline();
    final KeyValue kv = new KeyValue(imgStorageDoor.layoutXProperty(), -1200);
    final KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
    timeline.getKeyFrames().add(kf);
    delay(
        300,
        () -> {
          App.audio.playStorageDoor();
          timeline.play();
        });
    timeline.setOnFinished(event -> imgStorageDoor.setVisible(false));
  }

  /** TODO JAVADOCS */
  public static void openLabDoor() {
    App.audio.playScan();
    rectLabLeftDoor.setVisible(true);
    rectLabLeftDoor.setLayoutX(25);
    rectLabRightDoor.setLayoutX(505);
    final Timeline timeline = new Timeline();
    final KeyValue kvLeft = new KeyValue(rectLabLeftDoor.layoutXProperty(), -1000);
    final KeyFrame kfLeft = new KeyFrame(Duration.millis(400), kvLeft);
    timeline.getKeyFrames().add(kfLeft);
    final KeyValue kvRight = new KeyValue(rectLabRightDoor.layoutXProperty(), 1500);
    final KeyFrame kfRight = new KeyFrame(Duration.millis(400), kvRight);
    timeline.getKeyFrames().add(kfRight);
    delay(
        300,
        () -> {
          App.audio.playLabDoor();
          timeline.play();
        });
    timeline.setOnFinished(
        event -> {
          rectLabLeftDoor.setVisible(false);
        });
  }

  /** TODO JAVADOCS */
  public static void printRecipe() {
    App.audio.playPrint();
    imgLabPaper.setVisible(true);
    imgLabPaper.setLayoutY(-20);
    final Timeline timeline = new Timeline();
    final KeyValue kvPaper = new KeyValue(imgLabPaper.layoutYProperty(), 130);
    final KeyFrame kfPaper = new KeyFrame(Duration.millis(900), kvPaper);
    timeline.getKeyFrames().add(kfPaper);
    final KeyValue kvText = new KeyValue(txtLabRecipe.layoutYProperty(), 140);
    final KeyFrame kfText = new KeyFrame(Duration.millis(900), kvText);
    timeline.getKeyFrames().add(kfText);
    timeline.play();
  }

  /** TODO JAVADOCS */
  public static void removeRecipe() {
    App.audio.playPrint();
    imgLabPaper.setLayoutY(130);
    txtLabRecipe.setLayoutY(140);
    final Timeline timeline = new Timeline();
    final KeyValue kvPaper = new KeyValue(imgLabPaper.layoutYProperty(), -20);
    final KeyFrame kfPaper = new KeyFrame(Duration.millis(900), kvPaper);
    timeline.getKeyFrames().add(kfPaper);
    final KeyValue kvText = new KeyValue(txtLabRecipe.layoutYProperty(), -40);
    final KeyFrame kfText = new KeyFrame(Duration.millis(900), kvText);
    timeline.getKeyFrames().add(kfText);
    timeline.play();
  }
}
