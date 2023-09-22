package nz.ac.auckland.se206;

import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.speech.TextToSpeech;

/** Represents the state of the game. */
public class GameState {

  public static boolean isDifficultyMedium = false;

  public static boolean isDifficultyEasy = false;

  public static boolean isDifficultyHard = false;

  /** Indicates whether the riddle has been resolved. */
  public static boolean isLabResolved = false;

  /** Indicates whether the key has been found. */
  public static boolean isKeyFound = false;

  /** Indicates whether the storage task has been completed. */
  public static boolean isStorageResolved = false;

  /** Indicates whether the user has visited the lab. */
  public static boolean isLabVisited = false;

  /** Indicates whether the user has visited the storage room. */
  public static boolean isStorageVisited = false;

  /** Current text to speech for game. */
  public static TextToSpeech txtToSpeech;

  /** Current chat log for the game. */
  public static String chatLog;

  /** Main AI Core. */
  public static ChatCompletionRequest chatCompletionRequest;

  /** Function to get the next step for the user */
  public static String getNextStep() {
    if (isLabResolved && isStorageResolved) {
      return "go to the time machine room and repair the time machine to stabilise it and return to"
          + " the present";
    } else if (isLabResolved && !isStorageResolved) {
      return "go to the storage room and restore power to the time machine";
    } else if (!isLabResolved && isStorageResolved) {
      return "go to the labratory and create the time fluid";
    } else {
      return "go to either the labratory to create the time fluid or the storage room to restore"
          + " power to the time machine";
    }
  }
}
