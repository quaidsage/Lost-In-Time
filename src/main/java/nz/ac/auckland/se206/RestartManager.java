package nz.ac.auckland.se206;

import java.util.ArrayList;
import java.util.Collections;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.controllers.DifficultyController;
import nz.ac.auckland.se206.controllers.IntroController;
import nz.ac.auckland.se206.controllers.LabController;
import nz.ac.auckland.se206.controllers.StorageController;
import nz.ac.auckland.se206.controllers.TimemachineController;
import nz.ac.auckland.se206.controllers.TimerController;
import nz.ac.auckland.se206.gpt.ChatTaskGenerator;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;

/** Class to restart components in every scene in the game. */
public class RestartManager {

  public static CheckBox[] difficultyCheckBoxes;
  public static Object[] introElements;
  public static Label timemachineLabel;
  public static Rectangle timemachineRect;
  public static Label labLabel;
  public static Polyline labChemicals;
  public static ArrayList<ImageView> labArrowCollection;
  public static TextArea labTxtRecipe;
  public static Label storageLabel;
  public static Object[] storageElements;

  /** Function to restart certain parts of the game by calling restart methods. */
  public static void restartGame() {
    // Reset various game states and settings when the game starts.
    ChatTaskGenerator.numHints = 5;

    // Initialise AI chat parameters
    GameState.chatCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.17).setTopP(0.6).setMaxTokens(400);

    restartDifficultyScene();
    restartIntroScene();

    clearGameChatAreas();
    restartTimemachineScene();
    restartLabScene();
    restartStorageScene();
  }

  /** Function to restart the difficulty menu scene upon playing the game again. */
  private static void restartDifficultyScene() {
    GameState.isDifficultyEasy = false;
    GameState.isDifficultyMedium = false;
    GameState.isDifficultyHard = false;

    DifficultyController.isDifficultyChecked = false;
    DifficultyController.isTimeChecked = false;
    DifficultyController.booleanProperty.set(true);

    // Deselect all check boxes
    for (int i = 0; i < difficultyCheckBoxes.length; i++) {
      difficultyCheckBoxes[i].setSelected(false);
    }
  }

  /** Function to restart the introduction scene upon playing the game again. */
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

  /** Function to restart the time machine scene upon playing the game again. */
  private static void restartTimemachineScene() {
    GameState.isHackOpened = false;

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

  /** Function to restart the lav scene upon playing the game again. */
  private static void restartLabScene() {
    GameState.isLabResolved = false;
    GameState.isLabVisited = false;

    LabController.numChemicalsAdded = 0;
    Boolean[] isChemicalSolution = new Boolean[] {false, false, false, false, false, false, false};

    // Initialise timer and bind the lblTimer to the timerController properties.
    LabController.timer = new TimerController();
    labLabel.textProperty().bind(LabController.timer.messageProperty());
    LabController.timer.setOnSucceeded(
        e -> {
          App.setUi(AppUi.TIMEOUT);
          LabController.timer.reset();
        });

    // Create task to run GPT model for intro message
    LabController.labIntroTask = ChatTaskGenerator.createTask(GptPromptEngineering.getLabIntro());
    LabController.labIntroTask.setOnSucceeded(
        e -> {
          ChatTaskGenerator.updateChat("\n\n-> ", LabController.labIntroTask.getValue());
          labChemicals.setVisible(true);
        });

    // Reset arrows
    for (int i = 0; i < 7; i++) {
      labArrowCollection.get(i + 21).setVisible(false);
      labArrowCollection.get(i + 14).setVisible(false);
      labArrowCollection.get(i + 7).setVisible(true);
      labArrowCollection.get(i).setVisible(true);
    }

    // Set solution chemicals
    ArrayList<Integer> list = new ArrayList<Integer>();
    for (int i = 0; i < 7; i++) {
      list.add(i);
    }
    Collections.shuffle(list);
    ArrayList<Integer> solutionColours = new ArrayList<Integer>();
    for (int i = 0; i < 3; i++) {
      solutionColours.add(list.get(i));
      isChemicalSolution[solutionColours.get(i)] = true;
    }
    LabController.isChemicalSolution = isChemicalSolution;
    LabController.solutionColours = solutionColours;

    // Set new recipe
    AnimationManager.removeRecipe();
    labTxtRecipe.setText("Recipe:\n" + LabController.convertRecipe(solutionColours));
  }

  /** Function to restart the storage scene upon playing the game again. */
  private static void restartStorageScene() {
    // Reset game states
    GameState.isStorageResolved = false;
    GameState.isStorageVisited = false;
    StorageController.counter = 0;
    StorageController.patternOrder = 0;
    StorageController.turn = 1;
    StorageController.consecutiveRounds = 0;

    // Initialise timer
    StorageController.timer = new TimerController();
    storageLabel.textProperty().bind(StorageController.timer.messageProperty());
    StorageController.timer.setOnSucceeded(
        e -> {
          storageLabel.setText("0:00");
        });

    // Get introduction message on first visit of storage room
    StorageController.storageIntroTask =
        ChatTaskGenerator.createTask(GptPromptEngineering.getStorageIntro());
    StorageController.storageIntroTask.setOnSucceeded(
        e -> {
          ChatTaskGenerator.updateChat("\n\n-> ", StorageController.storageIntroTask.getValue());
        });

    // Enable visibility of mini-game elements
    ((ImageView) storageElements[0]).setVisible(true);
    ((Rectangle) storageElements[1]).setVisible(true);
    ((ImageView) storageElements[2]).setVisible(true);

    // Disable visibility of other elements
    ((Rectangle) storageElements[3]).setVisible(false);
    ((ImageView) storageElements[4]).setVisible(false);
    ((VBox) storageElements[5]).setVisible(false);
    ((Button) storageElements[6]).setVisible(false);
    ((Text) storageElements[7]).setVisible(false);
    ((Text) storageElements[8]).setVisible(false);
    ((Text) storageElements[9]).setVisible(false);

    // Re enable switch to time machine button
    ((Button) storageElements[10]).setDisable(false);

    // Reset text
    ((Text) storageElements[7]).setText("Current Streak: ");
  }

  /** Function to clear chat areas for all scenes in the game. */
  private static void clearGameChatAreas() {
    for (int i = 0; i < ChatTaskGenerator.chatAreas.size(); i++) {
      ChatTaskGenerator.chatAreas.get(i).setText("");
    }
  }
}
