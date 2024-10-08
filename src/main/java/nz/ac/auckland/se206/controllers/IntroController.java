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
import nz.ac.auckland.se206.RestartManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.ChatTaskGenerator;
import nz.ac.auckland.se206.speech.TextToSpeech;

/** A controller class for the intro scene. */
public class IntroController {
  public static Task<Void> appendTask;
  public static ChatMessage msg;
  public static int minutes;
  public static boolean isFilesLoaded;
  public static boolean isContextLoaded = false;
  public static int interaction = 0;

  // Define FXML elements
  @FXML private Button btnSkip;
  @FXML private Button btnPick;
  @FXML private Button btnNext;
  @FXML private TextArea txtIntro;
  @FXML private TextArea txtAi;
  @FXML private Rectangle rectBack;

  // Define variables for the introduction
  private int characterDelay = 5;

  // Array of introduction messages
  private String[] interactions = {
    "-> Greetings, intrepid traveler! You've stumbled into quite the temporal predicament.",
    "\n\n"
        + "-> My consciousness resides within this contraption. Fear not, for I have a penchant for"
        + " fixing the unfathomable.",
    "\n\n"
        + "-> Alas, our time machine thirsts for a replenishment of time fluid, its"
        + " essence for proper function depleted, and it yearns to be powered on once more.",
    "\n\n"
        + "-> Let us not dally; our future awaits, both in this era and the moments yet to come."
        + " Onward, my courageous compatriot!"
  };

  /** Initialise the introduction scene with specific settings. */
  public void initialize() {
    // Form the introduction message
    msg =
        new ChatMessage(
            "assistant",
            "You wake up in a strange room...\n Next to you, you see a strange device glowing.");
    updateTask(txtIntro);

    MainmenuController.btnSkip = btnSkip;

    setVisiblity(true);

    RestartManager.introElements =
        new Object[] {btnSkip, btnPick, txtIntro, btnNext, rectBack, txtAi};
  }

  /**
   * Handles switching to time machine scene.
   *
   * @param event The event that triggered the function.
   */
  @FXML
  private void onClickSkipIntro(ActionEvent event) {
    App.audio.playClick();

    // Switch to time machine scene
    App.setUi(AppUi.TIMEMACHINE);

    // If context is preloaded, load appending
    if (isContextLoaded) {
      TimemachineController.appendContextProperty.set(
          !TimemachineController.appendContextProperty.get());
    }

    // Start the round function in the time machine scene
    Thread startThread = new Thread(TimemachineController.startTask);
    startThread.setDaemon(true);
    startThread.start();
  }

  /** Function to handle starting interaction with the AI. */
  @FXML
  private void onClickPickDevice(ActionEvent event) {
    App.audio.playClick();

    btnNext.setText("Next");
    setVisiblity(false);
    interact();
  }

  /** Function to handle the next interaction with the AI. */
  @FXML
  private void onClickNext(ActionEvent event) {
    App.audio.playClick();
    interaction++;

    // Hide the "Next" button
    btnNext.setDisable(true);

    // Check if it's the last interaction, and switch to the time machine scene if so
    if (interaction == interactions.length) {
      onClickSkipIntro(null);
      return;
    } else if (interaction == interactions.length - 1) {
      btnNext.setText("Onward");
    }

    interact();
  }

  /** Function to handle interaction with the AI in the intro scene. */
  public void interact() {
    // Start appending the next interaction message
    msg = new ChatMessage("assistant", interactions[interaction]);
    updateTask(txtAi);

    // Run text to speech for new line
    ChatTaskGenerator.textToSpeech.clear();
    TextToSpeech.runTextToSpeech(msg.getContent());

    // Append the line to chat area
    Thread appendThread = new Thread(appendTask);
    appendThread.setDaemon(true);
    appendThread.start();
  }

  /**
   * Appends a chat message to the text area with character-by-character animation.
   *
   * @param msg The chat message to append.
   * @param chatArea The text area to append the message to.
   */
  public void appendMessage(ChatMessage msg, TextArea chatArea) {
    // Create timeline animation of message appending to text area
    Timeline timeline = createMessageTimeline(msg.getContent().toCharArray(), chatArea);
    timeline.play();
    timeline.setOnFinished(
        event -> {
          btnPick.setDisable(false);
          btnNext.setDisable(false);
        });
  }

  /**
   * Create a timeline which animates the message into the text area character by character.
   *
   * @param ch the character array to animate
   * @return the timeline
   */
  private Timeline createMessageTimeline(char[] ch, TextArea chatArea) {
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
    return timeline;
  }

  /**
   * Update the task which appends chat log to chat area.
   *
   * @param chatArea The text area to append the message to.
   */
  public void updateTask(TextArea chatArea) {
    appendTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // Append chat log to chat area
            appendMessage(msg, chatArea);
            return null;
          }
        };
  }

  /**
   * Sets visibility of required javafx elements.
   *
   * @param show Whether to show or hide specific elements.
   */
  public void setVisiblity(Boolean show) {
    // Update visibility of required javafx elements
    rectBack.setVisible(show);
    btnNext.setVisible(!show);
    btnPick.setVisible(show);
    txtIntro.setVisible(show);
    btnSkip.setVisible(show);

    // Update disable status of required javafx elements
    btnPick.setDisable(show);
    btnNext.setDisable(!show);
  }
}
