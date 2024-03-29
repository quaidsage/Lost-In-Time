package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import nz.ac.auckland.se206.AnimationManager;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.Delay;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.RestartManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.ChatTaskGenerator;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

/** A controller class for the storage scene. */
public class StorageController {
  public static Task<ChatMessage> storageIntroTask;
  public static int patternOrder = 0;
  public static int counter = 0;
  public static int turn = 1;
  public static int consecutiveRounds = 0;

  // Initialise Timer
  public static TimerController timer = new TimerController();

  public static TaskController taskController;

  /**
   * Delays given code by a given number of milliseconds.
   *
   * @param ms milliseconds of delay
   * @param continuation Code to execute after delay
   */
  public static void delay(int ms, Runnable continuation) {
    Task<Void> delayTask = Delay.createDelay(ms);
    delayTask.setOnSucceeded(event -> continuation.run());
    Thread delayThread = new Thread(delayTask);
    delayThread.setDaemon(true);
    delayThread.start();
  }

  /**
   * Function to start the time in the timemachinescene.
   *
   * @param minutes the number of minutes to set the timer to.
   */
  public static void storageStartTimer(int minutes) {
    timer.setMinutes(minutes);
    timer.start();
  }

  // JavaFX elements
  @FXML private Button btnSwitchToTimeMachine;
  @FXML private Button btnSend;
  @FXML private Rectangle circuitBox;
  @FXML private ImageView imgStorageDoor;
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
  @FXML private Text info2;
  @FXML private Circle circuitLed;
  @FXML private Pane dropdownMenu;
  @FXML private Pane menuOverlay;
  @FXML private Button btnCloseDropDownMenu;
  @FXML private Button btnOpenDropDownMenu;
  @FXML private Text txtTaskList;
  @FXML private Circle task1CircleStorage;
  @FXML private Circle task2CircleStorage;
  @FXML private Circle task3CircleStorage;
  @FXML private Text txtTask1;
  @FXML private Text txtTask2;
  @FXML private Text txtTask3;

  // Initialise Variables
  private ArrayList<Button> buttons = new ArrayList<>();
  private ArrayList<String> pattern = new ArrayList<>();
  private MenuController menuController;

  private Random random = new Random();
  private int targetConsecutiveRounds = 3;
  private boolean buttonsDisabled = false;
  private ArrayList<String> possibleButtons =
      new ArrayList<>(
          Arrays.asList(
              "button0", "button1", "button2", "button3", "button4", "button5", "button6",
              "button7", "button8"));

  /**
   * Initialises the storage scene with the required settings.
   *
   * @throws ApiProxyException when there is a problem with the ApiProxy.
   */
  public void initialize() throws ApiProxyException {
    taskController = new TaskController();
    menuController = new MenuController(dropdownMenu);

    // Bind the properties of the task list circles to gamestate variables.
    task1CircleStorage
        .fillProperty()
        .bind(
            Bindings.when(taskController.labTaskCompletedProperty())
                .then(Color.GREEN)
                .otherwise(Color.TRANSPARENT));
    task2CircleStorage
        .fillProperty()
        .bind(
            Bindings.when(taskController.storageTaskCompletedProperty())
                .then(Color.GREEN)
                .otherwise(Color.TRANSPARENT));
    task3CircleStorage
        .fillProperty()
        .bind(
            Bindings.when(taskController.controlBoxTaskCompletedProperty())
                .then(Color.GREEN)
                .otherwise(Color.TRANSPARENT));

    // Bind the properties of the task list text to gamestate variables.
    txtTask1
        .styleProperty()
        .bind(
            Bindings.when(taskController.labTaskCompletedProperty())
                .then("-fx-strikethrough: true; -fx-font-size: 16px;")
                .otherwise("-fx-strikethrough: false; -fx-font-size: 16px;"));
    txtTask2
        .styleProperty()
        .bind(
            Bindings.when(taskController.storageTaskCompletedProperty())
                .then("-fx-strikethrough: true; -fx-font-size: 16px;")
                .otherwise("-fx-strikethrough: false; -fx-font-size: 16px;"));
    txtTask3
        .styleProperty()
        .bind(
            Bindings.when(taskController.controlBoxTaskCompletedProperty())
                .then("-fx-strikethrough: true; -fx-font-size: 16px;")
                .otherwise("-fx-strikethrough: false; -fx-font-size: 16px;"));

    // Initialise timer
    timer = new TimerController();
    lblTimer.textProperty().bind(timer.messageProperty());
    timer.setOnCancelled(
        e -> {
          timer.reset();
        });

    initialiseTasks();

    buttons.addAll(
        Arrays.asList(
            button0, button1, button2, button3, button4, button5, button6, button7, button8));

    startFlashing();
  }

  /**
   * Function to handle when a minigame button is pressed.
   *
   * @param event the action event triggered by the button press
   */
  @FXML
  private void onClickPanel(ActionEvent event) {
    App.audio.playKeypad();
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
    App.audio.playClick();
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
    App.audio.playClick();
    App.audio.playStorageDoorClose();
    App.setUi(AppUi.TIMEMACHINE);
  }

