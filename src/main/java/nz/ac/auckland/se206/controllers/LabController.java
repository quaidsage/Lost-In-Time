package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
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

/** A controller class for the lab scene. */
public class LabController {
  public static ArrayList<Integer> solutionColours;
  public static int numChemicalsAdded = 0;

  // Fields related to Task
  public static Task<ChatMessage> labIntroTask;
  public static Task<ChatMessage> labRiddleTask;
  public static Task<Void> animateTask;

  // Fields related to chemical solutions
  public static Boolean[] isChemicalSolution = {false, false, false, false, false, false, false};

  // Initialise Timer
  public static TimerController timer = new TimerController();

  // Initialsie Timer
  public static TaskController taskController;

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

  /**
   * Function to start the time in the timemachinescene.
   *
   * @param minutes the number of minutes to set the timer to.
   */
  public static void labStartTimer(int minutes) {
    timer.setMinutes(minutes);
    timer.start();
  }

  /** Converts the randomised solution colours to strings for use in the recipe. */
  public static String convertRecipe(ArrayList<Integer> solutionColours) {
    String[] colorStr = new String[3];

    // Convert the solution colours to strings to append to the riddle
    for (int i = 0; i < 3; i++) {
      switch (solutionColours.get(i)) {
        case 0:
          colorStr[i] = "Blue";
          break;
        case 1:
          colorStr[i] = "Purple";
          break;
        case 2:
          colorStr[i] = "Cyan";
          break;
        case 3:
          colorStr[i] = "Green";
          break;
        case 4:
          colorStr[i] = "Yellow";
          break;
        case 5:
          colorStr[i] = "Orange";
          break;
        default:
          colorStr[i] = "Red";
          break;
      }
    }
    return "1. " + colorStr[0] + "\n2. " + colorStr[1] + "\n3. " + colorStr[2];
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
  @FXML private ImageView imgPaper;
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
  @FXML private Rectangle rectLeftDoor;
  @FXML private Rectangle rectRightDoor;
  @FXML private ImageView baseImage;
  @FXML private ImageView blurredImage;
  @FXML private Pane dropdownMenu;
  @FXML private Pane menuOverlay;
  @FXML private Button btnCloseDropdownMenu;
  @FXML private Button btnOpenDropdownMenu;
  @FXML private Text txtTaskList;
  @FXML private Circle task1Circle;
  @FXML private Circle task2Circle;
  @FXML private Circle task3Circle;
  @FXML private Text txtTask1;
  @FXML private Text txtTask2;
  @FXML private Text txtTask3;
  @FXML private TextArea txtRecipe;

  private ArrayList<ImageView> arrowCollection = new ArrayList<ImageView>();
  private MenuController menuController;

  // Fields for initializing variables
  private int arrowAnimationSpeed = 85;
  private int arrowAnimationDistance = 25;
  private int fadeTransitionSpeed = 1500;
  private Duration flashDuration = Duration.millis(0);
  private int numFlashes = 0;

  /**
   * Initialise the scene with the specific settings.
   *
   * @throws ApiProxyException if there is a problem with the API proxy.
   */
  public void initialize() throws ApiProxyException {
    menuController = new MenuController(dropdownMenu);
    taskController = new TaskController();

    // Bind the circles for the task list to the gamestate variables.
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

    // Bind the text for the task list to the gamestate variables.
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
    App.audio.playClick();
    App.audio.playLabDoorClose();
    App.setUi(AppUi.TIMEMACHINE);
  }

  /**
   * Function to return to main menu, restarting game.
   *
   * @param event the action event triggered by the main menu button.
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

  /**
   * Function to begin lab riddle.
   *
   * @param event the action event triggered by the begin button.
   */
  @FXML
  private void onClickChemicals(MouseEvent event) {
    App.audio.playClick();
    if (GameState.isDifficultyMedium == true) {
      hintsRemaining.setText("Hints Remaining: " + String.valueOf(ChatTaskGenerator.numHints));
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
    labRiddleTask = ChatTaskGenerator.createTask(GptPromptEngineering.getRiddleLab());

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
    App.audio.playClick();
    ChatTaskGenerator.onSendMessage(chatField);
  }

  /** Function to initalise the relevant tasks for scene. */
  private void initialiseTasks() {
    // Set chat area
    ChatTaskGenerator.chatAreas.add(chatArea);

    // Set text field
    ChatTaskGenerator.chatFields.add(chatField);

    // Set send button
    ChatTaskGenerator.sendButtons.add(btnSend);

    // Set thinking animation
    ChatTaskGenerator.thinkingAnimationImages.add(imgScientistThinking);

    // Add hint text
    ChatTaskGenerator.hintsRemaining = hintsRemaining;

    // Add timer label, arrows, recipe txt, and general chemicals to restart manager
    RestartManager.labLabel = lblTimer;
    RestartManager.labArrowCollection = arrowCollection;
    RestartManager.labChemicals = chemicalGeneral;
    RestartManager.labTxtRecipe = txtRecipe;

    // Create tasks for animation and updating hint label
    createAnimateTask();

    // Add door animation to animation manager and bind their properties
    AnimationManager.rectLabLeftDoor = rectLeftDoor;
    AnimationManager.rectLabRightDoor = rectRightDoor;
    rectRightDoor.visibleProperty().bind(rectLeftDoor.visibleProperty());

    // Add printing animation
    AnimationManager.imgLabPaper = imgPaper;
    AnimationManager.txtLabRecipe = txtRecipe;
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
            AnimationManager.printRecipe();

            createAnimateTask();
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
            int posy = 150;
            for (int i = 0; i < 14; i++) { // 0-6 are up arrows, 8-13 are down arrows
              ImageView arrow = new ImageView("file:src/main/resources/images/arrow_white.png");

              // Set properties of arrow
              int posx = 193 + (98 * i);
              if (i > 6) { // >6 are arrows along bottom row
                posx = 193 + (98 * (i - 7));
                posy = 530;
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
            posy = 175;
            for (int i = 0; i < 14; i++) { // 0-6 are up arrows, 7-13 are down arrows
              ImageView arrow = new ImageView("file:src/main/resources/images/arrow_green.png");

              // Set properties
              int posx = 193 + (98 * i);
              if (i > 6) {
                posx = 193 + (98 * (i - 7));
                posy = 515;
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

    // Initialise recipe text
    txtRecipe.setText("Recipe:\n" + convertRecipe(solutionColours));
  }

  /** Increment number of solutions added and check if puzzle is complete. */
  private Boolean isPuzzleComplete() {
    numChemicalsAdded++;
    if (numChemicalsAdded == 3) {
      puzzleComplete();
      AnimationManager.removeRecipe();
      return true;
    } else {
      return false;
    }
  }

  /** Function to execute events for when the lab task is finished. */
  private void puzzleComplete() {
    App.audio.playSuccess();
    GameState.isLabResolved = true;
    TaskController.completeTask1();
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
      App.audio.playClick();
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
    // Set the general chemical rectangle to invisible
    chemicalGeneral.setVisible(false);
  }
}
