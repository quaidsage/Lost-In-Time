package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class mainmenuController {
    @FXML private Button btnSwitchToTimeMachine;
    
    @FXML 
    private void switchToTimeMachine(ActionEvent event) {
        App.setUi(AppUi.TIMEMACHINE);
    }
}
