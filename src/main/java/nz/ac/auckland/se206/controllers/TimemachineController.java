package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.Delay;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.ChatTaskGenerator;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

/** A controller class for the time machine scene. */
public class TimemachineController {
  public static Task<ChatMessage> contextTask;
  public static Task<Void> updateChatTask;
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
    new Thread(delayTask).start();
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

  // Initialise Variables
  private int characterDelay = 5;

  /** Carries out specific tasks required when opening the scene. */
  public void initialize() {
    // Create task to get context from GPT model
    setThinkingAnimation(true);
    contextTask = ChatTaskGenerator.createTask(GptPromptEngineering.getContext());
    new Thread(contextTask).start();

    timer = new TimerController();
    // Bind the lblTimer to the timerController properties.
    lblTimer.textProperty().bind(timer.messageProperty());
    timer.setOnSucceeded(
        e -> {
          // Add code here to implement the loss of the game
          lblTimer.setText("0:00");
        });

    initialiseTasks();
  }

  /**
   * Changes scene to the lab without resetting the scene.
   *
   * @param event the action event triggered by the send button
   */
  @FXML
  private void onClickLab(ActionEvent event) {
    if (!GameState.isLabVisited) {
      GameState.isLabVisited = true;
      new Thread(LabController.labIntroTask).start();
    }
    App.setUi(AppUi.LAB);
  }

  /**
   * Change scene to storage without resetting any scenes.
   *
   * @param event the action event triggered by the send button
   */
  @FXML
  private void onClickStorage(ActionEvent event) {
    if (!GameState.isStorageVisited) {
      GameState.isStorageVisited = true;
      Thread storageIntroThread = new Thread(StorageController.storageIntroTask);
      storageIntroThread.start();
    }
    App.setUi(AppUi.STORAGE);
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
    // Get user message and update chat with user message
    String userMessage = ChatTaskGenerator.getUserMessage(chatField);
    updateChat("\n\n<- ", new ChatMessage("user", userMessage));

    // Create task to run GPT model for AI response
    Task<ChatMessage> aiResponseTask = ChatTaskGenerator.createTask(userMessage);
    new Thread(aiResponseTask).start();
    setThinkingAnimation(true);

    aiResponseTask.setOnSucceeded(
        e -> {
          // Update chat with AI response
          setThinkingAnimation(false);
          updateChat("\n\n-> ", aiResponseTask.getValue());
        });
  }

  /**
   * Appends a chat message to the chat text area one character at a time.
   *
   * @param msg the chat message to append
   */
  public void appendChatMessage(ChatMessage msg) {
    btnSend.setDisable(true);

    // Create timeline animation of message appending to text area
    Timeline timeline = createMessageTimeline(msg.getContent().toCharArray());
    timeline.play();
    timeline.setOnFinished(
        event -> {
          btnSend.setDisable(false);
        });
  }

  /**
   * Create a timeline which animates the message into the text area character by character.
   *
   * @param ch the character array to animate
   * @return the timeline
   */
  private Timeline createMessageTimeline(char[] ch) {
    // Create a timeline and keyframes to append each character of the message to the chat text area
    Timeline timeline = new Timeline();
    if (ch.length < 100) {
      characterDelay = (50 - (ch.length / 2)) + 5;
    }
    Duration delayBetweenCharacters = Duration.millis(characterDelay);
    Duration frame = delayBetweenCharacters;
    for (int i = 0; i < ch.length; i++) {
      final int I = i;
      KeyFrame keyFrame =
          new KeyFrame(
              frame,
              event -> {
                chatArea.appendText(String.valueOf(ch[I]));
              });
      timeline.getKeyFrames().add(keyFrame);
      frame = frame.add(delayBetweenCharacters);
    }
    return timeline;
  }

  /**
   * Show/hide the thinking animation of scientist.
   *
   * @param isThinking whether the scientist is thinking
   */
  public void setThinkingAnimation(Boolean isThinking) {
    // Enable thinking image of scientist
    imgScientistThinking.setVisible(isThinking);
    typingBubble.setVisible(isThinking);
  }

  /**
   * Function to update chatlog, current scene chat area, and chat areas of other scenes.
   *
   * @param indent the indent of the message
   * @param chatMessage the chat message to update
   */
  private void updateChat(String indent, ChatMessage chatMessage) {
    // Add to chat log
    GameState.chatLog += indent + chatMessage.getContent();

    // Append to chat area
    chatArea.appendText(indent);
    appendChatMessage(chatMessage);

    // Update chat area in other scenes
    new Thread(LabController.updateChatTask).start();
    new Thread(StorageController.updateChatTask).start();
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

    // Starting prompt
    delay(
        2000,
        () -> {
          setThinkingAnimation(false);

          updateChat("-> ", contextTask.getValue());
        });
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
    // Create task to update chat area for this scene
    createUpdateTask();

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

  /** Function to create task to update chat area for scene. */
  public void createUpdateTask() {
    // Create task to update chat area
    updateChatTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // Update chat area with chat log
            chatArea.setText(GameState.chatLog);
            chatArea.appendText("");

            // Create new task for next update of chat area
            createUpdateTask();
            return null;
          }
        };
  }
}
