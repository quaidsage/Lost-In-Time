package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
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

/** A controller class for the lab scene. */
public class LabController {
  public static int numHints = 5;
  public static ArrayList<Integer> solutionColours;

  // Fields related to Task
  public static Task<ChatMessage> labIntroTask;
  public static Task<ChatMessage> labRiddleTask;
  public static Task<Void> animateTask;
  public static Task<Void> updateHintTask;

  // Initialise Timer
  private static TimerController timer = new TimerController();

  /**
   * Function to start the scenes timer when the game is started.
   *
   * @param minutes the number of minutes to set the timer to.
   */
  public static void labStartTimer(int minutes) {
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

  // Fields for JavaFX elements
  @FXML private Pane paneLab;
  @FXML private Button btnSwitchToTimeMachine;
  @FXML private Button btnSend;
  @FXML private Label lblTimer;
  @FXML private Label hintsRemaining;
  @FXML private TextArea chatArea;
  @FXML private TextArea chatField;
  @FXML private ImageView imgScientistThinking;
  @FXML private Button btnMenu;
  @FXML private Polyline chemicalGeneral;
  @FXML private Rectangle chemicalCyan;
  @FXML private Rectangle chemicalBlue;
  @FXML private Rectangle chemicalPurple;
  @FXML private Rectangle chemicalOrange;
  @FXML private Rectangle chemicalYellow;
  @FXML private Rectangle chemicalGreen;
  @FXML private Rectangle chemicalRed;
  @FXML private Rectangle transitionScene;
  @FXML private ImageView baseImage;
  @FXML private ImageView blurredImage;
  @FXML private ImageView typingBubble;

  private ArrayList<ImageView> arrowCollection = new ArrayList<ImageView>();

  // Fields for initializing variables
  private int numChemicalsAdded = 0;
  private int arrowAnimationSpeed = 85;
  private int arrowAnimationDistance = 25;
  private int fadeTransitionSpeed = 1500;
  private Duration flashDuration = Duration.millis(0);
  private int numFlashes = 0;

  // Fields related to chemical solutions
  private Boolean[] isChemicalSolution = {false, false, false, false, false, false, false};

  /**
   * Initialise the scene with the specific settings.
   *
   * @throws ApiProxyException if there is a problem with the API proxy.
   */
  public void initialize() throws ApiProxyException {
    // Initialise timer and bind the lblTimer to the timerController properties.
    timer = new TimerController();
    lblTimer.textProperty().bind(timer.messageProperty());
    timer.setOnSucceeded(
        e -> {
          App.setUi(AppUi.TIMEOUT);
          timer.reset();
        });

    // Create task to run GPT model for intro message
    labIntroTask = ChatTaskGenerator.createTask(GptPromptEngineering.getLabIntro());
    labIntroTask.setOnSucceeded(
        e -> {
          ChatTaskGenerator.updateChat("\n\n-> ", labIntroTask.getValue());
          chemicalGeneral.setVisible(true);
        });

    // Set solution chemicals
    ArrayList<Integer> list = new ArrayList<Integer>();
    for (int i = 0; i < 7; i++) {
      list.add(i);
    }
    Collections.shuffle(list);
    solutionColours = new ArrayList<Integer>();
    for (int i = 0; i < 3; i++) {
      solutionColours.add(list.get(i));
      isChemicalSolution[solutionColours.get(i)] = true;
    }

    initialiseTasks();

    initialiseElements();
  }

  /**
   * Change scene to Time Machine.
   *
   * @param event the action event triggered by the time machine button.
   */
  @FXML
  private void onClickTimeMachineRoom(ActionEvent event) {
    App.setUi(AppUi.TIMEMACHINE);
  }

  /**
   * Function to return to main menu, restarting game.
   *
   * @param event the action event triggered by the main menu button.
   */
  @FXML
  private void onClickReturn(ActionEvent event) throws IOException {
    App.setRoot("mainmenu");
  }

  /**
   * Function to begin lab riddle.
   *
   * @param event the action event triggered by the begin button.
   */
  @FXML
  private void onClickChemicals(MouseEvent event) {
    if (GameState.isDifficultyMedium == true) {
      numHints = 5;
      hintsRemaining.setText("Hints Remaining: " + String.valueOf(numHints));
    } else if (GameState.isDifficultyEasy == true) {
      hintsRemaining.setText("Unlimited hints available");
    } else {
      hintsRemaining.setText("No hints available");
    }

    if (GameState.isLabResolved) {
      return;
    }

    // Prevent user from leaving lab until riddle is solved
    chemicalGeneral.setVisible(false);
    btnSwitchToTimeMachine.setDisable(true);

    // Create task to run GPT model for riddle message
    labRiddleTask =
        ChatTaskGenerator.createTask(
            GptPromptEngineering.getRiddleLab(LabController.solutionColours));
    Thread labRiddleThread = new Thread(labRiddleTask);
    labRiddleThread.setDaemon(true);
    labRiddleThread.start();
    labRiddleTask.setOnSucceeded(
        e -> {
          ChatTaskGenerator.updateChat("\n\n-> ", labRiddleTask.getValue());
        });
  }

  /**
   * Function to correctly identify which chemical is clicked.
   *
   * @param event the action event triggered by the chemical being clicked.
   */
  @FXML
  private void onClickChemical(MouseEvent event) {
    // Get source of click
    Rectangle src = (Rectangle) event.getSource();

    // Access appropriate arrows
    switch (src.getId()) {
      case "chemicalBlue":
        chemClicked(0);
        break;
      case "chemicalPurple":
        chemClicked(1);
        break;
      case "chemicalCyan":
        chemClicked(2);
        break;
      case "chemicalGreen":
        chemClicked(3);
        break;
      case "chemicalYellow":
        chemClicked(4);
        break;
      case "chemicalOrange":
        chemClicked(5);
        break;
      case "chemicalRed":
        chemClicked(6);
        break;
    }
  }

  /**
   * Function to correctly identify which chemical is hovered.
   *
   * @param event the action event triggered by the chemical being hovered.
   */
  @FXML
  private void onMouseEnterChemical(MouseEvent event) {
    // Get source of click
    Rectangle src = (Rectangle) event.getSource();

    // Animate appropriate arrows
    switch (src.getId()) {
      case "chemicalBlue":
        chemAnimate(0, true);
        break;
      case "chemicalPurple":
        chemAnimate(1, true);
        break;
      case "chemicalCyan":
        chemAnimate(2, true);
        break;
      case "chemicalGreen":
        chemAnimate(3, true);
        break;
      case "chemicalYellow":
        chemAnimate(4, true);
        break;
      case "chemicalOrange":
        chemAnimate(5, true);
        break;
      case "chemicalRed":
        chemAnimate(6, true);
        break;
    }
  }

  /**
   * Function to correctly identify which chemical is unhovered.
   *
   * @param event the action event triggered by the chemical being unhovered.
   */
  @FXML
  private void onMouseExitChemical(MouseEvent event) {
    // Get source of click
    Rectangle src = (Rectangle) event.getSource();

    // Animate appropriate arrows
    switch (src.getId()) {
      case "chemicalBlue":
        chemAnimate(0, false);
        break;
      case "chemicalPurple":
        chemAnimate(1, false);
        break;
      case "chemicalCyan":
        chemAnimate(2, false);
        break;
      case "chemicalGreen":
        chemAnimate(3, false);
        break;
      case "chemicalYellow":
        chemAnimate(4, false);
        break;
      case "chemicalOrange":
        chemAnimate(5, false);
        break;
      case "chemicalRed":
        chemAnimate(6, false);
        break;
    }
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button.
   * @throws ApiProxyException if there is an error communicating with the API proxy.
   * @throws IOException if there is an I/O error.
   */
  @FXML
  private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {
    ChatTaskGenerator.onSendMessage(chatField);
  }

  /** Function to initalise the relevant tasks for scene. */
  private void initialiseTasks() {
    // Set chat area
    ChatTaskGenerator.chatAreas.add(chatArea);

    // Set send button
    ChatTaskGenerator.sendButtons.add(btnSend);

    // Set thinking animation
    ChatTaskGenerator.thinkingAnimationImages.add(imgScientistThinking);
    ChatTaskGenerator.thinkingAnimationImages.add(typingBubble);

    // Create tasks for animation and updating hint label
    createAnimateTask();
    updateHintTask(numHints);
  }

  /**
   * Function to update the label showing the user their remaining hints.
   *
   * @param numHints the number of hints remaining.
   */
  public void updateHintTask(int numHints) {
    updateHintTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            if (numHints >= 0) {
              // Update number of hints to relevant number of hints
              hintsRemaining.setText("Hints Remaining: " + String.valueOf(numHints));
            }
            return null;
          }
        };
  }

