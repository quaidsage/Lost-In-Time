package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;

public class storageController {
  // JavaFX elements
  @FXML private Button btnSwitchToTimeMachine, btnSend;
  @FXML private Rectangle circuitBox;
  @FXML private ImageView background;
  @FXML private Label lblTimer;
  @FXML private ImageView circuitBoxImg;
  @FXML private TextArea chatArea;
  @FXML private TextArea chatField;
  @FXML private ImageView imgScientistThinking;
  @FXML private Rectangle circuitGameBg;
  @FXML private ImageView circuitGameImg;
  @FXML private VBox memoryGame;
  @FXML private Button btnStartCircuitGame;
  @FXML private Text text;
  @FXML private Button button0;
  @FXML private Button button1;
  @FXML private Button button2;
  @FXML private Button button3;
  @FXML private Button button4;
  @FXML private Button button5;
  @FXML private Button button6;
  @FXML private Button button7;
  @FXML private Button button8;
  @FXML private Text info;

  // Initialise Variables
  private int characterDelay = 5;
  public static Task<Void> updateChatTask;
  private ArrayList<Button> buttons = new ArrayList<>();
  private ArrayList<String> pattern = new ArrayList<>();
  private int patternOrder = 0;
  private int counter = 0;
  private int turn = 1;
  private Random random = new Random();
  private int consecutiveRounds = 0;
  private int targetConsecutiveRounds = 4;
  private ArrayList<String> possibleButtons =
      new ArrayList<>(
          Arrays.asList(
              "button0", "button1", "button2", "button3", "button4", "button5", "button6",
              "button7", "button8"));

  // Initialise Timer
  private static timerController timer = new timerController();

  public void initialize() {
    timer = new timerController();
    // Bind the lblTimer to the timerController properties.
    lblTimer.textProperty().bind(timer.messageProperty());
    timer.setOnSucceeded(
        e -> {
          // Add code here to implement the loss of the game
          lblTimer.setText("0:00");
        });

    createUpdateTask();

    buttons.addAll(
        Arrays.asList(
            button0, button1, button2, button3, button4, button5, button6, button7, button8));
  }

  @FXML
  void buttonClicked(ActionEvent event) {
    if (((Control) event.getSource()).getId().equals(pattern.get(counter))) {
      Button button = buttons.get(getIndexOfButton(event));
      changeButtonColor(button, "-fx-base: lightgreen");
      counter++;
    } else {
      resetGame(); // Reset the game on a wrong click
      return;
    }

    if (counter == turn) {
      if (consecutiveRounds >= targetConsecutiveRounds) {
        // Player wins after reaching the target consecutive rounds
        text.setText("You Win!");
        winGame();
      } else {
        nextTurn();
      }
    }
  }

  @FXML
  void start(ActionEvent event) {
    pattern.clear();
    text.setText("Current Streak: ");

    pattern.add(possibleButtons.get(random.nextInt(possibleButtons.size())));
    showPattern();
    System.out.println(pattern);

    counter = 0;
    turn = 1;
  }

  private void winGame() {
    System.out.println("minigame won");
    circuitGameBg.setVisible(false);
    circuitGameImg.setVisible(false);
    memoryGame.setVisible(false);
    btnStartCircuitGame.setVisible(false);
    text.setVisible(false);
    info.setVisible(false);

    background.setVisible(true);
    circuitBox.setVisible(true);
    circuitBoxImg.setVisible(true);
  }

  private void nextTurn() {
    counter = 0;
    consecutiveRounds++; // Increase consecutive rounds on successful turn
    turn++;

    pattern.add(possibleButtons.get(random.nextInt(possibleButtons.size())));
    showPattern();
    System.out.println(pattern);
    text.setText("Current Streak: " + consecutiveRounds);
  }

  private void resetGame() {
    consecutiveRounds = 0; // Reset consecutive rounds
    text.setText("Wrong - Start Again");
    pattern.clear();
    counter = 0;
    turn = 1;
  }

  private int getIndexOfButton(ActionEvent event) {
    String buttonId = ((Control) event.getSource()).getId();
    return Integer.parseInt(buttonId.substring(buttonId.length() - 1));
  }

  private int getIndexOfButton(String button) {
    return Integer.parseInt(button.substring(button.length() - 1));
  }

  private void showPattern() {
    PauseTransition pause = new PauseTransition(Duration.seconds(0.75));
    pause.setOnFinished(
        e -> {
          Timeline timeline =
              new Timeline(
                  new KeyFrame(
                      Duration.seconds(0.75),
                      event -> {
                        showNext();
                      }));
          timeline.setCycleCount(pattern.size());
          timeline.play();
        });
    pause.play();
  }

  private void showNext() {
    Button button = buttons.get(getIndexOfButton(pattern.get(patternOrder)));
    changeButtonColor(button, "-fx-base: gray");
    patternOrder++;

    if (patternOrder == turn) {
      patternOrder = 0;
    }
  }

  private void changeButtonColor(Button button, String color) {
    button.setStyle(color);
    PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
    pause.setOnFinished(
        e -> {
          button.setStyle(null);
        });
    pause.play();
  }

  /**
   * Change scene to time machine
   *
   * @param event the action event triggered by the time machine button
   */
  @FXML
  private void switchToTimeMachine(ActionEvent event) {
    App.setUi(AppUi.TIMEMACHINE);
  }

  @FXML
  private void clickCircuitBox(MouseEvent event) {
    System.out.println("Circuit box clicked");

    background.setVisible(false);
    circuitBox.setVisible(false);
    circuitBoxImg.setVisible(false);

    circuitGameBg.setVisible(true);
    circuitGameImg.setVisible(true);
    memoryGame.setVisible(true);
    btnStartCircuitGame.setVisible(true);
    text.setVisible(true);
    info.setVisible(true);
  }

  /**
   * Function to start timer
   *
   * @param minutes the number of minutes to set the timer to
   */
  public static void storageStartTimer(int minutes) {
    timer.setMinutes(minutes);
    timer.start();
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
    Thread updateChatThreadTM = new Thread(timemachineController.updateChatTask);
    updateChatThreadTM.start();

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
          Thread updateChatThreadTM2 = new Thread(timemachineController.updateChatTask);
          updateChatThreadTM2.start();
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