  /** Function to handle when the circuit minigame is opened. */
  @FXML
  private void onClickCircuit(MouseEvent event) {
    App.audio.playClick();
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
    info2.setVisible(true);
    btnSwitchToTimeMachine.setDisable(true);
  }

  /**
   * Opens the dropdown menu in response to an action event.
   *
   * @param event The action event that triggered this method.
   */
  @FXML
  private void openDropDownMenu(ActionEvent event) {
    App.audio.playClick();
    // Call the openMenu method in the MenuController to open the dropdown menu.
    menuOverlay.setVisible(true);
    menuController.openMenu();
  }

  /**
   * Closes the dropdown menu in response to an action event.
   *
   * @param event The action event that triggered this method.
   */
  @FXML
  private void closeDropDownMenu(ActionEvent event) {
    App.audio.playClick();
    // Call the closeMenu method in the MenuController to close the dropdown menu.
    menuOverlay.setVisible(false);
    menuController.closeMenu();
  }

  /**
   * Closes the dropdown menu in response to an action event.
   *
   * @param event The action event that triggered this method.
   */
  @FXML
  private void closeDropdownMenuOverlay(MouseEvent event) {
    App.audio.playClick();
    // Call the closeMenu method in the MenuController to close the dropdown menu.
    menuOverlay.setVisible(false);
    menuController.closeMenu();
  }

  /**
   * Function to handle returning to main menu.
   *
   * @param event the action event triggered by the menu button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onClickReturn(ActionEvent event) throws IOException {
    App.audio.playClick();
    timer.cancel();
    menuOverlay.setVisible(false);
    menuController.closeMenu();
    App.setUi(AppUi.MAINMENU);
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
    App.audio.playClick();
    ChatTaskGenerator.onSendMessage(chatField);
  }

  /** Function to handle the flashing animation. */
  private void startFlashing() {
    FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), circuitLed);
    fadeTransition.setFromValue(1.0);
    fadeTransition.setToValue(0.0);
    fadeTransition.setAutoReverse(true);
    fadeTransition.setCycleCount(FadeTransition.INDEFINITE);
    fadeTransition.play();
  }

  /** Function to handle when the user wins the minigame. */
  private void winGame() {
    App.audio.playSuccess();

    // Hide minigame elements
    circuitGameBg.setVisible(false);
    circuitGameImg.setVisible(false);
    memoryGame.setVisible(false);
    btnStartCircuitGame.setVisible(false);
    text.setVisible(false);
    info.setVisible(false);
    info2.setVisible(false);
    circuitBox.setVisible(false);
    btnSwitchToTimeMachine.setDisable(false);

    // Show storage room elements previously hidden by minigame
    background.setVisible(true);
    circuitBoxImg.setVisible(true);

    // Update game state
    GameState.isStorageResolved = true;
    TaskController.completeTask2();

    // Get AI response for completing task
    Task<ChatMessage> storageTaskComplete =
        ChatTaskGenerator.createTask(GptPromptEngineering.getStorageComplete());
    storageTaskComplete.setOnSucceeded(
        e -> {
          ChatTaskGenerator.updateChat("\n\n-> ", storageTaskComplete.getValue());
        });
    Thread storageTaskCompleteThread = new Thread(storageTaskComplete);
    storageTaskCompleteThread.setDaemon(true);
    storageTaskCompleteThread.start();
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
    App.audio.playFail();
    consecutiveRounds = 0; // Reset consecutive rounds
    text.setText("Wrong - Click Start to try again");
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
    PauseTransition pause = new PauseTransition(Duration.seconds(0.65));
    pause.setOnFinished(
        e -> {
          Timeline timeline =
              new Timeline(
                  new KeyFrame(
                      Duration.seconds(0.65),
                      event -> {
                        App.audio.playPattern();
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

  /** Function to initialise the tasks for the storage scene. */
  private void initialiseTasks() {
    // Set chat area
    ChatTaskGenerator.chatAreas.add(chatArea);

    // Set text field
    ChatTaskGenerator.chatFields.add(chatField);

    // Set send button
    ChatTaskGenerator.sendButtons.add(btnSend);

    // Set thinking animation
    ChatTaskGenerator.thinkingAnimationImages.add(imgScientistThinking);

    // Add timer label and minigame elements to restart manager
    RestartManager.storageLabel = lblTimer;
    RestartManager.storageElements =
        new Object[] {
          background,
          circuitBox,
          circuitBoxImg,
          circuitGameBg,
          circuitGameImg,
          memoryGame,
          btnStartCircuitGame,
          text,
          info,
          info2,
          btnSwitchToTimeMachine
        };

    // Initialise door to animation manager
    AnimationManager.imgStorageDoor = imgStorageDoor;

    // Get introduction message on first visit of storage room
    storageIntroTask = ChatTaskGenerator.createTask(GptPromptEngineering.getStorageIntro());
    storageIntroTask.setOnSucceeded(
        e -> {
          ChatTaskGenerator.updateChat("\n\n-> ", storageIntroTask.getValue());
        });
  }
}
