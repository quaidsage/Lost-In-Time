package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class timemachineController {
    @FXML private Button btnSwitchToLab, btnSwitchToStorage;
    @FXML private Label lblTimer;
    
    @FXML
    private void switchToLab(ActionEvent event) {
        App.setUi(AppUi.LAB);
    }

    @FXML
    private void switchToStorage(ActionEvent event) {
        App.setUi(AppUi.STORAGE);
    }
}
