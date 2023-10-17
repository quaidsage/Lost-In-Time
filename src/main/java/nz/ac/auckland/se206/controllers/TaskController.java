package nz.ac.auckland.se206.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class TaskController {

  /** Indicates whether the riddle has been resolved. */
  private static BooleanProperty isLabResolved = new SimpleBooleanProperty(false);

  /** Indicates whether the storage task has been completed. */
  private static BooleanProperty isStorageResolved = new SimpleBooleanProperty(false);

  /** Indicates whether the control box task has been completed. */
  private static BooleanProperty isControlBoxResolved = new SimpleBooleanProperty(false);

  public BooleanProperty labTaskCompletedProperty() {
    return isLabResolved;
  } 

  public Boolean isLabTaskCompleted() {
    return isLabResolved.get();
  }

  public void setLabTaskCompleted(Boolean completed) {
    isLabResolved.set(completed);    
  }

  public BooleanProperty storageTaskCompletedProperty() {
    return isStorageResolved;
  }

  public Boolean isStorageTaskCompleted() {
    return isStorageResolved.get();
  }

  public void setStorageTaskCompleter(Boolean completed) {
    isStorageResolved.set(completed);    
  }

  public BooleanProperty controlBoxTaskCompletedProperty() {
    return isControlBoxResolved;
  }

  public Boolean isControlBoxTaskCompleted() {
    return isControlBoxResolved.get();
  }

  public void setControlBoxTaskCompleter(Boolean completed) {
    isLabResolved.set(completed);    
  }

  public static void completeTask1() {
    isLabResolved.set(true);
  }

public static void completeTask2() {
    isStorageResolved.set(true);
  }

public static void completeTask3() {
    isControlBoxResolved.set(true);
  }

}
