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
  @FXML private Button btnSkip, btnPick, btnNext;
  @FXML private TextArea txtIntro, txtAi;
  @FXML private Rectangle rectBack;

  public static int minutes;
  private int characterDelay = 5;
  public static Task<Void> appendTask;
  public static ChatMessage msg;
  private int interaction = 0;
  private String[] interactions = {
    "Greetings, intrepid traveler! You've stumbled into quite the temporal predicament.\n\n",
    "My consciousness resides within this contraption, a victim of the very device that brought you"
        + " here. Fear not, for I am a mad scientist with a penchant for fixing the"
        + " unfathomable.\n\n",
    "Alas, our time machine thirsts for a replenishment of time fluid, its essence for proper"
        + " function depleted, and it yearns to be powered on once more.\n\n",
    "Seek the time fluid reservoir within the machine's core, and find the temporal power switch"
        + " nearby. As you administer the rejuvenating elixir and breathe life into the circuits,"
        + " we edge closer to restoring the fabric of time.\n\n",
    "We must hurry though, for the time machine has become unstable and will soon collapse",
    "Let us not dally; our future awaits, both in this era and the moments yet to come. Onward, my"
        + " courageous compatriot!"
  };

  public void initialize() {
    msg =
        new ChatMessage("assistant", "You wake up in a strange room...\n Next to you, you see a strange device glowing.");
    updateTask(txtIntro);
    rectBack.setVisible(true);
    btnPick.setDisable(true);
    btnNext.setVisible(false);
    btnNext.setDisable(true);
  }

  @FXML
  public void switchToTimeMachine(ActionEvent event) {
    // Switch to time machine
    App.setUi(AppUi.TIMEMACHINE);

    // Start round function
    Thread startThread = new Thread(timemachineController.startTask);
    startThread.start();
  }

  public void appendMessage(ChatMessage msg, TextArea chatArea) {

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
          btnPick.setDisable(false);
          btnNext.setDisable(false);
        });
  }

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

  @FXML
  public void startInteraction() {
    // Hide relevant elements
    rectBack.setVisible(false);
    btnPick.setVisible(false);
    txtIntro.setVisible(false);
    btnSkip.setVisible(false);

    // Show next button
    btnNext.setVisible(true);
    btnNext.setDisable(true);

    // Start appending message
    msg = new ChatMessage("assistant", interactions[0]);
    updateTask(txtAi);
    Thread appendThread = new Thread(appendTask);
    appendThread.start();
  }

  @FXML
  public void nextInteraction() {
    // Hide button
    btnNext.setDisable(true);

    // Increment interaction
    interaction++;

    // Check if last interaction
    if (interaction == interactions.length - 1) {
      switchToTimeMachine(null);
    } else if (interaction == interactions.length - 2) {
      btnNext.setText("Onward");
    }

    // Start appending message
    msg = new ChatMessage("assistant", interactions[interaction]);
    updateTask(txtAi);
    Thread appendThread = new Thread(appendTask);
    appendThread.start();
  }
}
