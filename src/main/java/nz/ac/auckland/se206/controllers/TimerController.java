package nz.ac.auckland.se206.controllers;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/** A class to create a timer to be used within the game. */
public class TimerController extends Service<Void> {
  private int minutes = 2; // Initial minutes for the timer
  private int seconds = 0; // Initial seconds for the timer

  /**
   * Sets the number of minutes for the timer.
   *
   * @param minutes The number of minutes to set for the timer.
   */
  public void setMinutes(int minutes) {
    this.minutes = minutes;
  }

  /**
   * Creates and returns a task that handles the timer countdown.
   *
   * @return A task responsible for updating the timer
   */
  @Override
  protected Task<Void> createTask() {
    return new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        // Continuously update the timer until it reaches 00:00
        while (minutes >= 0 && seconds >= 0) {
          // Update the timer display in the format MM:SS
          updateMessage(String.format("%02d:%02d", minutes, seconds));

          Thread.sleep(1000); // Sleep for 1 second

          // Update minutes and seconds
          if (seconds == 0) {
            minutes--;
            seconds = 59;
          } else {
            seconds--;
          }
        }
        return null;
      }
    };  
  }

  public void cancelAndReset() {
    if (isRunning()) {
        cancel(); // Cancel the timer task
    }
    reset(); // Reset the service to its initial state
    minutes = 2; // Reset minutes to the initial value
    seconds = 0; // Reset seconds to the initial value
  }
}
