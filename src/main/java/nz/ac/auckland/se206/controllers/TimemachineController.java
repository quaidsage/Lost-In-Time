package nz.ac.auckland.se206.controllers;

import java.io.IOException;

import javafx.animation.PathTransition;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.Delay;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.RestartManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.ChatTaskGenerator;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.speech.TextToSpeech;

/** A controller class for the time machine scene. */
public class TimemachineController {
  public static ChatMessage chatTaskValue;
  public static Task<Void> startTask;
  public static BooleanProperty appendContextProperty = new SimpleBooleanProperty(false);
  public static TimerController timer = new TimerController();

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
    LabController.labStartTimer(IntroController.minutes);
    StorageController.storageStartTimer(IntroController.minutes);
  }

  /** Function to create an animation of the lights turning on. */
  private static void animateLights(Rectangle rectLight) {
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
  @FXML private ImageView typingBubble;
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
  @FXML private Button btnCloseDropdownMenu;
  @FXML private Button btnOpenDropdownMenu;

  private Circle currentCircle;
  private int res;

  /** Carries out specific tasks required when opening the scene. */
  public void initialize() {

    // Initialise timer and bind the lblTimer to the timerController properties.
    timer = new TimerController();
    lblTimer.textProperty().bind(timer.messageProperty());
    timer.setOnSucceeded(
        e -> {
          lblTimer.setText("0:00");
        });

    // Initialise relevant tasks
    initialiseTasks();

    GameState.isControlBoxResolved = false;

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
    if (GameState.isLabResolved && GameState.isStorageResolved && GameState.isControlBoxResolved) {
      App.setUi(AppUi.ENDSCENE);
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
    hackInstructions.setVisible(true);
  }

  @FXML
  private void onClickHideInstructions(ActionEvent event) {
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
    ChatTaskGenerator.onSendMessage(chatField);
  }

  // selects the smallest circle that is, the top ring, and makes it glow to show it is selected
  @FXML
  private void selectCircle(MouseEvent event) {
    if (currentCircle == null) {
      currentCircle = getCircle((Circle) event.getPickResult().getIntersectedNode());
      Glow glow = new Glow();
      glow.setLevel(0.5);
      currentCircle.setEffect(glow);
    } else {

      if (currentCircle
              .getId()
              .compareTo(getCircle((Circle) event.getPickResult().getIntersectedNode()).getId())
          > 0) {
        String row = event.getPickResult().getIntersectedNode().getParent().getId();
        if (row.equals("row1")) {
          row1.getChildren().add(currentCircle);
        } else if (row.equals("row2")) {
          row2.getChildren().add(currentCircle);
        } else if (row.equals("row3")) {
          row3.getChildren().add(currentCircle);
        }
      }
      currentCircle.setEffect(null);
      currentCircle = null;
    }

    if (row3.getChildren().size() == res) {
      winGame();
    }
  }

  private void winGame() {
    hackGame.setVisible(false);
    desktopView.setVisible(false);
    btnControlBox.setVisible(false);
    GameState.isControlBoxResolved = true;
  }

  // drops circle on both blank as well as a non blank row appropriately
  @FXML
  private void dropCircle(MouseEvent event) {
    String row = event.getPickResult().getIntersectedNode().getId();

    if (row.equals("row1")) {
      if (currentCircle == null) return;
      else if (row1.getChildren().size() == 0) {
        row1.getChildren().add(currentCircle);
        currentCircle.setEffect(null);
        currentCircle = null;
      } else {
        if (currentCircle.getId().compareTo(getCircle((Circle) row1.getChildren().get(0)).getId())
            > 0) {
          row1.getChildren().add(currentCircle);
          currentCircle.setEffect(null);
          currentCircle = null;
        } else {
          currentCircle.setEffect(null);
          currentCircle = null;
        }
      }
    } else if (row.equals("row2")) {
      if (currentCircle == null) return;
      else if (row2.getChildren().size() == 0) {
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
      if (currentCircle == null) return;
      else if (row3.getChildren().size() == 0) {
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

  // gets the smallest circle of the row of whichever circle you press
  // that is you don't want to select a bigger circle when a smaller circle is already present
  @FXML
  private Circle getCircle(Circle circle) {
    Circle toChoseCircle = circle;
    StackPane row = (StackPane) circle.getParent();
    for (int i = 0; i < row.getChildren().size(); i++) {
      if (row.getChildren().get(i).getId().compareTo(toChoseCircle.getId()) > 0)
        toChoseCircle = (Circle) row.getChildren().get(i);
    }
    return toChoseCircle;
  }

  /** Function to create tasks to update elements outside class controller. */
  public void initialiseTasks() {
    // Set chat area
    ChatTaskGenerator.chatAreas.add(chatArea);

    // Set send button
    ChatTaskGenerator.sendButtons.add(btnSend);

    // Set thinking animation
    ChatTaskGenerator.thinkingAnimationImages.add(imgScientistThinking);
    ChatTaskGenerator.thinkingAnimationImages.add(typingBubble);

    // Set timer label and light rectangle to restart manager
    RestartManager.timemachineLabel = lblTimer;
    RestartManager.timemachineRect = rectLight;

    createStartTask(rectLight, lblTimer);
  }

  @FXML
  private void openDropdownMenu(ActionEvent event) {
    openDropdownMenuAnimation(300);
  }

  @FXML
  private void closeDropdownMenu(ActionEvent event) {
    closeDropdownMenuAnimation(300);
  }

  private void openDropdownMenuAnimation(int duration) {
    Line lineAcross = new Line(-133, 375, 133, 375);
    Duration duration2 = Duration.millis(duration);
    new PathTransition(duration2, lineAcross, dropdownMenu).play();
  }

  private void closeDropdownMenuAnimation(int duration) {
    Line lineAcross = new Line(133, 375, -133, 375);
    Duration duration2 = Duration.millis(duration);
    new PathTransition(duration2, lineAcross, dropdownMenu).play();
  }
}