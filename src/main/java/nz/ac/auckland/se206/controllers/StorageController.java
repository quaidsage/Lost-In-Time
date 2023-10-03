package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
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
import nz.ac.auckland.se206.Delay;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.ChatTaskGenerator;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class StorageController {
  public static Task<Void> updateChatTask;
  public static Task<ChatMessage> storageIntroTask;

  // Initialise Timer
  private static TimerController timer = new TimerController();

  /**
   * Function to start timer.
   *
   * @param minutes the number of minutes to set the timer to
   */
  public static void storageStartTimer(int minutes) {
    timer.setMinutes(minutes);
    timer.start();
  }

  /**
   * Delays given code by a given number of milliseconds.
   *
   * @param ms milliseconds of delay
   * @param continuation Code to execute after delay
   */
  public static void delay(int ms, Runnable continuation) {
    Task<Void> delayTask = Delay.createDelay(ms);
    delayTask.setOnSucceeded(event -> continuation.run());
    new Thread(delayTask).start();
  }

  // JavaFX elements
  @FXML private Button btnSwitchToTimeMachine;
  @FXML private Button btnSend;
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
  @FXML private Button btnMenu;
  @FXML private Text info;
  @FXML private ImageView typingBubble;

  // Initialise Variables
  private int CHARACTER_DELAY = 5;
  private ArrayList<Button> buttons = new ArrayList<>();
  private ArrayList<String> pattern = new ArrayList<>();
  private int patternOrder = 0;
  private int counter = 0;
  private int turn = 1;
  private Random random = new Random();
  private int consecutiveRounds = 0;
  private int targetConsecutiveRounds = 4;
  private boolean buttonsDisabled = false;
  private ArrayList<String> possibleButtons =
      new ArrayList<>(
          Arrays.asList(
              "button0", "button1", "button2", "button3", "button4", "button5", "button6",
              "button7", "button8"));

  public void initialize() throws ApiProxyException {
    // Initialise timer
    timer = new TimerController();
    lblTimer.textProperty().bind(timer.messageProperty());
    timer.setOnSucceeded(
        e -> {
          lblTimer.setText("0:00");
        });

    createUpdateTask();

    // Get introduction message on first visit of storage room
    storageIntroTask = ChatTaskGenerator.createTask(GptPromptEngineering.getStorageIntro());
    setThinkingAnimation(true);
    storageIntroTask.setOnSucceeded(
        e -> {
          setThinkingAnimation(false);
          updateChat("\n\n-> ", storageIntroTask.getValue());
        });

    buttons.addAll(
        Arrays.asList(
            button0, button1, button2, button3, button4, button5, button6, button7, button8));
  }

  /**
   * Function to handle when a minigame button is pressed.
   *
   * @param event the action event triggered by the button press
   */
  @FXML
  private void onClickPanel(ActionEvent event) {
    if (buttonsDisabled) {
      return; // Ignore clicks while buttons are disabled
    }
    System.out.println(((Control) event.getSource()).getId());
    if (((Control) event.getSource()).getId().equals(pattern.get(counter))) {
      Button button = buttons.get(getIndexOfButton(event));
      changeButtonColor(
          button, "-fx-background-color: rgb(77,181,127); -fx-border-color: rgb(28,28,28);");
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

  /**
   * Function to handle the starting of the minigame.
   *
   * @param event the action event triggered by the start button
   */
  @FXML
  private void onClickStartMemoryGame(ActionEvent event) {
    // Clear pattern and text for new game
    pattern.clear();
    text.setText("Current Streak: ");

    // Get first possible button to press and show
    pattern.add(possibleButtons.get(random.nextInt(possibleButtons.size())));
    showPattern();
    System.out.println(pattern);

    // Set initial parameters
    counter = 0;
    turn = 1;
  }

  /**
   * Change scene to time machine.
   *
   * @param event the action event triggered by the time machine button
   */
  @FXML
  private void onClickTimeMachineRoom(ActionEvent event) {
    App.setUi(AppUi.TIMEMACHINE);
  }

  /** Function to handle when the circuit minigame is opened. */
  @FXML
  private void onClickCircuit(MouseEvent event) {

    // Hide the circuit box
    background.setVisible(false);
    circuitBox.setVisible(false);
    circuitBoxImg.setVisible(false);

    // Show enlarged circuit box and minigame elements
    circuitGameBg.setVisible(true);
    circuitGameImg.setVisible(true);
    memoryGame.setVisible(true);
    btnStartCircuitGame.setVisible(true);
    text.setVisible(true);
    info.setVisible(true);
    btnSwitchToTimeMachine.setDisable(true);
  }

  /**
   * Function to handle returning to main menu.
   *
   * @param event the action event triggered by the menu button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onClickReturn(ActionEvent event) throws IOException {
    App.setRoot("mainmenu");
    SceneManager.clearAllScenesExceptMainMenu();
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

    // Add users message to chat area, chat log, and other scenes
    updateChat("\n\n<- ", new ChatMessage("user", message));

    // Get response for users message from GPT model
    Task<ChatMessage> chatTask = ChatTaskGenerator.createTask(message);
    new Thread(chatTask).start();

    setThinkingAnimation(true);
    chatTask.setOnSucceeded(
        e -> {
          setThinkingAnimation(false);
          updateChat("\n\n-> ", chatTask.getValue());
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
      CHARACTER_DELAY = (50 - (ch.length / 2)) + 5;
    }
    Duration delayBetweenCharacters = Duration.millis(CHARACTER_DELAY);
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

  /** Function to create task to update chat area for scene. */
  public void createUpdateTask() {
    // Create task to append chat log to chat area
    updateChatTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // Append chat log to chat area
            chatArea.setText(GameState.chatLog);
            chatArea.appendText("");

            // Create new task to update chat area
            createUpdateTask();
            return null;
          }
        };
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
    new Thread(TimemachineController.updateChatTask).start();
  }

  /** Function to handle when the user wins the minigame. */
  private void winGame() {
    // Hide minigame elements
    circuitGameBg.setVisible(false);
    circuitGameImg.setVisible(false);
    memoryGame.setVisible(false);
    btnStartCircuitGame.setVisible(false);
    text.setVisible(false);
    info.setVisible(false);
    circuitBox.setVisible(false);
    btnSwitchToTimeMachine.setDisable(false);

    // Show storage room elements previously hidden by minigame
    background.setVisible(true);
    circuitBoxImg.setVisible(true);

    // Update game state
    GameState.isStorageResolved = true;

    // Send complete message
    Task<ChatMessage> storageTaskComplete =
        ChatTaskGenerator.createTask(GptPromptEngineering.getStorageComplete());
    new Thread(storageTaskComplete).start();

    setThinkingAnimation(true);
    storageTaskComplete.setOnSucceeded(
        e -> {
          setThinkingAnimation(false);
          updateChat("\n\n-> ", storageTaskComplete.getValue());
        });
  }

  /** Function to handle the next round of minigame. */
  private void nextTurn() {
    counter = 0;
    consecutiveRounds++; // Increase consecutive rounds on successful turn
    turn++;

    // Add next button to pattern for user to memorise
    pattern.add(possibleButtons.get(random.nextInt(possibleButtons.size())));
    showPattern();

    // Update current streak user is on
    text.setText("Current Streak: " + consecutiveRounds);
  }

  /** Function to handle when the minigame must be reset. */
  private void resetGame() {
    consecutiveRounds = 0; // Reset consecutive rounds
    text.setText("Wrong - Start Again");
    pattern.clear();
    counter = 0;
    turn = 1;
  }

  /**
   * Function to get the index of minigame button via ActionEvent.
   *
   * @param event the action event triggered by the button press
   */
  private int getIndexOfButton(ActionEvent event) {
    // Get source of click event
    String buttonId = ((Control) event.getSource()).getId();
    return Integer.parseInt(buttonId.substring(buttonId.length() - 1));
  }

  /**
   * Function to get the index of minigame button via String.
   *
   * @param button the button to get the index of
   */
  private int getIndexOfButton(String button) {
    return Integer.parseInt(button.substring(button.length() - 1));
  }

  /** Function to show the pattern to the user that they have to memorise for current round. */
  private void showPattern() {
    buttonsDisabled = true; // Disable buttons during pattern display

    // Initialise pause transition
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
          timeline.setOnFinished(
              event -> {
                buttonsDisabled = false; // Enable buttons after pattern display
              });
          timeline.play();
        });
    pause.play();
  }

  /** Function to show next button to press in the pattern order. */
  private void showNext() {
    // Get next button in pattern and indicate by changing colour
    Button button = buttons.get(getIndexOfButton(pattern.get(patternOrder)));
    changeButtonColor(
        button, "-fx-background-color: rgb(55,130,91); -fx-border-color: rgb(28,28,28);");
    patternOrder++;

    if (patternOrder == turn) {
      patternOrder = 0;
    }
  }

  /**
   * Function to change colour of minigame button to desired colour, then reset after delay.
   *
   * @param button the button to change the colour of
   * @param color the colour to change the button to
   */
  private void changeButtonColor(Button button, String color) {
    // Apply style of desired color
    button.setStyle(color);

    // Initialise pause transition to change colour back
    PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
    pause.setOnFinished(
        e -> {
          // Change back to original color
          button.setStyle(
              "-fx-background-color: rgb(107,249,177); -fx-border-color: rgb(28,28,28);");
        });

    // Start transition
    pause.play();
  }
}
