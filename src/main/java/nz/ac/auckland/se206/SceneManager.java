package nz.ac.auckland.se206;

import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.stage.Stage;

/** A class to store and manage various scenes within the game. */
public class SceneManager {
  /** A collcetion of names for various scenes. */
  public enum AppUi {
    MAINMENU,
    TIMEMACHINE,
    LAB,
    STORAGE,
    DIFFICULTY,
    INTRO,
    ENDSCENE,
    TIMEOUT
  }

  // Initialize hashMap to manage Scenes
  private static HashMap<AppUi, Parent> sceneMap = new HashMap<AppUi, Parent>();

  /**
   * Adds a Ui to the hashmap that contains all scenes.
   *
   * @param ui the UI to be added as the key for the root.
   * @param root the root of the scene being added.
   */
  public static void addUi(AppUi ui, Parent root) {
    sceneMap.put(ui, root);
  }

  /**
   * Returns the root node of a Ui.
   *
   * @param ui the root to get from the method.
   * @return the instance of the ui root.
   */
  public static Parent getUiRoot(AppUi ui) {
    return sceneMap.get(ui);
  }

  /**
   * Returns the String value of each possible scene.
   *
   * @param ui takes the ui to be converted.
   * @return the string of the ui value.
   */
  public static String convertUiType(AppUi ui) {
    // With the switch statement, we can convert the enum to a string
    switch (ui) {
      case MAINMENU:
        return "mainmenu";
      case DIFFICULTY:
        return "difficulty";
      case TIMEMACHINE:
        return "timemachine";
      case LAB:
        return "lab";
      case STORAGE:
        return "storage";
      case INTRO:
        return "intro";
      case ENDSCENE:
        return "endscene";
      case TIMEOUT:
        return "timeout";
    }
    return "mainmenu";
  }

  /** Clears all of the scenes and closes the game. */
  public static void clearAllScenesAndClose() {
    for (Map.Entry<AppUi, Parent> entry : sceneMap.entrySet()) {
      Parent root = entry.getValue();

      if (root.getScene() != null && root.getScene().getWindow() instanceof Stage) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
      }
    }

    // Clear all scenes from the sceneMap
    sceneMap.clear();

    // Close the application
    Platform.exit();
  }

  /** Clears all of the scenes except for the main menu. */
  public static void clearAllScenesExceptMainMenu() {
    for (Map.Entry<AppUi, Parent> entry : sceneMap.entrySet()) {
      AppUi ui = entry.getKey();
      Parent root = entry.getValue();

      // Check if the scene is not MAINMENU and it has a stage
      if (ui != AppUi.MAINMENU
          && root.getScene() != null
          && root.getScene().getWindow() instanceof Stage) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
      }
    }

    // Remove all scenes except MAINMENU from the sceneMap
    sceneMap.entrySet().removeIf(entry -> entry.getKey() != AppUi.MAINMENU);
  }
}
