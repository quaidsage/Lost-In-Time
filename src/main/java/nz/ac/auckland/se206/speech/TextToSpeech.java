package nz.ac.auckland.se206.speech;

import javafx.concurrent.Task;
import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import nz.ac.auckland.se206.controllers.MainmenuController;
import nz.ac.auckland.se206.gpt.ChatTaskGenerator;

/** Text-to-speech API using the JavaX speech library. */
public class TextToSpeech {
  /** Custom unchecked exception for Text-to-speech issues. */
  static class TextToSpeechException extends RuntimeException {
    public TextToSpeechException(final String message) {
      super(message);
    }
  }

  /**
   * Main function to speak the given list of sentences.
   *
   * @param args A sequence of strings to speak.
   */
  public static void main(final String[] args) {
    if (args.length == 0) {
      throw new IllegalArgumentException(
          "You are not providing any arguments. You need to provide one or more sentences.");
    }

    final TextToSpeech textToSpeech = new TextToSpeech();

    textToSpeech.speak(args);
    textToSpeech.terminate();
  }

  private final Synthesizer synthesizer;

  /**
   * Constructs the TextToSpeech object creating and allocating the speech synthesizer. English
   * voice: com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory
   */
  public TextToSpeech() {
    try {
      System.setProperty(
          "freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
      Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");

      synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(java.util.Locale.ENGLISH));

      synthesizer.allocate();
    } catch (final EngineException e) {
      throw new TextToSpeechException(e.getMessage());
    }
  }

  /**
   * Speaks the given list of sentences.
   *
   * @param sentences A sequence of strings to speak.
   */
  public void speak(final String... sentences) {
    boolean isFirst = true;

    for (final String sentence : sentences) {
      if (isFirst) {
        isFirst = false;
      } else {
        // Add a pause between sentences.
        sleep();
      }

      speak(sentence);
    }
  }

  /**
   * Speaks the given sentence in input.
   *
   * @param sentence A string to speak.
   */
  public void speak(final String sentence) {
    if (sentence == null) {
      throw new IllegalArgumentException("Text cannot be null.");
    }

    try {
      synthesizer.resume();
      synthesizer.speakPlainText(sentence, null);
      synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
    } catch (final AudioException | InterruptedException e) {
      throw new TextToSpeechException(e.getMessage());
    }
  }

  /** Sleeps a while to add some pause between sentences. */
  private void sleep() {
    try {
      Thread.sleep(100);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * It deallocates the speech synthesizer. If you are experiencing an IllegalThreadStateException,
   * avoid using this method and run the speak method without terminating.
   */
  public void terminate() {
    try {
      synthesizer.deallocate();
    } catch (final EngineException e) {
      throw new TextToSpeechException(e.getMessage());
    }
  }

  /**
   * Function to handle text to speech in seperate thread.
   *
   * @param msg The message to be spoken
   */
  public static void runTextToSpeech(String msg) {
    // If TTS is muted, return
    if (MainmenuController.isTTSMuted) {
      return;
    }

    // Create a task for text-to-speech
    Task<Void> ttsTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // Create an instance of TextToSpeech and speak the message
            ChatTaskGenerator.textToSpeech.speak(msg);
            return null;
          }
        };

    // Create a thread to run the text-to-speech task
    Thread ttsThread = new Thread(ttsTask);
    ttsThread.setDaemon(true);
    ttsThread.start();
  }

  /**
   * Function to pause text to speech.
   *
   * @param setPause Whether to pause or resume the text to speech
   * @throws EngineStateError If there is an error with the speech engine
   * @throws AudioException If there is an error with the audio
   */
  public void pause(boolean setPause) throws AudioException, EngineStateError {
    if (setPause) {
      synthesizer.pause();
    } else {
      synthesizer.resume();
    }
  }

  /** Function to clear current queue of text. */
  public void clear() throws EngineStateError {
    synthesizer.cancelAll();
  }
}
