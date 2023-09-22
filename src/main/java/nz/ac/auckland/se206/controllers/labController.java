package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;

public class labController {

  // JavaFX elements
  @FXML private Pane paneLab;
  @FXML private Button btnSwitchToTimeMachine, btnSend;
  @FXML private Label lblTimer;
  @FXML private TextArea chatArea;
  @FXML private TextArea chatField;
  @FXML private ImageView imgScientistThinking;
  @FXML private Button btnMenu;
  @FXML private HBox box;
  @FXML private Polyline chemicalGeneral;
  @FXML private Rectangle chemicalCyan, chemicalBlue, chemicalPurple, chemicalOrange;
  @FXML private Rectangle chemicalYellow, chemicalGreen, chemicalRed, transitionScene;
  @FXML private ImageView baseImage, blurredImage;
  ArrayList<ImageView> arrowCollection = new ArrayList<ImageView>();

  // Initialise Variables
  private int characterDelay = 5;
  public static Task<Void> updateChatTask;
  public static Task<ChatMessage> labIntroTask;
  public static Task<ChatMessage> labRiddleTask;
  private Boolean[] isChemicalSolution = {false, false, false, false, false, false, false};
  private Boolean isChemicalsEnabled = false;

  // Animation variables
  private int numChemicalsAdded = 0;
  private int arrowAnimationSpeed = 85;
  private int arrowAnimationDistance = 25;
  private int fadeTransitionSpeed = 1500;
  private Duration flashDuration = Duration.millis(0);
  private int numFlashes = 0;

  // Initialise Timer
  private static timerController timer = new timerController();

