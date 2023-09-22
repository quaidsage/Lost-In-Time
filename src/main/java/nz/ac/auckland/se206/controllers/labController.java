package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
  @FXML private Button btnSwitchToTimeMachine, btnSend;
  @FXML private Label lblTimer;
  @FXML private TextArea chatArea;
  @FXML private TextArea chatField;
  @FXML private ImageView imgScientistThinking;
  @FXML private Button btnMenu;
  @FXML private Polyline chemicalGeneral;

  // Chemicals and arrow javafx elements
  @FXML private Rectangle chemicalCyan, chemicalBlue, chemicalPurple, chemicalOrange;
  @FXML private Rectangle chemicalYellow, chemicalGreen, chemicalRed, transitionScene;
  @FXML private ImageView baseImage, blurredImage;
  @FXML private ImageView arrowUpCyan, arrowUpBlue, arrowUpPurple, arrowUpOrange;
  @FXML private ImageView arrowUpYellow, arrowUpGreen, arrowUpRed;
  @FXML private ImageView arrowDownCyan, arrowDownBlue, arrowDownPurple, arrowDownOrange;
  @FXML private ImageView arrowDownYellow, arrowDownGreen, arrowDownRed;
  @FXML private ImageView arrowUpCyan1, arrowUpBlue1, arrowUpPurple1, arrowUpOrange1;
  @FXML private ImageView arrowUpYellow1, arrowUpGreen1, arrowUpRed1;
  @FXML private ImageView arrowDownCyan1, arrowDownBlue1, arrowDownPurple1, arrowDownOrange1;
  @FXML private ImageView arrowDownYellow1, arrowDownGreen1, arrowDownRed1;

  // Initialise Variables
  private int characterDelay = 5;
  public static Task<Void> updateChatTask;
  public static Task<ChatMessage> labIntroTask;
  public static Task<ChatMessage> labRiddleTask;
  private Boolean isCyanSolution = false,
      isBlueSolution = false,
      isPurpleSolution = false,
      isOrangeSolution = false;
  private Boolean isYellowSolution = false, isGreenSolution = false, isRedSolution = false;
  private Boolean isChemicalsEnabled = false;
  private String[] possibleChemicalColours = {
    "Red", "Green", "Blue", "Cyan", "Purple", "Yellow", "Orange"
  };
  private String[] puzzleColours;
  private int numChemicalsAdded = 0;
  private int arrowAnimationSpeed = 85;
  private int arrowAnimationDistance = 25;
  private int fadeTransitionSpeed = 1500;
  private Duration flashDuration = Duration.millis(0);
  private int numFlashes = 0;
  //  ArrayList<ImageView> imageViewList = new ArrayList<>();
  ArrayList<ImageView> arrowCollection = new ArrayList<ImageView>();

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
    List<String> colourList = Arrays.asList(possibleChemicalColours);
    Collections.shuffle(colourList);

    String[] Colours = colourList.subList(0, 3).toArray(new String[3]);
    puzzleColours = Colours;
    for (int i = 0; i < 3; i++) {
      updateColourAnswerVariables(puzzleColours[i]);
    }

    // Add the ImageView elements to the ArrayList
    arrowCollection.add(arrowUpCyan);
    arrowCollection.add(arrowUpBlue);
    arrowCollection.add(arrowUpPurple);
    arrowCollection.add(arrowUpOrange);
    arrowCollection.add(arrowUpYellow);
    arrowCollection.add(arrowUpGreen);
    arrowCollection.add(arrowUpRed);
    arrowCollection.add(arrowDownCyan);
    arrowCollection.add(arrowDownBlue);
    arrowCollection.add(arrowDownPurple);
    arrowCollection.add(arrowDownOrange);
    arrowCollection.add(arrowDownYellow);
    arrowCollection.add(arrowDownGreen);
    arrowCollection.add(arrowDownRed);
    arrowCollection.add(arrowUpCyan1);
    arrowCollection.add(arrowUpBlue1);
    arrowCollection.add(arrowUpPurple1);
    arrowCollection.add(arrowUpOrange1);
    arrowCollection.add(arrowUpYellow1);
    arrowCollection.add(arrowUpGreen1);
    arrowCollection.add(arrowUpRed1);
    arrowCollection.add(arrowDownCyan1);
    arrowCollection.add(arrowDownBlue1);
    arrowCollection.add(arrowDownPurple1);
    arrowCollection.add(arrowDownOrange1);
    arrowCollection.add(arrowDownYellow1);
    arrowCollection.add(arrowDownGreen1);
    arrowCollection.add(arrowDownRed1);
    chemicalGeneral.setVisible(false);

    // Create task to run GPT model for riddle message
    labRiddleTask = createTask(GptPromptEngineering.getRiddleLab(puzzleColours));

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

  @FXML
  private void clkChemicalCyan(MouseEvent event) {
    if (isCyanSolution == true) {
      updateColourIndication(arrowUpCyan, arrowDownCyan, arrowUpCyan1, arrowDownCyan1);
      isPuzzleComplete();
      isCyanSolution = false;
    }
  }

  @FXML
  private void clkChemicalBlue(MouseEvent event) {
    if (isBlueSolution == true) {
      updateColourIndication(arrowUpBlue, arrowDownBlue, arrowUpBlue1, arrowDownBlue1);
      isPuzzleComplete();
      isBlueSolution = false;
    }
  }

  @FXML
  private void clkChemicalOrange(MouseEvent event) {
    if (isOrangeSolution == true) {
      updateColourIndication(arrowUpOrange, arrowDownOrange, arrowUpOrange1, arrowDownOrange1);
      isPuzzleComplete();
      isOrangeSolution = false;
    }
  }

  @FXML
  private void clkChemicalPurple(MouseEvent event) {
    if (isPurpleSolution == true) {
      updateColourIndication(arrowUpPurple, arrowDownPurple, arrowUpPurple1, arrowDownPurple1);
      isPuzzleComplete();
      isPurpleSolution = false;
    }
  }

  @FXML
  private void clkChemicalYellow(MouseEvent event) {
    if (isYellowSolution == true) {
      updateColourIndication(arrowUpYellow, arrowDownYellow, arrowUpYellow1, arrowDownYellow1);
      isPuzzleComplete();
      isYellowSolution = false;
    }
  }

  @FXML
  private void clkChemicalGreen(MouseEvent event) {
    if (isGreenSolution == true) {
      updateColourIndication(arrowUpGreen, arrowDownGreen, arrowUpGreen1, arrowDownGreen1);
      isPuzzleComplete();
      isGreenSolution = false;
    }
  }

  @FXML
  private void clkChemicalRed(MouseEvent event) {
    if (isRedSolution == true) {
      updateColourIndication(arrowUpRed, arrowDownRed, arrowUpRed1, arrowDownRed1);
      isPuzzleComplete();
      isRedSolution = false;
    }
  }

  @FXML
  private void showChemicalCyan(MouseEvent event) {
    arrowAnimationIn(arrowDownCyan, arrowUpCyan, arrowAnimationSpeed, arrowAnimationDistance);
  }

  @FXML
  private void showChemicalBlue(MouseEvent event) {
    arrowAnimationIn(arrowDownBlue, arrowUpBlue, arrowAnimationSpeed, arrowAnimationDistance);
  }

  @FXML
  private void showChemicalOrange(MouseEvent event) {
    arrowAnimationIn(arrowDownOrange, arrowUpOrange, arrowAnimationSpeed, arrowAnimationDistance);
  }

  @FXML
  private void showChemicalPurple(MouseEvent event) {
    arrowAnimationIn(arrowDownPurple, arrowUpPurple, arrowAnimationSpeed, arrowAnimationDistance);
  }

  @FXML
  private void showChemicalYellow(MouseEvent event) {
    arrowAnimationIn(arrowDownYellow, arrowUpYellow, arrowAnimationSpeed, arrowAnimationDistance);
  }

  @FXML
  private void showChemicalGreen(MouseEvent event) {
    arrowAnimationIn(arrowDownGreen, arrowUpGreen, arrowAnimationSpeed, arrowAnimationDistance);
  }

  @FXML
  private void showChemicalRed(MouseEvent event) {
    arrowAnimationIn(arrowDownRed, arrowUpRed, arrowAnimationSpeed, arrowAnimationDistance);
  }

  @FXML
  private void hideChemicalCyan(MouseEvent event) {
    arrowAnimationOut(arrowDownCyan, arrowUpCyan, arrowAnimationSpeed, -arrowAnimationDistance);
  }

  @FXML
  private void hideChemicalBlue(MouseEvent event) {
    arrowAnimationOut(arrowDownBlue, arrowUpBlue, arrowAnimationSpeed, -arrowAnimationDistance);
  }

  @FXML
  private void hideChemicalOrange(MouseEvent event) {
    arrowAnimationOut(arrowDownOrange, arrowUpOrange, arrowAnimationSpeed, -arrowAnimationDistance);
  }

  @FXML
  private void hideChemicalPurple(MouseEvent event) {
    arrowAnimationOut(arrowDownPurple, arrowUpPurple, arrowAnimationSpeed, -arrowAnimationDistance);
  }

  @FXML
  private void hideChemicalYellow(MouseEvent event) {
    arrowAnimationOut(arrowDownYellow, arrowUpYellow, arrowAnimationSpeed, -arrowAnimationDistance);
  }

  @FXML
  private void hideChemicalGreen(MouseEvent event) {
    arrowAnimationOut(arrowDownGreen, arrowUpGreen, arrowAnimationSpeed, -arrowAnimationDistance);
  }

  @FXML
  private void hideChemicalRed(MouseEvent event) {
    arrowAnimationOut(arrowDownRed, arrowUpRed, arrowAnimationSpeed, -arrowAnimationDistance);
  }

  private void enableChemicals(Boolean disable) {
    chemicalBlue.setVisible(disable);
    chemicalCyan.setVisible(disable);
    chemicalPurple.setVisible(disable);
    chemicalRed.setVisible(disable);
    chemicalYellow.setVisible(disable);
    chemicalGreen.setVisible(disable);
    chemicalOrange.setVisible(disable);
    chemicalGeneral.setVisible(false);
    isChemicalsEnabled = true;
  }

  private void updateColourAnswerVariables(String colour) {
    if (colour.equals("Cyan")) {
      isCyanSolution = true;
    } else if (colour.equals("Blue")) {
      isBlueSolution = true;
    } else if (colour.equals("Purple")) {
      isPurpleSolution = true;
    } else if (colour.equals("Yellow")) {
      isYellowSolution = true;
    } else if (colour.equals("Red")) {
      isRedSolution = true;
    } else if (colour.equals("Green")) {
      isGreenSolution = true;
    } else if (colour.equals("Orange")) {
      isOrangeSolution = true;
    }
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
