package nz.ac.auckland.se206;

import javafx.concurrent.Task;

/** A class to generate a delay between characters when printing text. */
public class Delay {

  /**
   * Function to create a delay with given time.
   *
   * @param ms milliseconds of delay.
   * @return the task to be run by the background thread
   */
  public static Task<Void> createDelay(int ms) {
    // Create delay function
    Task<Void> delayTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            try {
              Thread.sleep(ms);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            return null;
          }
        };
    return delayTask;
  }
}
