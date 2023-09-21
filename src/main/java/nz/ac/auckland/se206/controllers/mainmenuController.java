package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class mainmenuController {
  @FXML private Button btnBeginGame;

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
  }
}
