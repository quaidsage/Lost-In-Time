package nz.ac.auckland.se206;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/** Class that acts as a manager for animations in the game. */
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
    // Create delay task for animations
    Task<Void> delayTask = Delay.createDelay(ms);
    delayTask.setOnSucceeded(event -> continuation.run());
    Thread delayThread = new Thread(delayTask);
    delayThread.setDaemon(true);
    delayThread.start();
  }

  /** Function to handle the storage door animation. */
  public static void openStorageDoor() {
    // play audio
    App.audio.playScan();
    // Set fxml elements
    imgStorageDoor.setVisible(true);
    imgStorageDoor.setLayoutX(11);
    // Create animation timeline
    final Timeline timeline = new Timeline();
    final KeyValue kv = new KeyValue(imgStorageDoor.layoutXProperty(), -1200);
    final KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
    timeline.getKeyFrames().add(kf);
    // Start the animation with a short delay
    delay(
        300,
        () -> {
          App.audio.playStorageDoor();
          timeline.play();
        });
    timeline.setOnFinished(event -> imgStorageDoor.setVisible(false));
  }

  /** Function to handle opening of the lab door. */
  public static void openLabDoor() {
    // Play sound
    App.audio.playScan();
    // Update scene elements.
    rectLabLeftDoor.setVisible(true);
    rectLabLeftDoor.setLayoutX(25);
    rectLabRightDoor.setLayoutX(505);
    // Create animation for door opening.
    final Timeline timeline = new Timeline();
    final KeyValue kvLeft = new KeyValue(rectLabLeftDoor.layoutXProperty(), -1000);
    final KeyFrame kfLeft = new KeyFrame(Duration.millis(400), kvLeft);
    timeline.getKeyFrames().add(kfLeft);
    final KeyValue kvRight = new KeyValue(rectLabRightDoor.layoutXProperty(), 1500);
    final KeyFrame kfRight = new KeyFrame(Duration.millis(400), kvRight);
    timeline.getKeyFrames().add(kfRight);
    // Create a delay for the sounds and animation start
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

  /** Print the recipe on the piece of paper. */
  public static void printRecipe() {
    // Play audio
    App.audio.playPrint();
    // Update fxml values (layout/visibilty).
    imgLabPaper.setVisible(true);
    imgLabPaper.setLayoutY(-20);
    // Create animation
    final Timeline timeline = new Timeline();
    final KeyValue kvPaper = new KeyValue(imgLabPaper.layoutYProperty(), 130);
    final KeyFrame kfPaper = new KeyFrame(Duration.millis(900), kvPaper);
    timeline.getKeyFrames().add(kfPaper);
    final KeyValue kvText = new KeyValue(txtLabRecipe.layoutYProperty(), 140);
    final KeyFrame kfText = new KeyFrame(Duration.millis(900), kvText);
    timeline.getKeyFrames().add(kfText);
    // Start animation
    timeline.play();
  }

  /** Function removes the recipe when the solution is made. */
  public static void removeRecipe() {
    // Play audio
    App.audio.playPrint();
    // Update layout for the recipe
    imgLabPaper.setLayoutY(130);
    txtLabRecipe.setLayoutY(140);
    // Make animation
    final Timeline timeline = new Timeline();
    final KeyValue kvPaper = new KeyValue(imgLabPaper.layoutYProperty(), -20);
    final KeyFrame kfPaper = new KeyFrame(Duration.millis(900), kvPaper);
    timeline.getKeyFrames().add(kfPaper);
    final KeyValue kvText = new KeyValue(txtLabRecipe.layoutYProperty(), -40);
    final KeyFrame kfText = new KeyFrame(Duration.millis(900), kvText);
    timeline.getKeyFrames().add(kfText);
    // Play animation
    timeline.play();
  }
}
