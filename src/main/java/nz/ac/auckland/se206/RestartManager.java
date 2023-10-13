package nz.ac.auckland.se206;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.controllers.DifficultyController;
import nz.ac.auckland.se206.controllers.IntroController;
import nz.ac.auckland.se206.controllers.LabController;
import nz.ac.auckland.se206.controllers.TimemachineController;
import nz.ac.auckland.se206.controllers.TimerController;
import nz.ac.auckland.se206.gpt.ChatTaskGenerator;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;

public class RestartManager {

  public static CheckBox[] difficultyCheckBoxes;
  public static Object[] introElements;
  public static Label timemachineLabel;
  public static Rectangle timemachineRect;

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

    clearGameChatAreas();
    restartTimemachineScene();
  }

  private static void restartDifficultyScene() {
    DifficultyController.isDifficultyChecked = false;
    DifficultyController.isTimeChecked = false;
    DifficultyController.booleanProperty.set(true);

    // Deselect all check boxes
    for (int i = 0; i < difficultyCheckBoxes.length; i++) {
      difficultyCheckBoxes[i].setSelected(false);
    }
  }

  private static void restartIntroScene() {
    IntroController.interaction = 0;
    IntroController.isContextLoaded = false;

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

  private static void restartTimemachineScene() {
    // Initialise timer and bind the lblTimer to the timerController properties.
    TimemachineController.timer = new TimerController();
    timemachineLabel.textProperty().bind(TimemachineController.timer.messageProperty());
    TimemachineController.timer.setOnSucceeded(
        e -> {
          timemachineLabel.setText("0:00");
        });

    // Initialise start task
    TimemachineController.createStartTask(timemachineRect, timemachineLabel);
  }

  private static void clearGameChatAreas() {
    for (int i = 0; i < ChatTaskGenerator.chatAreas.size(); i++) {
      ChatTaskGenerator.chatAreas.get(i).setText("");
    }
  }
}
