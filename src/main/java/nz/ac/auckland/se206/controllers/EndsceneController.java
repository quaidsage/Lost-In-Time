package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;

/** A controller class for end scene. */
public class EndsceneController {
  @FXML private Button btnMenu;

  /** Handle the "Play Again" button click event. */
  @FXML
  private void onClickBackToMenu() throws IOException {
    // Set the root scene to the main menu when the "Play Again" button is clicked
    App.setRoot("mainmenu");
  }
}
