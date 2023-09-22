package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;

public class endsceneController {
  @FXML private Button btnPlayAgain;

  // Handle the "Play Again" button click event
  @FXML
  private void playAgain() throws IOException {
    // Set the root scene to the main menu when the "Play Again" button is clicked
    App.setRoot("mainmenu");

    // Clear all scenes except the main menu to start fresh
    SceneManager.clearAllScenesExceptMainMenu();
  }
}
