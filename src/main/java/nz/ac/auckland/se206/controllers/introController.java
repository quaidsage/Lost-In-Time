package nz.ac.auckland.se206.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;

public class introController {
  // Define FXML elements
  @FXML private Button btnSkip, btnPick, btnNext;
  @FXML private TextArea txtIntro, txtAi;
  @FXML private Rectangle rectBack;

  // Define variables for the introduction
  public static int minutes;
  private int characterDelay = 5;
  public static Task<Void> appendTask;
  public static ChatMessage msg;
  private int interaction = 0;
  private String[] interactions = {
    // Array of introduction messages
    "Greetings, intrepid traveler! You've stumbled into quite the temporal predicament.\n\n",
    // ... (more messages)
  };

  // Initialize the introduction scene
  public void initialize() {
    msg =
        new ChatMessage(
            "assistant",
            "You wake up in a strange room...\n Next to you, you see a strange device glowing.");
    updateTask(txtIntro);
    rectBack.setVisible(true);
    btnPick.setDisable(true);
    btnNext.setVisible(false);
    btnNext.setDisable(true);
  }

  // Handle switching to the time machine scene
  @FXML
  public void switchToTimeMachine(ActionEvent event) {
    // Switch to time machine scene
    App.setUi(AppUi.TIMEMACHINE);

    // Start the round function in the time machine scene
    Thread startThread = new Thread(timemachineController.startTask);
    startThread.start();
  }

  // Append a chat message to the text area with character-by-character animation
  public void appendMessage(ChatMessage msg, TextArea chatArea) {
    // Convert message to character array
    char[] ch = msg.getContent().toCharArray();

    // Use text-to-speech alongside chat appending
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

    // Play the timeline animation
    timeline.play();

    // Enable the "Pick" and "Next" buttons after the animation is finished
    timeline.setOnFinished(
        event -> {
          btnPick.setDisable(false);
          btnNext.setDisable(false);
        });
  }

  // Update the task to append a chat message
  public void updateTask(TextArea chatArea) {
    appendTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            appendMessage(msg, chatArea);
            return null;
          }
        };
  }

  // Start the interaction with the assistant
  @FXML
  public void startInteraction() {
    // Hide relevant elements
    rectBack.setVisible(false);
    btnPick.setVisible(false);
    txtIntro.setVisible(false);
    btnSkip.setVisible(false);

    // Show the "Next" button
    btnNext.setVisible(true);
    btnNext.setDisable(true);

    // Start appending the first interaction message
    msg = new ChatMessage("assistant", interactions[0]);
    updateTask(txtAi);
    Thread appendThread = new Thread(appendTask);
    appendThread.start();
  }

  // Proceed to the next interaction with the assistant
  @FXML
  public void proceedToNextInteraction() {
    // Hide the "Next" button
    btnNext.setDisable(true);

    // Increment the interaction index
    interaction++;

    // Check if it's the last interaction, and switch to the time machine scene if so
    if (interaction == interactions.length - 1) {
      switchToTimeMachine(null);
    } else if (interaction == interactions.length - 2) {
      btnNext.setText("Onward");
    }

    // Start appending the next interaction message
    msg = new ChatMessage("assistant", interactions[interaction]);
    updateTask(txtAi);
    Thread appendThread = new Thread(appendTask);
    appendThread.start();
  }
}
