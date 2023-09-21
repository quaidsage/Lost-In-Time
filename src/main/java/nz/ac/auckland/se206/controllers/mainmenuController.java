package nz.ac.auckland.se206.controllers;

import java.io.IOException;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class mainmenuController {
  @FXML private Button btnBeginGame;

  int count = 0;

  public void initialize() {
    textToSpeech("Soft enj 2 0 6, escape room. Welcome!");
  }

  @FXML
  private void beginGame(ActionEvent event) throws IOException {
    SceneManager.addUi(AppUi.LAB, App.loadFxml("lab"));
    SceneManager.addUi(AppUi.DIFFICULTY, App.loadFxml("difficulty"));
    SceneManager.addUi(AppUi.STORAGE, App.loadFxml("storage"));
    SceneManager.addUi(AppUi.TIMEMACHINE, App.loadFxml("timemachine"));
    SceneManager.addUi(AppUi.ENDSCENE, App.loadFxml("endscene"));
    SceneManager.addUi(AppUi.TIMEOUT, App.loadFxml("timeout"));
    SceneManager.addUi(AppUi.INTRO, App.loadFxml("intro"));
    App.setUi(AppUi.DIFFICULTY);
    textToSpeech("Select difficulty level and time limit.");
  }

  private void textToSpeech(String msg) {
    // Declare textToSpeech from tts constructor
    Task<Void> ttsTask = new Task<Void>() {
  
        @Override
        protected Void call() throws Exception {
            // call desired methods when start()
            TextToSpeech textToSpeech = new TextToSpeech();
            textToSpeech.speak(msg);

            if (count == 1) {
              // Terminate the text-to-speech when done
              textToSpeech.terminate();
            }
            
            count++;
            return null;
        }
    };

    // Create a thread to run the task
    new Thread(ttsTask).start();
}
}
