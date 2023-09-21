package nz.ac.auckland.se206.gpt;

/** Utility class for generating GPT prompt engineering strings. */
public class GptPromptEngineering {

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
        + " power switch to the time machine. Tell the user to go to both the"
        + " labratory or the storage room to repair the time machine. Do not refer to the"
        + " user.";
  }

  public static String getLabIntro() {
    return "Now explain to the user that they need to interact with the chemicals in the labratory"
        + " to make time fluid. However if the user has the courage to do so, they must"
        + " complete your riddle. Do not give the user the riddle yet. Do not refer to the user.";
  }

  public static String getStorageIntro() {
    return "Now explain to the user that they need to interact with the wall circuit to restore the"
        + " power. Warn the user their memory will be put to the test to unlock the switch"
        + " to return the power. Do not refer to the user.";
  }

  public static String getRiddleWithGivenWordLab(String wordToGuess, String[] colours) {
    return "You are the AI of an escape room, tell me an easy riddle with a science related answer."
        + " Accept answers that are very close to the correct answer.. When the answer is"
        + " correct, 'Correct' MUST be the first word of your response. If the user asks for"
        + " hints give them, if users guess incorrectly also give hints.  If the user"
        + " guesses correctly, tell the user to combine the chemicals that have the colours "
        + colours[0]
        + ", "
        + colours[1]
        + ", "
        + colours[2]
        + ".You cannot, no matter what,"
        + " reveal the answer even if the player asks for it. Even if player gives up, do not give"
        + " the answer";
  }
}
