package nz.ac.auckland.se206.gpt;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import javafx.util.Duration;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.controllers.LabController;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;

/** A class to generate a chat task. This class is used by many scenes within the game. */
public class ChatTaskGenerator {
  static int characterDelay = 5;
  static TextArea chatArea;
  public static TextArea labChatArea;
  public static TextArea storageChatArea;
  public static TextArea timemachineChatArea;

  /**
   * Creates a task to run the LLM model on a given message to be run by background thread.
   *
   * @param message string to attach to message to be given to the LLM
   */
  public static Task<ChatMessage> createTask(String message) {
    // Initialise a new task for a message
    Task<ChatMessage> task =
        new Task<ChatMessage>() {
          @Override
          // Run gpt with the message
          protected ChatMessage call() throws Exception {
            ChatMessage msg = runGpt(new ChatMessage("assistant", message));
            return msg;
          }
        };
    return task;
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private static ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    // Append to main game chat completion request
    GameState.chatCompletionRequest.addMessage(msg);

    try {
      // Get response from GPT model
      ChatCompletionResult chatCompletionResult = GameState.chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();

      // Create chat message from response
      GameState.chatCompletionRequest.addMessage(result.getChatMessage());

      // Check if users answer was correct
      if (result.getChatMessage().getContent().startsWith("Correct")) {
        new Thread(LabController.animateTask).start();
      }

      // Decrement hint on medium
      if (result.getChatMessage().getContent().contains("Hint:")
          && GameState.isDifficultyMedium == true
          && LabController.numHints > 0) {
        Platform.runLater(
            () -> {
              LabController.numHints--;
              new Thread(LabController.updateHintTask).start();
            });
      }

      // Prevent hints on hard and with 0 hints remaining
      if (result.getChatMessage().getContent().contains("Hint:")
          && (GameState.isDifficultyHard || LabController.numHints <= 0)) {
        return new ChatMessage("assistant", "No more hints remaining...");
      }

      return result.getChatMessage();
    } catch (ApiProxyException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String getUserMessage(TextArea chatField) {
    // Get the text from the chat area and clear it
    String message = chatField.getText();
    chatField.clear();

    // Check if message is empty
    if (message.trim().isEmpty()) {
      return null;
    }
    return message;
  }

  /**
   * Creates a task to generate a timeline to append text character by character to given chat area.
   *
   * @param msg the chat message to append
   * @param chatArea the chat area to append the message to
   * @return the timeline task
   */
  public static Task<Timeline> createMessageTimeline(ChatMessage msg, TextArea chatArea) {
    // Convert from ChatMessage to char array
    char[] ch = msg.getContent().toCharArray();

    Task<Timeline> timelineTask =
        new Task<Timeline>() {
          @Override
          protected Timeline call() throws Exception {
            // Create a timeline and keyframes to append each character of the message to the chat
            // text area
            Timeline timeline = new Timeline();
            if (ch.length < 100) {
              characterDelay = (50 - (ch.length / 2)) + 5;
            } else {
              characterDelay = 5;
            }

            // Set duration between each character
            Duration delayBetweenCharacters = Duration.millis(characterDelay);
            Duration frame = delayBetweenCharacters;

            // Add keyframes for each character
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
        };

    return timelineTask;
  }

  /** Function to create task to update chat area for scene. */
  public static Task<Void> createUpdateTask(String scene) {
    if (scene == "lab") {
      chatArea = labChatArea;
    } else if (scene == "storage") {
      chatArea = storageChatArea;
    } else if (scene == "timemachine") {
      chatArea = timemachineChatArea;
    } else {
      System.out.println("BAD");
    }
    // Create task to append chat log to chat area
    Task<Void> updateChatTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // Append chat log to chat area
            chatArea.setText(GameState.chatLog);
            chatArea.appendText("");

            return null;
          }
        };
    return updateChatTask;
  }
}
