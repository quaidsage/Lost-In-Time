package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.App;

public class TimeoutController {

  @FXML private Button btnPlayAgain;

  /**
   * Handles the "Play Again" button click event. This method sets the root scene to the main menu
   * and clears all scenes except the main menu.
   *
   * @throws IOException if there is an I/O error during scene transition
   */
  @FXML
  private void playAgain() throws IOException {
    App.setRoot("mainmenu");
    SceneManager.clearAllScenesExceptMainMenu();
  }
}