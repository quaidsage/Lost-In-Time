package nz.ac.auckland.se206.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * This class represents the controller for managing tasks in a game.
 * It provides methods for tracking the completion status of various tasks.
 */
public class TaskController {

  /** Indicates whether the riddle has been resolved. */
  private static BooleanProperty isLabResolved = new SimpleBooleanProperty(false);

  /** Indicates whether the storage task has been completed. */
  private static BooleanProperty isStorageResolved = new SimpleBooleanProperty(false);

  /** Indicates whether the control box task has been completed. */
  private static BooleanProperty isControlBoxResolved = new SimpleBooleanProperty(false);

  /**
   * Get a BooleanProperty representing the completion status of the lab task.
   * @return The BooleanProperty for the lab task.
   */
  public BooleanProperty labTaskCompletedProperty() {
    return isLabResolved;
  } 

  /**
   * Check if the lab task has been completed.
   * @return True if the lab task is completed, otherwise false.
   */
  public Boolean isLabTaskCompleted() {
    return isLabResolved.get();
  }

  /**
   * Set the completion status of the lab task.
   * @param completed True to mark the lab task as completed, otherwise false.
   */
  public void setLabTaskCompleted(Boolean completed) {
    isLabResolved.set(completed);    
  }

  /**
   * Get a BooleanProperty representing the completion status of the storage task.
   * @return The BooleanProperty for the storage task.
   */
  public BooleanProperty storageTaskCompletedProperty() {
    return isStorageResolved;
  }

  /**
   * Check if the storage task has been completed.
   * @return True if the storage task is completed, otherwise false.
   */
  public Boolean isStorageTaskCompleted() {
    return isStorageResolved.get();
  }

  /**
   * Set the completion status of the storage task.
   * @param completed True to mark the storage task as completed, otherwise false.
   */
  public void setStorageTaskCompleted(Boolean completed) {
    isStorageResolved.set(completed);    
  }

  /**
   * Get a BooleanProperty representing the completion status of the control box task.
   * @return The BooleanProperty for the control box task.
   */
  public BooleanProperty controlBoxTaskCompletedProperty() {
    return isControlBoxResolved;
  }

  /**
   * Check if the control box task has been completed.
   * @return True if the control box task is completed, otherwise false.
   */
  public Boolean isControlBoxTaskCompleted() {
    return isControlBoxResolved.get();
  }

  /**
   * Set the completion status of the control box task.
   * @param completed True to mark the control box task as completed, otherwise false.
   */
  public void setControlBoxTaskCompleted(Boolean completed) {
    isControlBoxResolved.set(completed);    
  }

  /**
   * Marks the lab task as completed.
   */
  public static void completeTask1() {
    isLabResolved.set(true);
  }

  /**
   * Marks the storage task as completed.
   */
  public static void completeTask2() {
    isStorageResolved.set(true);
  }

  /**
   * Marks the control box task as completed.
   */
  public static void completeTask3() {
    isControlBoxResolved.set(true);
  }
}
