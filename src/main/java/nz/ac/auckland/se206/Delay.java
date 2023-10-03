package nz.ac.auckland.se206;

import javafx.concurrent.Task;

public class Delay {

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
