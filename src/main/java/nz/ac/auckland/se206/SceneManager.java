package nz.ac.auckland.se206;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class SceneManager {
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

  // Adds Ui to the hashmap
  public static void addUi(AppUi ui, Parent root) {
    sceneMap.put(ui, root);
  }

  // Returns the root node of a Ui
  public static Parent getUiRoot(AppUi ui) {
    return sceneMap.get(ui);
  }

  // Returns the String value of each scene
  public static String convertUiType(AppUi ui) {
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

  public static void clearAllScenesExceptMainMenu() {
    for (Map.Entry<AppUi, Parent> entry : sceneMap.entrySet()) {
        AppUi ui = entry.getKey();
        Parent root = entry.getValue();
        
        // Check if the scene is not MAINMENU and it has a stage
        if (ui != AppUi.MAINMENU && root.getScene() != null && root.getScene().getWindow() instanceof Stage) {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.close();
        }
    }
    
    // Remove all scenes except MAINMENU from the sceneMap
    sceneMap.entrySet().removeIf(entry -> entry.getKey() != AppUi.MAINMENU);
}
}
