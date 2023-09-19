package nz.ac.auckland.se206;

import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.speech.TextToSpeech;

/** Represents the state of the game. */
public class GameState {

  /** Indicates whether the riddle has been resolved. */
  public static boolean isRiddleResolved = false;

  /** Indicates whether the key has been found. */
  public static boolean isKeyFound = false;

  /** Current text to speech for game. */
  public static TextToSpeech txtToSpeech;

  /** Current item to be found in the game. */
  public static String item = "battery";

  /** Current chat log for the game. */
  public static String chatLog;

  /** Main AI Core. */
  public static ChatCompletionRequest chatCompletionRequest;
}
