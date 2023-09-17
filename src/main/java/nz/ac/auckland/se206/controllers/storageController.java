package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class storageController {
    @FXML private Button btnSwitchToTimeMachine;
    @FXML private Label lblTimer;

    @FXML
    private void switchToTimeMachine(ActionEvent event) {
        App.setUi(AppUi.TIMEMACHINE);
    }
}