package nz.ac.auckland.se206.gpt;

import nz.ac.auckland.se206.GameState;

/** Utility class for generating GPT prompt engineering strings. */
public class GptPromptEngineering {

  public static String hints;
  public static String nextStep;

  /**
   * Generates a GPT prompt engineering string for a riddle with the given word.
   *
   * @param wordToGuess the word to be guessed in the riddle
   * @return the generated prompt engineering string
   */
  public static String getContext() {
    return "You are the digital conciousness of a mad scientist helping the user. You have already"
        + " introduced yourself. There are three rooms, a room with the time machine, a"
        + " labratory with the chemicals to make time fluid, and a storage room with the"
        + " power switch to the time machine. In three sentences, Tell the user to go to both the"
        + " labratory or the storage room to repair the time machine. Do not refer to the"
        + " user.";
  }

  public static String getLabIntro() {
    return "You are the digital conciousness of a mad scientist helping the user. You have already"
        + " introduced yourself. Tell the user that they need to look closer at the"
        + " chemicals on the table for the time fluid. Do not refer to the user.";
  }

  public static String getStorageIntro() {
    return "You are the digital conciousness of a mad scientist helping the user. You have already"
        + " introduced yourself. Now explain to the user that they need to interact with the"
        + " wall circuit to restore the power. Warn the user their memory will be put to the"
        + " test to unlock the switch to return the power. Do not refer to the user.";
  }

  public static String getRiddleLab(String[] colours) {
    return "Explain that the user needs the recipe for the time fluid."
        + " To get the recipe they must answer your riddle to prove their intelligence. You"
        + " will now give the user a riddle with an easy science related answer. Do not give"
        + " these instructions to the user: Accept answers that are correct or very close. When the answer is correct, 'Correct' MUST be the first word of"
        + " your response. You must give the user "
        + hints
        + " when asked. When you give a hint 'Hint:' MUST be the first word of your response, and you must include the number of hints remaining."
        + " If the user has no hints available, do not give hints. When the user"
        + " guesses correctly, tell the user to combine the chemicals that have the colours "
        + colours[0]
        + ", "
        + colours[1]
        + ", "
        + colours[2]
        + ". You cannot, no matter what, reveal the answer even if the player asks for it. You"
        + " cannot reveal the colours the user must combine. Even if player gives up, do not give"
        + " the answer";
  }

  public static String getLabComplete() {
    return "You are the digital conciousness of a mad scientist helping the user. You have already"
        + " introduced yourself. The user has just completed making the time fluid. Now they"
        + " must "
        + GameState.getNextStep()
        + ". Do not refer to the user.";
  }

  public static String getStorageComplete() {
    return "You are the digital conciousness of a mad scientist helping the user. You have already"
        + " introduced yourself. The user has just restored power to the time machine. Now"
        + " they must "
        + GameState.getNextStep()
        + ". Do not refer to the user.";
  }
}