  public void initialize() throws ApiProxyException {
    timer = new timerController();
    // Bind the lblTimer to the timerController properties.
    lblTimer.textProperty().bind(timer.messageProperty());
    timer.setOnSucceeded(
        e -> {
          App.setUi(AppUi.TIMEOUT);
          timer.reset();
        });

    // Initialise task to update chat area
    createUpdateTask();

    // Create task to run GPT model for intro message
    labIntroTask = createTask(GptPromptEngineering.getLabIntro());

    // Change to thinking image
    imgScientistThinking.setVisible(true);

    labIntroTask.setOnSucceeded(
        e -> {
          // Update imagery of scientist
          imgScientistThinking.setVisible(false);

          ChatMessage response = labIntroTask.getValue();
          // Append to chat log
          GameState.chatLog += "\n\n-> " + response.getContent();

          // Append response to current scene
          chatArea.appendText("\n\n-> ");
          appendChatMessage(response);

          // Update chat area in other scenes
          Thread updateChatThreadStorage = new Thread(storageController.updateChatTask);
          updateChatThreadStorage.start();
          Thread updateChatThreadTM = new Thread(timemachineController.updateChatTask);
          updateChatThreadTM.start();
        });

    // Set solution chemicals
    ArrayList<Integer> list = new ArrayList<Integer>();
    for (int i = 0; i < 7; i++) {
      list.add(i);
    }
    Collections.shuffle(list);
    ArrayList<Integer> solutionColours = new ArrayList<Integer>();
    for (int i = 0; i < 3; i++) {
      solutionColours.add(list.get(i));
      isChemicalSolution[solutionColours.get(i)] = true;
    }

    Task<Void> initLabTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // Initialise arrows
            // Order: Blue, Purple, Cyan, Green, Yellow, Orange, Red

            // White arrows
            int posx = 180;
            int posy = 170;
            for (int i = 0; i < 14; i++) { // 0-6 are up arrows, 8-13 are down arrows
              // Create white arrow
              ImageView arrow = new ImageView("file:src/main/resources/images/arrow_white.png");

              // Set position and rotation
              posx = 180 + (110 * i);
              if (i > 6) {
                posx = 100 + (105 * (i - 6));
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

              // Add to collection
              paneLab.getChildren().add(arrow);
              arrowCollection.add(arrow);
            }

            // Green arrows
            posy = 195;
            for (int i = 0; i < 14; i++) { // 0-6 are up arrows, 7-13 are down arrows
              // Create green arrow
              ImageView arrow = new ImageView("file:src/main/resources/images/arrow_green.png");

              // Set position and rotation
              posx = 180 + (110 * i);
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
    initLabThread.start();

    // Create task to run GPT model for riddle message
    labRiddleTask = createTask(GptPromptEngineering.getRiddleLab(solutionColours));

    labRiddleTask.setOnSucceeded(
        e -> {
          // Update imagery
          imgScientistThinking.setVisible(false);

          ChatMessage response = labRiddleTask.getValue();

          // Append to chat log
          GameState.chatLog += "\n\n-> " + response.getContent();

          // Append response to current scene
          chatArea.appendText("\n\n-> ");
          appendChatMessage(response);

          // Update chat area in other scenes
          Thread updateChatThreadTM2 = new Thread(timemachineController.updateChatTask);
          updateChatThreadTM2.start();
          Thread updateChatThreadStorage2 = new Thread(storageController.updateChatTask);
          updateChatThreadStorage2.start();
        });
  }

  /**
   * Change scene to Time Machine
   *
   * @param event the action event triggered by the time machine button
   */
  @FXML
  private void switchToTimeMachine(ActionEvent event) {
    App.setUi(AppUi.TIMEMACHINE);
  }

  /**
   * Function to start timer
   *
   * @param minutes the number of minutes to set the timer to
   */
  public static void labStartTimer(int minutes) {
    timer.setMinutes(minutes);
    timer.start();
  }

  @FXML
  private void clkChemicalGeneral(MouseEvent event) {

    if (GameState.isLabResolved) {
      return;
    }
    // Hide general chemicals
    chemicalGeneral.setVisible(false);

    // Prevent user from leaving lab until riddle is solved
    btnSwitchToTimeMachine.setDisable(true);

    // Change to thinking scientist
    imgScientistThinking.setVisible(true);

    Thread labRiddleThread = new Thread(labRiddleTask);
    labRiddleThread.start();
  }

  @FXML
  private void returnToMenu(ActionEvent event) throws IOException {
    App.setRoot("mainmenu");
    GameState.isLabResolved = false;
    GameState.isStorageResolved = false;
    SceneManager.clearAllScenesExceptMainMenu();
  }

  private void updateArrows(int color) {
    updateColourIndication(
        arrowCollection.get(color),
        arrowCollection.get(color + 7),
        arrowCollection.get(color + 14),
        arrowCollection.get(color + 21));
  }

  private void chemClicked(int color) {
    if (isChemicalSolution[color]) {
      updateArrows(color);
      isPuzzleComplete();
      isChemicalSolution[color] = false;
    }
  }

  @FXML
  private void clkChemical(MouseEvent event) {
    Rectangle src = (Rectangle) event.getSource();
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

  private void chemAnimate(int color, Boolean show) {
    if (show) {
      arrowAnimationIn(
          arrowCollection.get(color),
          arrowCollection.get(color + 7),
          arrowAnimationSpeed,
          arrowAnimationDistance);
    } else {
      arrowAnimationOut(
          arrowCollection.get(color),
          arrowCollection.get(color + 7),
          arrowAnimationSpeed,
          -arrowAnimationDistance);
    }
  }

  @FXML
  private void showChemical(MouseEvent event) {
    Rectangle src = (Rectangle) event.getSource();
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

  @FXML
  private void hideChemical(MouseEvent event) {
    Rectangle src = (Rectangle) event.getSource();
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

  private void enableChemicals(Boolean visibility) {
    chemicalBlue.setVisible(visibility);
    chemicalCyan.setVisible(visibility);
    chemicalPurple.setVisible(visibility);
    chemicalRed.setVisible(visibility);
    chemicalYellow.setVisible(visibility);
    chemicalGreen.setVisible(visibility);
    chemicalOrange.setVisible(visibility);
    chemicalGeneral.setVisible(false);
    isChemicalsEnabled = true;
  }

  private Boolean isPuzzleComplete() {
    numChemicalsAdded++;
    if (numChemicalsAdded == 3) {
      puzzleComplete();
      return true;
    } else {
      return false;
    }
  }

  private void puzzleComplete() {
    GameState.isLabResolved = true;

    Task<ChatMessage> labCompleteTask = createTask(GptPromptEngineering.getLabComplete());
    Thread labCompleteThread = new Thread(labCompleteTask);
    labCompleteThread.start();

    labCompleteTask.setOnSucceeded(
        e -> {
          // Append to chat log
          GameState.chatLog += "\n\n-> " + labCompleteTask.getValue().getContent();

          // Append response to current scene
          chatArea.appendText("\n\n-> ");
          appendChatMessage(labCompleteTask.getValue());

          // Update chat area in other scenes
          Thread updateChatThreadTM2 = new Thread(timemachineController.updateChatTask);
          updateChatThreadTM2.start();
          Thread updateChatThreadStorage2 = new Thread(storageController.updateChatTask);
          updateChatThreadStorage2.start();
        });

    baseImage.setVisible(true);
    btnSwitchToTimeMachine.setDisable(false);
    startFlashingArrows();
    fadeTransitionOut();
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
            Platform.runLater(() -> {});
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
          if (!isChemicalsEnabled) {
            chemicalGeneral.setVisible(true);
          }
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
      if (result.getChatMessage().getContent().startsWith("Correct")) {
        // Show chemicals
        enableChemicals(true);

        blurredImage.setVisible(true);
        fadeTransition();
      }
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
    Thread updateChatThreadTM = new Thread(timemachineController.updateChatTask);
    updateChatThreadTM.start();
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
          Thread updateChatThreadTM2 = new Thread(timemachineController.updateChatTask);
          updateChatThreadTM2.start();
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

  private void fadingTransition(ImageView image, int duration) {
    FadeTransition fade = new FadeTransition(Duration.millis(duration), image);
    fade.setFromValue(0);
    fade.setToValue(1);
    fade.play();
  }

  private void fadingTransitionOut(ImageView image, int duration) {
    FadeTransition fade = new FadeTransition(Duration.millis(duration), image);
    fade.setDelay(Duration.millis(1200));
    fade.setFromValue(1);
    fade.setToValue(0);
    fade.play();
  }

  private void flashingArrowsOff(ImageView image) {
    FadeTransition fade = new FadeTransition(Duration.millis(1), image);
    fade.setDelay(flashDuration);
    fade.setFromValue(1);
    fade.setToValue(0);
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

  private void fadeTransition() {
    for (int i = 0; i < arrowCollection.size(); i++) {
      fadingTransition(arrowCollection.get(i), fadeTransitionSpeed);
    }
    fadingTransition(blurredImage, fadeTransitionSpeed);
  }

  private void fadeTransitionOut() {
    for (int i = 0; i < arrowCollection.size(); i++) {
      fadingTransitionOut(arrowCollection.get(i), fadeTransitionSpeed);
    }
    fadingTransitionOut(blurredImage, fadeTransitionSpeed);
  }

  private void startFlashingArrows() {
    for (int i = 0; i < arrowCollection.size(); i++) {
      flashingArrowsOff(arrowCollection.get(i));
    }
  }

  private void arrowAnimationIn(
      ImageView arrowDown, ImageView arrowUp, int duration, int distance) {
    Line lineDown = new Line(18, 13, 18, 13 + distance);
    Line lineUp = new Line(18, 13, 18, 13 - distance);

    Duration duration2 = Duration.millis(duration);

    PathTransition pathTransitionDown = new PathTransition(duration2, lineDown, arrowDown);
    PathTransition pathTransitionUp = new PathTransition(duration2, lineUp, arrowUp);

    pathTransitionDown.play();
    pathTransitionUp.play();
  }

  private void arrowAnimationOut(
      ImageView arrowDown, ImageView arrowUp, int duration, int distance) {
    Line lineDown = new Line(18, 13 - distance, 18, 13);
    Line lineUp = new Line(18, 13 + distance, 18, 13);

    Duration duration2 = Duration.millis(duration);

    PathTransition pathTransitionDown = new PathTransition(duration2, lineDown, arrowDown);
    PathTransition pathTransitionUp = new PathTransition(duration2, lineUp, arrowUp);

    pathTransitionDown.play();
    pathTransitionUp.play();
  }

  private void updateColourIndication(
      ImageView arrowUp, ImageView arrowDown, ImageView arrowUp1, ImageView arrowDown1) {
    arrowUp.setVisible(false);
    arrowDown.setVisible(false);
    arrowUp1.setVisible(true);
    arrowDown1.setVisible(true);
  }
}
