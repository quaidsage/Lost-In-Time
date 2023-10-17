package nz.ac.auckland.se206.gpt;

import nz.ac.auckland.se206.GameState;

/** Utility class for generating GPT prompt engineering strings. */
public class GptPromptEngineering {

  public static String nextStep;
  public static String numHints;

  /**
   * Generates a GPT prompt engineering string for a riddle with the given word.
   *
   * @param wordToGuess the word to be guessed in the riddle
   * @return the generated prompt engineering string
   */
  public static String getContext() {
    return "You are the digital conciousness of a mad scientist helping the user. You have already"
        + " introduced yourself.\n"
        + " There are rooms the user must go to fix the time machine. \n In three short sentences,"
        + " Tell the user to go to both the labratory or the storage room to repair the time"
        + " machine then after that hack the timemachine. Do not refer to the user.";
  }

  /**
   * Method to get the message to run ChatGPT when the lab is entered initially.
   *
   * @return the message to be sent to ChatGPT.
   */
  public static String getLabIntro() {
    return "You are the digital conciousness of a mad scientist helping the user. You have already"
        + " introduced yourself. Tell the user that they need to look closer at the"
        + " chemicals on the table for the time fluid. Do not refer to the user.";
  }

  /**
   * Method to get the message to run ChatGPT when the storage is entered initially.
   *
   * @return the message to be sent to ChatGPT.
   */
  public static String getStorageIntro() {
    return "You are the digital conciousness of a mad scientist helping the user. You have already"
        + " introduced yourself. Now explain to the user that they need to interact with the"
        + " wall circuit to restore the power. Warn the user their memory will be put to the"
        + " test to unlock the switch to return the power. Do not refer to the user.";
  }

  /**
   * Function to generate the riddle for the labratory task.
   *
   * @param solutionColours the colours of the chemicals that the user must combine
   * @return the generated prompt engineering string
   */
  public static String getRiddleLab() {
    // Generate riddle prompt.
    return " Tell the user they must earn the time fluid recipe. Then give just a riddle with an"
        + " easy science answer. You must follow these instructions: ";
  }

  /** TODO JAVADOCS */
  public static String getRiddleInstructions() {
    return "When the user answers, Accept answers that are correct or very close and 'Correct' MUST"
               + " be the first word of your response.\n"
               + "Hints limited to "
        + numHints
        + " The only way you can give hint, user must ask for hint and 'Hint:' MUST be the first"
        + " word of response.\n"
        + "When the user guesses correctly, tell them to follow the recipe on the printed note. Do"
        + " not give a recipe.\n."
        + "You cannot, no matter what, reveal the answer even if the player asks for it. Even if"
        + " player gives up, do not give the answer.";
  }

  /**
   * Method to get the message to run ChatGPT when the lab task is completed.
   *
   * @return the message to be sent to ChatGPT.
   */
  public static String getLabComplete() {
    return "You are the digital conciousness of a mad scientist helping the user. You have already"
        + " introduced yourself. The user has just completed making the time fluid. Now they"
        + " must "
        + GameState.getNextStep()
        + ". Do not refer to the user.";
  }

  /**
   * Method to get the message to run ChatGPT when the storage task is completed.
   *
   * @return the message to be sent to ChatGPT.
   */
  public static String getStorageComplete() {
    return "You are the digital conciousness of a mad scientist helping the user. You have already"
        + " introduced yourself. The user has just restored power to the time machine. Now"
        + " they must "
        + GameState.getNextStep()
        + ". Do not refer to the user.";
  }

  /** TODO JAVADOCS */
  public static String getHackComplete() {
    return "You are the digital conciousness of a mad scientist helping the user. You have already"
        + " introduced yourself. Tell the user to interact with the time machine to return"
        + " back to their timeline.";
  }
}
