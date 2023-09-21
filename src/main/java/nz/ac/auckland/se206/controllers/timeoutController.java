package nz.ac.auckland.se206.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;

public class timeoutController {
    
    @FXML private Button btnPlayAgain;

    @FXML
    private void playAgain() throws IOException {
        App.setRoot("mainmenu");
        SceneManager.clearAllScenesExceptMainMenu();
    }
}
