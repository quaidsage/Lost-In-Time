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
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class mainmenuController {
  @FXML private Button btnBeginGame;

  int count = 0;

  public void initialize() {
    // textToSpeech("Soft enj 2 0 6, escape room. Welcome!");
  }

  @FXML
  private void beginGame(ActionEvent event) throws IOException {
    // Reset GameState
    GameState.isLabResolved = false;
    GameState.isStorageResolved = false;
    GameState.isLabVisited = false;
    GameState.isStorageVisited = false;
    GameState.isDifficultyEasy = false;
    GameState.isDifficultyMedium = false;
    GameState.isDifficultyHard = false;
    labController.numHints = 5;

    // Initialise AI
    GameState.chatCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.2).setTopP(0.5).setMaxTokens(200);

    // Create a task to load all the fxml files
    Task<Void> loadTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            SceneManager.addUi(AppUi.LAB, App.loadFxml("lab"));
            SceneManager.addUi(AppUi.STORAGE, App.loadFxml("storage"));
            SceneManager.addUi(AppUi.TIMEMACHINE, App.loadFxml("timemachine"));
            SceneManager.addUi(AppUi.ENDSCENE, App.loadFxml("endscene"));
            SceneManager.addUi(AppUi.TIMEOUT, App.loadFxml("timeout"));
            return null;
          }
        };
    Thread loadThread = new Thread(loadTask);
    loadThread.start();

    SceneManager.addUi(AppUi.DIFFICULTY, App.loadFxml("difficulty"));
    App.setUi(AppUi.DIFFICULTY);
    SceneManager.addUi(AppUi.INTRO, App.loadFxml("intro"));
    // textToSpeech("Select difficulty level and time limit.");
  }

  private void textToSpeech(String msg) {
    // Declare textToSpeech from tts constructor
    Task<Void> ttsTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            // call desired methods when start()
            TextToSpeech textToSpeech = new TextToSpeech();
            textToSpeech.speak(msg);

            if (count == 1) {
              // Terminate the text-to-speech when done
              textToSpeech.terminate();
            }

            count++;
            return null;
          }
        };

    // Create a thread to run the task
    new Thread(ttsTask).start();
  }
}
