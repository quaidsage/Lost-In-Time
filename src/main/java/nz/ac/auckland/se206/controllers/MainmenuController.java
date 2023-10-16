package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javax.speech.AudioException;
import javax.speech.EngineStateError;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.ChatTaskGenerator;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.speech.TextToSpeech;

/** A controller class for the main menu scene. */
public class MainmenuController {

  public static Button btnSkip;

  public static boolean hasRestarted = false;
  public static boolean isTTSMuted = true;

  @FXML private Button btnBeginGame;
  @FXML private Button btnMute;

  /** Initialises the main menu scene with the required settings. */
  public void initialize() {
    // Initialise text to speech

    loadFxmlFiles();

    // Initialise AI chat parameters
    GameState.chatCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.2).setTopP(0.5).setMaxTokens(200);
  }

  /**
   * Function to handle when the user starts game.
   *
   * @param event The event that triggered this function
   * @throws IOException If the FXML file is not found
   * @throws EngineStateError If there is an error with the speech engine
   * @throws AudioException If there is an error with the audio
   */
  @FXML
  private void onClickBeginGame(ActionEvent event)
      throws IOException, AudioException, EngineStateError {
    App.setUi(AppUi.DIFFICULTY);
    TextToSpeech.runTextToSpeech("Select difficulty level and time limit.");
  }

  /**
   * Function to toggle whether to mute or unmute TTS.
   *
   * @throws EngineStateError If there is an error with the speech engine
   * @throws AudioException If there is an error with the audio
   */
  @FXML
  private void muteTTS(ActionEvent event) throws AudioException, EngineStateError {
    if (isTTSMuted) {
      isTTSMuted = false;
      ChatTaskGenerator.textToSpeech.pause(false);
      btnMute.setText("Mute TTS (TEMP BUTTON)");
    } else {
      isTTSMuted = true;
      ChatTaskGenerator.textToSpeech.pause(true);
      btnMute.setText("Unmute TTS (TEMP BUTTON)");
    }
  }

  /** Function to disable the skip button until all fxml files are loaded. */
  public static void disableSkipButton() {
    Task<Void> disableSkipButtonTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            while (App.currentUi == AppUi.INTRO) {
              if (IntroController.isFilesLoaded) {
                btnSkip.setDisable(false);
              } else {
                btnSkip.setDisable(true);
              }
            }
            return null;
          }
        };
    Thread disableSkipButtonThread = new Thread(disableSkipButtonTask);
    disableSkipButtonThread.setDaemon(true);
    disableSkipButtonThread.start();
  }

  /**
   * Function to load all fxml files for application in seperate thread then load first AI response.
   */
  private void loadFxmlFiles() {
    // Initialise with TTS message
    TextToSpeech.runTextToSpeech("Lost in time. Restore the fabric of time.");

    // Create a task to load various FXML files for different scenes
    Task<Void> loadTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // Load FXML files for different scenes and add them to the SceneManager
            // Clear any excess scenes on restart
            SceneManager.addUi(AppUi.DIFFICULTY, App.loadFxml("difficulty"));
            SceneManager.addUi(AppUi.INTRO, App.loadFxml("intro"));
            SceneManager.addUi(AppUi.LAB, App.loadFxml("lab"));
            SceneManager.addUi(AppUi.STORAGE, App.loadFxml("storage"));
            SceneManager.addUi(AppUi.ENDSCENE, App.loadFxml("endscene"));
            SceneManager.addUi(AppUi.TIMEOUT, App.loadFxml("timeout"));
            SceneManager.addUi(AppUi.TIMEMACHINE, App.loadFxml("timemachine"));
            return null;
          }
        };

    loadTask.setOnSucceeded(
        e -> {
          IntroController.isFilesLoaded = true;
          newContextResponse();
          ChatTaskGenerator.enableEnterHandler();
        });
    Thread loadThread = new Thread(loadTask);
    loadThread.setDaemon(true);
    loadThread.start();
  }

  /** Function to create new first AI response on loading time machine. */
  public static void newContextResponse() {
    // Generate context for start of the game
    ChatTaskGenerator.contextResponse = null;
    Task<ChatMessage> contextTask = ChatTaskGenerator.createTask(GptPromptEngineering.getContext());
    contextTask.setOnSucceeded(
        event -> {
          ChatTaskGenerator.contextResponse = contextTask.getValue();
          if (App.currentUi == AppUi.TIMEMACHINE) {
            TimemachineController.appendContextProperty.set(
                !TimemachineController.appendContextProperty.get());
          } else {
            IntroController.isContextLoaded = true;
          }
        });
    Thread contextThread = new Thread(contextTask);
    contextThread.setDaemon(true);
    contextThread.start();
  }
}
