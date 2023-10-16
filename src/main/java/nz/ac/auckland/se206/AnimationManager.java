package nz.ac.auckland.se206;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class AnimationManager {
  public static ImageView imgStorageDoor;

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
  public static void openDoor() {
    imgStorageDoor.setVisible(true);
    imgStorageDoor.setLayoutX(11);
    final Timeline timeline = new Timeline();
    final KeyValue kv = new KeyValue(imgStorageDoor.layoutXProperty(), -1200);
    final KeyFrame kf = new KeyFrame(Duration.millis(700), kv);
    timeline.getKeyFrames().add(kf);
    delay(500, () -> timeline.play());
    timeline.setOnFinished(event -> imgStorageDoor.setVisible(false));
  }
}
