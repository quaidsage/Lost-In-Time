package nz.ac.auckland.se206;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.controllers.DifficultyController;
import nz.ac.auckland.se206.controllers.IntroController;
import nz.ac.auckland.se206.controllers.LabController;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;

public class RestartManager {

  public static CheckBox[] difficultyCheckBoxes;
  public static Object[] introElements;

  public static void restartGame() {
    // Reset various game states and settings when the game starts.
    GameState.isLabResolved = false;
    GameState.isStorageResolved = false;
    GameState.isLabVisited = false;
    GameState.isStorageVisited = false;
    GameState.isDifficultyEasy = false;
    GameState.isDifficultyMedium = false;
    GameState.isDifficultyHard = false;
    LabController.numHints = 5;

    // Initialise AI chat parameters
    GameState.chatCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.2).setTopP(0.5).setMaxTokens(200);

    restartDifficultyScene();
    restartIntroScene();
  }

  private static void restartDifficultyScene() {
    DifficultyController.isDifficultyChecked = false;
    DifficultyController.isTimeChecked = false;

    // Deselect all check boxes
    for (int i = 0; i < difficultyCheckBoxes.length; i++) {
      difficultyCheckBoxes[i].setSelected(false);
    }
  }

  private static void restartIntroScene() {
    IntroController.interaction = 0;
    ((TextArea) introElements[2])
        .setText(
            "You wake up in a strange room...\n Next to you, you see a strange device glowing.");

    // Toggle visibility of elements
    ((Button) introElements[0]).setVisible(true);
    ((Button) introElements[1]).setVisible(true);
    ((TextArea) introElements[2]).setVisible(true);
    ((Button) introElements[3]).setVisible(false);
    ((Rectangle) introElements[4]).setVisible(true);

    // Update disable status of required javafx elements
    ((Button) introElements[1]).setDisable(false);
    ((Button) introElements[3]).setDisable(true);

    // Restart text area
    ((TextArea) introElements[5]).setText("");
  }
}
