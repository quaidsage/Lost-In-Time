package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
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
import nz.ac.auckland.se206.speech.TextToSpeech;

/** A controller class for the time machine scene. */
public class TimemachineController {
  public static ChatMessage chatTaskValue;
  public static Task<Void> startTask;
  public static BooleanProperty appendContextProperty = new SimpleBooleanProperty(false);
  public static TimerController timer = new TimerController();
  public static TaskController taskController;

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

  /** Function to animate the start of the round. */
  public static void startRound(Rectangle rectLight, Label lblTimer) {

    // Light flash animation
    animateLights(rectLight);

    // Start timer. Change 'minutes' variable to change the length of the game
    lblTimer.setVisible(true);
    timemachineStartTimer(IntroController.minutes);
    StorageController.storageStartTimer(IntroController.minutes);
    LabController.labStartTimer(IntroController.minutes);
  }

  /** Function to create an animation of the lights turning on. */
  private static void animateLights(Rectangle rectLight) {
    App.audio.playLights();

    // Start animation and set the rectangle to visible
    rectLight.setVisible(true);
    // Start a series of delays to turn the light on and off
    delay(
        800,
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
                                200,
                                () -> {
                                  // Finish the animation.
                                  rectLight.setVisible(false);
                                });
                          });
                    });
              });
        });
  }

  public static void createStartTask(Rectangle rectLight, Label lblTimer) {
    // Create task to start round
    startTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            startRound(rectLight, lblTimer);
            return null;
          }
        };
  }

  // JavaFX elements
  @FXML private Button btnSwitchToLab;
  @FXML private Button btnSwitchToStorage;
  @FXML private Button btnSend;
  @FXML private Label lblTimer;
  @FXML private Label lblTaskList;
  @FXML private TextArea chatArea;
  @FXML private TextArea chatField;
  @FXML private ImageView imgScientistThinking;
  @FXML private Rectangle rectLight;
  @FXML private Button btnTimeMachine;
  @FXML private Button btnMenu;
  @FXML private Circle circle1;
  @FXML private Circle circle2;
  @FXML private Circle circle3;
  @FXML private Circle circle4;
  @FXML private StackPane row1;
  @FXML private StackPane row2;
  @FXML private StackPane row3;
  @FXML private Rectangle btnHackFile;
  @FXML private Rectangle fileNameBg;
  @FXML private Pane hackGame;
  @FXML private Pane desktopView;
  @FXML private Button btnControlBox;
  @FXML private Button btnHelp;
  @FXML private Pane hackInstructions;
  @FXML private Pane dropdownMenu;
  @FXML private Pane menuOverlay;
  @FXML private Button btnCloseDropdownMenu;
  @FXML private Button btnOpenDropdownMenu;
  @FXML private Circle task1Circle;
  @FXML private Circle task2Circle;
  @FXML private Circle task3Circle;
  @FXML private Text txtTask1;
  @FXML private Text txtTask2;
  @FXML private Text txtTask3;

  private Circle currentCircle;
  private int res;
  private MenuController menuController;

  /** Carries out specific tasks required when opening the scene. */
  public void initialize() {

    // Initialise controller instances
    menuController = new MenuController(dropdownMenu);
    taskController = new TaskController();

    // Bind the circle properties to the task list.
    task1Circle
        .fillProperty()
        .bind(
            Bindings.when(taskController.labTaskCompletedProperty())
                .then(Color.GREEN)
                .otherwise(Color.TRANSPARENT));
    task2Circle
        .fillProperty()
        .bind(
            Bindings.when(taskController.storageTaskCompletedProperty())
                .then(Color.GREEN)
                .otherwise(Color.TRANSPARENT));
    task3Circle
        .fillProperty()
        .bind(
            Bindings.when(taskController.controlBoxTaskCompletedProperty())
                .then(Color.GREEN)
                .otherwise(Color.TRANSPARENT));

    // Bind the task list text to the gamestate variables.
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

    // Initialise timer and bind the lblTimer to the timerController properties.
    timer = new TimerController();
    lblTimer.textProperty().bind(timer.messageProperty());
    timer.setOnSucceeded(
        e -> {
          lblTimer.setText("0:00");
        });
    timer.setOnCancelled(
        e -> {
          timer.reset();
        });

    // Initialise relevant tasks
    initialiseTasks();

    // Init gamestate variable.
    GameState.isControlBoxResolved = false;

    // Set the circle radii, and currentCircle to null.
    circle1.setRadius(110);
    circle2.setRadius(95);
    circle3.setRadius(80);
    circle4.setRadius(65);
    currentCircle = null;
    res = row1.getChildren().size();

    // Create listener to when ready to append context
    ChangeListener<Boolean> changeListener =
        new ChangeListener<Boolean>() {
          @Override
          public void changed(
              ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            // Append context to chat and if unmuted, use TTS
            ChatTaskGenerator.updateChat("-> ", ChatTaskGenerator.contextResponse);
            if (!MainmenuController.isTTSMuted) {
              // Run the text to speech
              TextToSpeech.runTextToSpeech(ChatTaskGenerator.contextResponse.getContent());
            }
          }
        };
    appendContextProperty.addListener(changeListener);
  }

  /**
   * Changes scene to the lab without resetting the scene.
   *
   * @param event the action event triggered by the send button
   */
  @FXML
  private void onClickLab(ActionEvent event) {
    // Play audio
    App.audio.playClick();
    // Change UI and run animation
    App.setUi(AppUi.LAB);
    AnimationManager.openLabDoor();
    // Handle the gamestate variables
    if (!GameState.isLabVisited) {
      GameState.isLabVisited = true;
      Thread labIntroThread = new Thread(LabController.labIntroTask);
      labIntroThread.setDaemon(true);
      labIntroThread.start();
    }
  }

  /**
   * Change the scene to storage without resetting any scenes.
   *
   * @param event the action event triggered by the send button
   */
  @FXML
  private void onClickStorage(ActionEvent event) {
    // Play a click sound
    App.audio.playClick();

    // Set the UI to the storage scene
    App.setUi(AppUi.STORAGE);

    // Open the storage door using an animation
    AnimationManager.openStorageDoor();

    // Check if the storage has not been visited yet
    if (!GameState.isStorageVisited) {
      // Mark the storage as visited
      GameState.isStorageVisited = true;

      // Start a separate thread to run the storage intro task
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
    if (GameState.isLabResolved && GameState.isStorageResolved && GameState.isControlBoxResolved) {
      App.audio.playClick();
      timer.cancel();
      App.setUi(AppUi.ENDSCENE);
      delay(1000, () -> App.audio.playSuccess());
    }
  }

  /**
   * Function that handles if the control box is clicked.
   *
   * @param event the action event triggered by the send button
   */
  @FXML
  private void onClickControlBox(ActionEvent event) {
    // Check if other two tasks are complete
    if (GameState.isLabResolved && GameState.isStorageResolved) {
      App.audio.playClick();
      desktopView.setVisible(true);
    }
  }

  /**
   * Function that handles if the hack file is clicked.
   *
   * @param event the action event triggered by the send button
   */
  @FXML
  private void onClickHackFile(MouseEvent event) {
    App.audio.playClick();
    // Check if game is complete
    hackGame.setVisible(true);
  }

  /**
   * Function that handles if the help button is clicked.
   *
   * @param event the action event triggered by the send button
   */
  @FXML
  private void onClickHelp(ActionEvent event) {
    App.audio.playClick();
    hackInstructions.setVisible(true);
  }

  @FXML
  private void onClickHideInstructions(ActionEvent event) {
    App.audio.playClick();
    hackInstructions.setVisible(false);
  }

  /**
   * Function to handle returning to main menu.
   *
   * @param event the action event triggered by the send button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onClickReturn(ActionEvent event) throws IOException {
    // Play audio.
    App.audio.playClick();
    menuOverlay.setVisible(false);
    menuController.closeMenu();

    // Cancel timer and reset UI to the mainmenu.
    timer.cancel();
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

  /// This method selects the smallest circle, representing the top ring, and makes it glow to
  // indicate selection
  @FXML
  private void selectCircle(MouseEvent event) {
    // Play a click sound
    App.audio.playClick();

    if (currentCircle == null) {
      // If no circle is currently selected, select the one clicked
      currentCircle = getCircle((Circle) event.getPickResult().getIntersectedNode());

      // Add a glow effect to the selected circle
      Glow glow = new Glow();
      glow.setLevel(0.5);
      currentCircle.setEffect(glow);
    } else {
      // If a circle is already selected, check if the newly clicked circle is smaller
      if (currentCircle
              .getId()
              .compareTo(getCircle((Circle) event.getPickResult().getIntersectedNode()).getId())
          > 0) {
        // Determine which row to move the circle to based on the clicked circle's parent
        String row = event.getPickResult().getIntersectedNode().getParent().getId();
        if (row.equals("row1")) {
          row1.getChildren().add(currentCircle);
        } else if (row.equals("row2")) {
          row2.getChildren().add(currentCircle);
        } else if (row.equals("row3")) {
          row3.getChildren().add(currentCircle);
        }
      }

      // Remove the glow effect and reset the current selection
      currentCircle.setEffect(null);
      currentCircle = null;
    }

    // Check if all circles are in row3, which indicates a win
    if (row3.getChildren().size() == res) {
      winGame();
    }
  }

  /** Function to handle the completion of the game. */
  private void winGame() {
    // Play audio
    App.audio.playSuccess();

    // Set components visibility
    hackGame.setVisible(false);
    desktopView.setVisible(false);
    btnControlBox.setVisible(false);
    GameState.isControlBoxResolved = true;
    TaskController.completeTask3();

    // Get AI response for completing task
    Task<ChatMessage> hackTaskComplete =
        ChatTaskGenerator.createTask(GptPromptEngineering.getHackComplete());
    hackTaskComplete.setOnSucceeded(
        e -> {
          ChatTaskGenerator.updateChat("\n\n-> ", hackTaskComplete.getValue());
        });
  }

  /**
   * Drops circle on both blank as well as a non blank row appropriately.
   *
   * @param event event handler for when mouse event occurs.
   */
  @FXML
  private void dropCircle(MouseEvent event) {
    // Play a click sound
    App.audio.playClick();

    // Get the ID of the row where the circle is being dropped
    String row = event.getPickResult().getIntersectedNode().getId();

    // Handle events within the towers of Hanoi hacking game.
    if (row.equals("row1")) {
      // If there is no currently held circle, return
      if (currentCircle == null) {
        return;
      }
      // If the row is empty, add the current circle and reset it
      else if (row1.getChildren().size() == 0) {
        row1.getChildren().add(currentCircle);
        currentCircle.setEffect(null);
        currentCircle = null;
      } else {
        // If the current circle's ID is greater than the top circle's ID, add it and reset
        if (currentCircle.getId().compareTo(getCircle((Circle) row1.getChildren().get(0)).getId())
            > 0) {
          row1.getChildren().add(currentCircle);
          currentCircle.setEffect(null);
          currentCircle = null;
        } else {
          // If the current circle can't be added, just reset it
          currentCircle.setEffect(null);
          currentCircle = null;
        }
      }
    } else if (row.equals("row2")) {
      // Handle events for "row2" - similar logic as for "row1"
      if (currentCircle == null) {
        return;
      }
      if (row2.getChildren().size() == 0) {
        row2.getChildren().add(currentCircle);
        currentCircle.setEffect(null);
        currentCircle = null;
      } else {
        if (currentCircle.getId().compareTo(getCircle((Circle) row2.getChildren().get(0)).getId())
            > 0) {
          row2.getChildren().add(currentCircle);
          currentCircle.setEffect(null);
          currentCircle = null;
        } else {
          currentCircle.setEffect(null);
          currentCircle = null;
        }
      }
    } else if (row.equals("row3")) {
      // Handle events for "row3" - similar logic as for "row1"
      if (currentCircle == null) {
        return;
      }
      if (row3.getChildren().size() == 0) {
        row3.getChildren().add(currentCircle);
        currentCircle.setEffect(null);
        currentCircle = null;
      } else {
        if (currentCircle.getId().compareTo(getCircle((Circle) row3.getChildren().get(0)).getId())
            > 0) {
          row3.getChildren().add(currentCircle);
          currentCircle.setEffect(null);
          currentCircle = null;
        } else {
          currentCircle.setEffect(null);
          currentCircle = null;
        }
      }
    }
  }

  /**
   * Gets the smallest circle of the row of whichever circle you press.
   *
   * @param circle takes a javafx Circle as input.
   * @return returns updated Circle element.
   */
  @FXML
  private Circle getCircle(Circle circle) {
    // Initialise values
    Circle toChoseCircle = circle;
    StackPane row = (StackPane) circle.getParent();
    // Get the smallest circle
    for (int i = 0; i < row.getChildren().size(); i++) {
      if (row.getChildren().get(i).getId().compareTo(toChoseCircle.getId()) > 0) {
        toChoseCircle = (Circle) row.getChildren().get(i);
      }
    }
    return toChoseCircle;
  }

  /**
   * Opens the dropdown menu in response to an action event.
   *
   * @param event The action event that triggered this method.
   */
  @FXML
  private void openDropdownMenu(ActionEvent event) {
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
  private void closeDropdownMenu(ActionEvent event) {
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

  /** Function to create tasks to update elements outside class controller. */
  public void initialiseTasks() {
    // Set chat area
    ChatTaskGenerator.chatAreas.add(chatArea);

    // Set text field
    ChatTaskGenerator.chatFields.add(chatField);

    // Set send button
    ChatTaskGenerator.sendButtons.add(btnSend);

    // Set thinking animation
    ChatTaskGenerator.thinkingAnimationImages.add(imgScientistThinking);

    // Set timer label and light rectangle to restart manager
    RestartManager.timemachineLabel = lblTimer;
    RestartManager.timemachineRect = rectLight;

    createStartTask(rectLight, lblTimer);
  }
}
