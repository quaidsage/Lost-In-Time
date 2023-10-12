package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.Delay;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.ChatTaskGenerator;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.speech.TextToSpeech;

/** A controller class for the time machine scene. */
public class TimemachineController {
  public static ChatMessage chatTaskValue;
  public static Task<Void> startTask;
  private static TimerController timer = new TimerController();

  /**
   * Function to start the time in the timemachinescene.
   *
   * @param minutes the number of minutes to set the timer to.
   */
  public static void timemachineStartTimer(int minutes) {
    timer.setMinutes(minutes);
    timer.start();
  }

  /**
   * Delays given code by a given number of milliseconds.
   *
   * @param ms milliseconds of delay.
   * @param continuation Code to execute after delay.
   */
  public static void delay(int ms, Runnable continuation) {
    Task<Void> delayTask = Delay.createDelay(ms);
    delayTask.setOnSucceeded(event -> continuation.run());
    Thread delayThread = new Thread(delayTask);
    delayThread.setDaemon(true);
    delayThread.start();
  }

  // JavaFX elements
  @FXML private Button btnSwitchToLab;
  @FXML private Button btnSwitchToStorage;
  @FXML private Button btnSend;
  @FXML private Label lblTimer;
  @FXML private TextArea chatArea;
  @FXML private TextArea chatField;
  @FXML private ImageView imgScientistThinking;
  @FXML private Rectangle rectLight;
  @FXML private Button btnTimeMachine;
  @FXML private Button btnMenu;
  @FXML private ImageView typingBubble;

  // Initialise contextTask
  private Task<ChatMessage> contextTask;
  private ChatMessage contextResponse;

  /** Carries out specific tasks required when opening the scene. */
  public void initialize() {
    // Generate ai response for the context of the game
    contextTask = ChatTaskGenerator.createTask(GptPromptEngineering.getContext());
    contextTask.setOnSucceeded(
        e -> {
          contextResponse = contextTask.getValue();
          IntroController.isContextGenerated = true;
        });
    IntroController.isContextGenerated = false;
    Thread contextThread = new Thread(contextTask);
    contextThread.setDaemon(true);
    contextThread.start();

    // Initialise timer and bind the lblTimer to the timerController properties.
    timer = new TimerController();
    lblTimer.textProperty().bind(timer.messageProperty());
    timer.setOnSucceeded(
        e -> {
          lblTimer.setText("0:00");
        });

    // Initialise relevant tasks
    initialiseTasks();
  }

  /**
   * Changes scene to the lab without resetting the scene.
   *
   * @param event the action event triggered by the send button
   */
  @FXML
  private void onClickLab(ActionEvent event) {
    App.setUi(AppUi.LAB);
    if (!GameState.isLabVisited) {
      GameState.isLabVisited = true;
      Thread labIntroThread = new Thread(LabController.labIntroTask);
      labIntroThread.setDaemon(true);
      labIntroThread.start();
    }
  }

  /**
   * Change scene to storage without resetting any scenes.
   *
   * @param event the action event triggered by the send button
   */
  @FXML
  private void onClickStorage(ActionEvent event) {
    App.setUi(AppUi.STORAGE);
    if (!GameState.isStorageVisited) {
      GameState.isStorageVisited = true;
      Thread storageIntroThread = new Thread(StorageController.storageIntroTask);
      storageIntroThread.setDaemon(true);
      storageIntroThread.start();
    }
  }

  /**
   * Function that handles if the game is completed.
   *
   * @param event the action event triggered by the send button
   */
  @FXML
  private void onClickTimeMachine(ActionEvent event) {
    // Check if game is complete
    if (GameState.isLabResolved && GameState.isStorageResolved) {
      App.setUi(AppUi.ENDSCENE);
    }
  }

  /**
   * Function to handle returning to main menu.
   *
   * @param event the action event triggered by the send button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onClickReturn(ActionEvent event) throws IOException {
    App.setRoot("mainmenu");
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {
    ChatTaskGenerator.onSendMessage(chatField);
  }

  /** Function to animate the start of the round. */
  public void startRound() {
    // Light flash animation
    animateLights();

    // Start timer. Change 'minutes' variable to change the length of the game
    lblTimer.setVisible(true);
    timemachineStartTimer(IntroController.minutes);
    LabController.labStartTimer(IntroController.minutes);
    StorageController.storageStartTimer(IntroController.minutes);

    ChatTaskGenerator.updateChat("-> ", contextResponse);
    TextToSpeech.runTTS(contextResponse.getContent());
  }

  /** Function to create an animation of the lights turning on. */
  private void animateLights() {
    // Start animation and set the rectangle to visible
    rectLight.setVisible(true);
    // Start a series of delays to turn the light on and off
    delay(
        500,
        () -> {
          rectLight.setVisible(false);
          delay(
              100,
              () -> {
                rectLight.setOpacity(0.8);
                rectLight.setVisible(true);
                delay(
                    300,
                    () -> {
                      rectLight.setVisible(false);
                      delay(
                          100,
                          () -> {
                            rectLight.setOpacity(0.4);
                            rectLight.setVisible(true);
                            delay(
                                100,
                                () -> {
                                  // Finish the animation.
                                  rectLight.setVisible(false);
                                });
                          });
                    });
              });
        });
  }

  /** Function to create tasks to update elements outside class controller. */
  private void initialiseTasks() {
    // Set chat area
    ChatTaskGenerator.chatAreas.add(chatArea);

    // Set send button
    ChatTaskGenerator.sendButtons.add(btnSend);

    // Set thinking animation
    ChatTaskGenerator.thinkingAnimationImages.add(imgScientistThinking);
    ChatTaskGenerator.thinkingAnimationImages.add(typingBubble);

    // Create task to start round
    startTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            startRound();
            return null;
          }
        };
  }
}
