package nz.ac.auckland.se206.gpt;

import javafx.application.Platform;
import javafx.concurrent.Task;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.controllers.LabController;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;

public class ChatTaskGenerator {
  /**
   * Creates a task to run the LLM model on a given message to be run by background thread.
   *
   * @param message string to attach to message to be given to the LLM
   */
  public static Task<ChatMessage> createTask(String message) {
    Task<ChatMessage> task =
        new Task<ChatMessage>() {
          @Override
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
}
