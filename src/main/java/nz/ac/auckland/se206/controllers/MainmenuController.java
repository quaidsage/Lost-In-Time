package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatTaskGenerator;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;

/** A controller class for the main menu scene. */
public class MainmenuController {
  public static Button btnSkip;

  @FXML private Button btnBeginGame;

  private int count = 0;

  /** Initialises the main menu scene with the required settings. */
  public void initialize() {
    // This method is automatically called when the FXML is loaded.
    // It can be used for any initialization tasks.

    textToSpeech("Lost in time. Restore the fabric of time.");
  }

  /**
   * Function to handle when the user starts game.
   *
   * @param event The event that triggered this function
   * @throws IOException If the FXML file is not found
   */
  @FXML
  private void onClickBeginGame(ActionEvent event) throws IOException {
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

    SceneManager.clearAllScenesExceptMainMenu();

    // Create a task to load various FXML files for different scenes
    Task<Void> loadTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // Load FXML files for different scenes and add them to the SceneManager
            SceneManager.addUi(AppUi.LAB, App.loadFxml("lab"));
            SceneManager.addUi(AppUi.STORAGE, App.loadFxml("storage"));
            SceneManager.addUi(AppUi.TIMEMACHINE, App.loadFxml("timemachine"));
            SceneManager.addUi(AppUi.ENDSCENE, App.loadFxml("endscene"));
            SceneManager.addUi(AppUi.TIMEOUT, App.loadFxml("timeout"));
            return null;
          }
        };
    loadTask.setOnSucceeded(
        e -> {
          System.out.println("All scenes loaded.");
          disableSkipButton();
        });
    new Thread(loadTask).start();

    // Set the UI to the difficulty selection screen
    SceneManager.addUi(AppUi.DIFFICULTY, App.loadFxml("difficulty"));
    App.setUi(AppUi.DIFFICULTY);
    SceneManager.addUi(AppUi.INTRO, App.loadFxml("intro"));
    textToSpeech("Select difficulty level and time limit.");
  }

  /**
   * Function to handle text to speech in seperate thread.
   *
   * @param msg The message to be spoken
   */
  private void textToSpeech(String msg) {
    // This method uses text-to-speech to speak the provided message.

    // Create a task for text-to-speech
    Task<Void> ttsTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // Create an instance of TextToSpeech and speak the message

            ChatTaskGenerator.textToSpeech.speak(msg);

            if (count == 1) {
              // Terminate the text-to-speech when done
              ChatTaskGenerator.textToSpeech.terminate();
            }

            count++;
            return null;
          }
        };

    // Create a thread to run the text-to-speech task
    Thread ttsThread = new Thread(ttsTask);
    ttsThread.setDaemon(true);
    ttsThread.start();
  }

  public static void disableSkipButton() {
    btnSkip.setDisable(false);
  }
}
