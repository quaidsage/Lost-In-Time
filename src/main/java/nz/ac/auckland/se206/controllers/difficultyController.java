package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class difficultyController {
  @FXML private Button btnSwitchToTimeMachine;
  int minutes = 4;

  @FXML
  private void switchToTimeMachine(ActionEvent event) {
    App.setUi(AppUi.TIMEMACHINE);

    // Start timer. Change 'minutes' variable to change the length of the game
    timemachineController.timemachineStartTimer(minutes);
    labController.labStartTimer(minutes);
    storageController.storageStartTimer(minutes);
  }
}
