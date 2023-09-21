package nz.ac.auckland.se206;

import java.util.HashMap;
import javafx.scene.Parent;

public class SceneManager {
  public enum AppUi {
    MAINMENU,
    TIMEMACHINE,
    LAB,
    STORAGE,
    DIFFICULTY,
    INTRO
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
    }
    return "mainmenu";
  }
}
