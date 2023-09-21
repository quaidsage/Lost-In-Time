package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;

public class timemachineController {
  // JavaFX elements
  @FXML private Button btnSwitchToLab, btnSwitchToStorage, btnSend;
  @FXML private Label lblTimer;
  @FXML private TextArea chatArea;
  @FXML private TextArea chatField;
  @FXML private ImageView imgScientistThinking;
  @FXML private Button timeMachine;


  // Initialise Variables
  private int characterDelay = 5;
  public static Task<Void> updateChatTask;
  public static ChatMessage chatTaskValue;

  // Initialise Timer
  private static timerController timer = new timerController();

  public void initialize() {
    timer = new timerController();
    // Initialise AI
    GameState.chatCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.2).setTopP(0.5).setMaxTokens(100);

    // Enable thinking image of scientist
    imgScientistThinking.setVisible(true);

    // Create riddle thread
    Task<ChatMessage> riddleTask =
        createTask(GptPromptEngineering.getRiddleWithGivenWord(GameState.item));
    Thread riddleThread = new Thread(riddleTask);
    riddleThread.start();

    riddleTask.setOnSucceeded(
        e -> {
          // Update imagery of scientist
          imgScientistThinking.setVisible(false);

          // Add to chat log
          GameState.chatLog = "-> " + riddleTask.getValue().getContent();

          // Append to chat area
          appendChatMessage(riddleTask.getValue());

          // Update chat area in other scenes
          Thread updateChatThreadLab = new Thread(labController.updateChatTask);
          updateChatThreadLab.start();
          Thread updateChatThreadStorage = new Thread(storageController.updateChatTask);
          updateChatThreadStorage.start();
        });

    // Bind the lblTimer to the timerController properties.
    lblTimer.textProperty().bind(timer.messageProperty());
    timer.setOnSucceeded(
        e -> {
          // Add code here to implement the loss of the game
          lblTimer.setText("0:00");
        });

    createUpdateTask();
  }

  /**
   * Change scene to lab.
   *
   * @param event the action event triggered by the send button
   */
  @FXML
  private void switchToLab(ActionEvent event) {
    App.setUi(AppUi.LAB);
  }

  @FXML
  private void clickTimeMachine(ActionEvent event) {
    if (GameState.isLabResolved && GameState.isStorageResolved) {
      System.out.println("You win!"); // add winning screen logic here
    }
  }

  @FXML
  private void showTimeMachine(MouseEvent event) {
    timeMachine.setOpacity(0.5);
    System.out.println("Time machine hovered");
  }

  @FXML
  private void hideTimeMachine(MouseEvent event) {
    timeMachine.setOpacity(0);
  }

  /**
   * Change scene to storage.
   *
   * @param event the action event triggered by the send button
   */
  @FXML
  private void switchToStorage(ActionEvent event) {
    App.setUi(AppUi.STORAGE);
  }

  /**
   * Function to start timer
   *
   * @param minutes the number of minutes to set the timer to
   */
  public static void timemachineStartTimer(int minutes) {
    timer.setMinutes(minutes);
    timer.start();
  }

  @FXML
  private void finishGame(ActionEvent event) {
    if (GameState.isLabResolved) {
      App.setUi(AppUi.ENDSCENE);
    }
  }
  /**
   * Creates a task to run the LLM model on a given message to be run by background thread.
   *
   * @param message string to attach to message to be given to the LLM
   */
  private Task<ChatMessage> createTask(String message) {
    Task<ChatMessage> task =
        new Task<ChatMessage>() {
          @Override
          protected ChatMessage call() throws Exception {
            btnSend.setDisable(true);
            ChatMessage msg = runGpt(new ChatMessage("assistant", message));
            Platform.runLater(
                () -> {
                  System.out.println("Initialised message");
                });
            return msg;
          }
        };
    return task;
  }

  /**
   * Appends a chat message to the chat text area one character at a time.
   *
   * @param msg the chat message to append
   */
  public void appendChatMessage(ChatMessage msg) {

    // Disable send button
    btnSend.setDisable(true);

    // Convert message to char array
    char[] ch = msg.getContent().toCharArray();

    // Use text to speech alongside chat appending
    Task<Void> txtSpeechTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            GameState.txtToSpeech.speak(msg.getContent());
            return null;
          }
        };

    Thread txtSpeechThread = new Thread(txtSpeechTask);
    txtSpeechThread.start();

    // Create a timeline and keyframes to append each character of the message to the chat text area
    Timeline timeline = new Timeline();
    if (ch.length < 100) {
      characterDelay = (50 - (ch.length / 2)) + 5;
    }
    System.out.println("printing with delay of: " + characterDelay);
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

    // Play timeline animation
    timeline.play();

    // Enable send button after animation is finished
    timeline.setOnFinished(
        event -> {
          btnSend.setDisable(false);
        });
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    GameState.chatCompletionRequest.addMessage(msg);
    try {
      ChatCompletionResult chatCompletionResult = GameState.chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      GameState.chatCompletionRequest.addMessage(result.getChatMessage());
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      e.printStackTrace();
      return null;
    }
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
    // Get message from chat field
    String message = chatField.getText();
    chatField.clear();

    // Check if message is empty
    if (message.trim().isEmpty()) {
      System.out.println("message is empty");
      return;
    }

    // Create chat message
    ChatMessage chatMessage = new ChatMessage("user", message);

    // Append message to current scene
    chatArea.appendText("\n\n<- ");
    appendChatMessage(chatMessage);

    // Update chat area in other scenes
    Thread updateChatThreadLab = new Thread(labController.updateChatTask);
    updateChatThreadLab.start();
    Thread updateChatThreadStorage = new Thread(storageController.updateChatTask);
    updateChatThreadStorage.start();

    // Add to chat log
    GameState.chatLog += "\n\n<- " + chatMessage.getContent();

    // Create task to run GPT model
    Task<ChatMessage> chatTask = createTask(message);
    Thread chatThread = new Thread(chatTask);
    chatThread.start();

    // Enable thinking image of scientist
    imgScientistThinking.setVisible(true);

    chatTask.setOnSucceeded(
        e -> {
          // Update imagery
          imgScientistThinking.setVisible(false);

          // Add to chat log
          GameState.chatLog += "\n\n-> " + chatTask.getValue().getContent();

          // Append response to current scene
          chatArea.appendText("\n\n-> ");
          appendChatMessage(chatTask.getValue());

          // Update chat area in other scenes
          Thread updateChatThreadLab2 = new Thread(labController.updateChatTask);
          updateChatThreadLab2.start();
          Thread updateChatThreadStorage2 = new Thread(storageController.updateChatTask);
          updateChatThreadStorage2.start();
        });
  }

  /**
   * Delays given code by a given number of milliseconds.
   *
   * @param ms milliseconds of delay
   * @param continuation Code to execute after delay
   */
  public static void delay(int ms, Runnable continuation) {
    // Create delay function
    Task<Void> delayTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            try {
              Thread.sleep(ms);
            } catch (InterruptedException e) {
            }
            return null;
          }
        };
    // Execute code after delay
    delayTask.setOnSucceeded(event -> continuation.run());

    // Start delay thread
    new Thread(delayTask).start();
  }

  /** Function to set chat area to current history of chat log. */
  public void updateChatArea() {
    chatArea.setText(GameState.chatLog);
    chatArea.appendText("");
  }

  /** Function to create task to update chat area for scene. */
  public void createUpdateTask() {
    updateChatTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            updateChatArea();
            createUpdateTask();
            return null;
          }
        };
  }
}
