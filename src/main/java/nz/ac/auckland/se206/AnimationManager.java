package nz.ac.auckland.se206;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class AnimationManager {
  public static ImageView imgStorageDoor;
  public static Rectangle rectLabLeftDoor;
  public static Rectangle rectLabRightDoor;

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
    imgStorageDoor.setVisible(true);
    imgStorageDoor.setLayoutX(11);
    final Timeline timeline = new Timeline();
    final KeyValue kv = new KeyValue(imgStorageDoor.layoutXProperty(), -1200);
    final KeyFrame kf = new KeyFrame(Duration.millis(400), kv);
    timeline.getKeyFrames().add(kf);
    delay(300, () -> timeline.play());
    timeline.setOnFinished(event -> imgStorageDoor.setVisible(false));
  }

  public static void openLabDoor() {
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
    delay(300, () -> timeline.play());
    timeline.setOnFinished(
        event -> {
          rectLabLeftDoor.setVisible(false);
        });
  }
}