  /** Function to create a task for animation. */
  public void createAnimateTask() {
    // Create a new task for animation
    animateTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            enableChemicals(true);
            blurredImage.setVisible(true);
            fadeTransition();

            // End the task
            return null;
          }
        };
  }

  /** Function to initialise elements of the lab. */
  public void initialiseElements() {
    // Task to initialise javafx elements in lab
    Task<Void> initLabTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // Initialise white arrows
            int posy = 170;
            for (int i = 0; i < 14; i++) { // 0-6 are up arrows, 8-13 are down arrows
              ImageView arrow = new ImageView("file:src/main/resources/images/arrow_white.png");

              // Set properties of arrow
              int posx = 180 + (110 * i);
              if (i > 6) { // >6 are arrows along bottom row
                posx = posx + (105 * (i - 6));
                posy = 555;
                arrow.rotateProperty().setValue(180.0);
              }
              arrow.setOpacity(0);
              arrow.setFitHeight(26.0);
              arrow.setFitWidth(35.0);
              arrow.setLayoutX(posx);
              arrow.setLayoutY(posy);
              arrow.setPreserveRatio(true);
              arrow.setPickOnBounds(true);
              arrow.setCache(true);
              arrow.toFront();

              // Add to pane and collection
              paneLab.getChildren().add(arrow);
              arrowCollection.add(arrow);
            }

            // Initialise Green arrows
            posy = 195;
            for (int i = 0; i < 14; i++) { // 0-6 are up arrows, 7-13 are down arrows
              ImageView arrow = new ImageView("file:src/main/resources/images/arrow_green.png");

              // Set properties
              int posx = 180 + (110 * i);
              if (i > 6) {
                posx = 100 + (105 * (i - 6));
                posy = 530;
                arrow.rotateProperty().setValue(180.0);
              }
              arrow.setOpacity(0);
              arrow.setVisible(false);
              arrow.setFitHeight(26.0);
              arrow.setFitWidth(35.0);
              arrow.setLayoutX(posx);
              arrow.setLayoutY(posy);
              arrow.setFitHeight(26.0);
              arrow.setFitWidth(35.0);
              arrow.setPickOnBounds(true);
              arrow.setPreserveRatio(true);
              arrow.toFront();
              arrow.setCache(true);

              // Add to collection
              paneLab.getChildren().add(arrow);
              arrowCollection.add(arrow);
            }

            // Set relevant elements above arrows
            chemicalBlue.toFront();
            chemicalPurple.toFront();
            chemicalCyan.toFront();
            chemicalGreen.toFront();
            chemicalOrange.toFront();
            chemicalYellow.toFront();
            chemicalRed.toFront();
            chemicalGeneral.toFront();
            chemicalGeneral.setVisible(false);

            return null;
          }
        };
    Thread initLabThread = new Thread(initLabTask);
    initLabThread.setDaemon(true);
    initLabThread.start();
  }

  /** Increment number of solutions added and check if puzzle is complete. */
  private Boolean isPuzzleComplete() {
    numChemicalsAdded++;
    if (numChemicalsAdded == 3) {
      puzzleComplete();
      return true;
    } else {
      return false;
    }
  }

  /** Function to execute events for when the lab task is finished. */
  private void puzzleComplete() {
    GameState.isLabResolved = true;

    // Create task to run GPT model for lab complete message
    Task<ChatMessage> labCompleteTask =
        ChatTaskGenerator.createTask(GptPromptEngineering.getLabComplete());
    Thread labCompleteThread = new Thread(labCompleteTask);
    labCompleteThread.setDaemon(true);
    labCompleteThread.start();

    labCompleteTask.setOnSucceeded(
        e -> {
          ChatTaskGenerator.updateChat("\n\n-> ", labCompleteTask.getValue());
        });

    baseImage.setVisible(true);
    btnSwitchToTimeMachine.setDisable(false);
    startFlashingArrows();
    fadeTransitionOut();
  }

  /**
   * Function to get arrows to change white arrows to green arrows.
   *
   * @param color the color of the chemical whos arrows point to.
   */
  private void updateArrows(int color) {
    updateColourIndication(
        arrowCollection.get(color),
        arrowCollection.get(color + 7),
        arrowCollection.get(color + 14),
        arrowCollection.get(color + 21));
  }

  /**
   * Function to update arrows and check if puzzle is complete.
   *
   * @param color the color of the chemical whos arrows point to.
   */
  private void chemClicked(int color) {
    if (isChemicalSolution[color]) {
      updateArrows(color);
      isPuzzleComplete();
      isChemicalSolution[color] = false;
    }
  }

  /**
   * Function to set which arrows to animate and animation properties.
   *
   * @param color the color of the chemical whos arrows point to.
   * @param show whether to show or hide the arrows.
   */
  private void chemAnimate(int color, Boolean show) {
    // Animate appropriate arrows
    if (show) {
      moveArrowsIn(
          arrowCollection.get(color),
          arrowCollection.get(color + 7),
          arrowAnimationSpeed,
          arrowAnimationDistance);
    } else {
      // Animate arrow moving outwards
      moveArrowsOut(
          arrowCollection.get(color),
          arrowCollection.get(color + 7),
          arrowAnimationSpeed,
          -arrowAnimationDistance);
    }
  }

  /**
   * Function to fade in a given element.
   *
   * @param image the element to be faded in.
   * @param duration the duration of the animation.
   */
  private void fadingTransition(ImageView image, int duration) {
    // Initialise fade transition and its parameters
    FadeTransition fade = new FadeTransition(Duration.millis(duration), image);
    fade.setFromValue(0);
    fade.setToValue(1);

    // Play fade transition
    fade.play();
  }

  /**
   * Function to fade given element out.
   *
   * @param image the element to be faded out.
   * @param duration the duration of the animation.
   */
  private void fadingTransitionOut(ImageView image, int duration) {
    // Initialise fade transition and its parameters
    FadeTransition fade = new FadeTransition(Duration.millis(duration), image);
    fade.setDelay(Duration.millis(1200));
    fade.setFromValue(1);
    fade.setToValue(0);

    // Play fade transition
    fade.play();
  }

  /**
   * Function to flash given arrow from shown to hidden.
   *
   * @param image the arrow to be flashed.
   */
  private void flashingArrowsOff(ImageView image) {
    // Initialise transition and its parameters
    FadeTransition fade = new FadeTransition(Duration.millis(1), image);
    fade.setDelay(flashDuration);
    fade.setFromValue(1);
    fade.setToValue(0);

    // Recursive call to flash next arrow
    if (numFlashes >= 84) {
      return;
    } else {
      numFlashes++;
      fade.setOnFinished(
          event -> {
            flashDuration = Duration.millis(150);
            flashingArrowsOn(image);
          });
    }
    fade.play();
  }

  /**
   * Function to flash given arrow from hidden to shown.
   *
   * @param image the arrow to be flashed.
   */
  private void flashingArrowsOn(ImageView image) {
    FadeTransition fade = new FadeTransition(Duration.millis(1), image);
    fade.setDelay(flashDuration);
    fade.setFromValue(0);
    fade.setToValue(1);
    numFlashes++;
    fade.setOnFinished(
        event -> {
          flashingArrowsOff(image); // Recursive call to flash next arrow
        });
    fade.play();
  }

  /** Function to fade in elements for chemical task. */
  private void fadeTransition() {
    // Fade in all arrows and blurred image
    for (int i = 0; i < arrowCollection.size(); i++) {
      fadingTransition(arrowCollection.get(i), fadeTransitionSpeed);
    }
    fadingTransition(blurredImage, fadeTransitionSpeed);
  }

  /** Function to fade out chemical task elements. */
  private void fadeTransitionOut() {
    // Fade out all arrows and blurred image
    for (int i = 0; i < arrowCollection.size(); i++) {
      fadingTransitionOut(arrowCollection.get(i), fadeTransitionSpeed);
    }
    fadingTransitionOut(blurredImage, fadeTransitionSpeed);
  }

  /** Function to start flashing animation of arrows. */
  private void startFlashingArrows() {
    for (int i = 0; i < arrowCollection.size(); i++) {
      flashingArrowsOff(arrowCollection.get(i));
    }
  }

  /**
   * Function to move arrows towards the relevant chemical flask.
   *
   * @param arrowDown the down arrow to be moved.
   * @param arrowUp the up arrow to be moved.
   * @param duration the duration of the animation.
   * @param distance the distance to move the arrow.
   */
  private void moveArrowsIn(ImageView arrowDown, ImageView arrowUp, int duration, int distance) {
    // Initialise path lines and duration
    Line lineDown = new Line(18, 13, 18, 13 + distance);
    Line lineUp = new Line(18, 13, 18, 13 - distance);

    Duration duration2 = Duration.millis(duration);

    // Execute path transition
    new PathTransition(duration2, lineDown, arrowDown).play();
    new PathTransition(duration2, lineUp, arrowUp).play();
  }

  /**
   * Function to move arrows away from the relevant chemical flask.
   *
   * @param arrowDown the down arrow to be moved.
   * @param arrowUp the up arrow to be moved.
   * @param duration the duration of the animation.
   * @param distance the distance to move the arrow.
   */
  private void moveArrowsOut(ImageView arrowDown, ImageView arrowUp, int duration, int distance) {
    // Initialise path lines and duration
    Line lineDown = new Line(18, 13 - distance, 18, 13);
    Line lineUp = new Line(18, 13 + distance, 18, 13);

    Duration duration2 = Duration.millis(duration);

    // Execute path transition
    new PathTransition(duration2, lineDown, arrowDown).play();
    new PathTransition(duration2, lineUp, arrowUp).play();
  }

  /**
   * Function to switch from white arrows for green arrows.
   *
   * @param arrowUp the up arrow to be hidden.
   * @param arrowDown the down arrow to be hidden.
   * @param arrowUp1 the up arrow to be shown.
   * @param arrowDown1 the down arrow to be shown.
   */
  private void updateColourIndication(
      ImageView arrowUp, ImageView arrowDown, ImageView arrowUp1, ImageView arrowDown1) {
    arrowUp.setVisible(false);
    arrowDown.setVisible(false);
    arrowUp1.setVisible(true);
    arrowDown1.setVisible(true);
  }

  /**
   * Function to show chemical task and its relevant javafx elements.
   *
   * @param visibility whether to show or hide the chemicals.
   */
  public void enableChemicals(Boolean visibility) {
    // Set visiblity of elements for chemical task
    chemicalBlue.setVisible(visibility);
    chemicalCyan.setVisible(visibility);
    chemicalPurple.setVisible(visibility);
    chemicalRed.setVisible(visibility);
    chemicalYellow.setVisible(visibility);
    chemicalGreen.setVisible(visibility);
    chemicalOrange.setVisible(visibility);
    chemicalGeneral.setVisible(false);
  }
}
