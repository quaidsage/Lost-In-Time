package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class DifficultyController {
  // Define FXML elements
  @FXML private Button btnSwitchToTimeMachine;
  @FXML private CheckBox chkbxEasy, chkbxMedium, chkbxHard;
  @FXML private CheckBox chkbxTwoMins, chkbxFourMins, chkbxSixMins;
  @FXML private Label lblSelectBoxesWarning;

  // Enumerations for difficulty levels and time settings
  public enum Difficulty {
    EASY,
    MEDIUM,
    HARD
  }

  public enum TimeSetting {
    TWO,
    FOUR,
    SIX
  }

  // Default settings and variables
  int minutes = 4;
  boolean isDifficultyChecked, isTimeChecked = false;

  // Variables to track the current difficulty and time settings
  public static Difficulty currentDifficulty;
  TimeSetting currentTimeSetting;

  // Handle switching to the intro screen
  @FXML
  private void switchToIntro(ActionEvent event) {
    // Check if both difficulty and time settings are selected
    if (!isDifficultyChecked || !isTimeChecked) {
      lblSelectBoxesWarning.setText("Select a difficulty and time to begin");
    } else {
      // Set the selected difficulty in the game state
      if (currentDifficulty == Difficulty.EASY) {
        GameState.isDifficultyEasy = true;
      } else if (currentDifficulty == Difficulty.MEDIUM) {
        GameState.isDifficultyMedium = true;
      } else if (currentDifficulty == Difficulty.HARD) {
        GameState.isDifficultyHard = true;
      }

      // Set the selected time setting for the intro screen
      IntroController.minutes = minutes;
      Thread appendThread = new Thread(IntroController.appendTask);
      appendThread.start();
      App.setUi(AppUi.INTRO);
    }
  }

  // Handle the "Easy" difficulty checkbox
  @FXML
  private void checkedEasy(ActionEvent event) {
    currentDifficulty = Difficulty.EASY;
    isDifficultyChecked = true;
    LabController.numHints = 6;

    if (chkbxEasy.isSelected()) {
      deselectDifficultyBoxes(currentDifficulty, isDifficultyChecked);
    } else if (isDifficultyChecked == true) {
      isDifficultyChecked = false;
    }
  }

  // Handle the "Medium" difficulty checkbox
  @FXML
  private void checkedMedium(ActionEvent event) {
    currentDifficulty = Difficulty.MEDIUM;
    isDifficultyChecked = true;

    if (chkbxMedium.isSelected()) {
      deselectDifficultyBoxes(currentDifficulty, isDifficultyChecked);
    } else if (isDifficultyChecked == true) {
      isDifficultyChecked = false;
    }
  }

  // Handle the "Hard" difficulty checkbox
  @FXML
  private void checkedHard(ActionEvent event) {
    currentDifficulty = Difficulty.HARD;
    isDifficultyChecked = true;

    if (chkbxHard.isSelected()) {
      deselectDifficultyBoxes(currentDifficulty, isDifficultyChecked);
    } else if (isDifficultyChecked == true) {
      isDifficultyChecked = false;
    }
  }

  // Handle the "Two Minutes" time setting checkbox
  @FXML
  private void checkedTwoMins() {
    currentTimeSetting = TimeSetting.TWO;
    isTimeChecked = true;

    if (chkbxTwoMins.isSelected()) {
      deselectTimeBoxes(currentTimeSetting, isTimeChecked);
    } else if (isTimeChecked == true) {
      isTimeChecked = false;
    }

    minutes = 2;
  }

  // Handle the "Four Minutes" time setting checkbox
  @FXML
  private void checkedFourMins() {
    currentTimeSetting = TimeSetting.FOUR;
    isTimeChecked = true;

    if (chkbxFourMins.isSelected()) {
      deselectTimeBoxes(currentTimeSetting, isTimeChecked);
    } else if (isTimeChecked == true) {
      isTimeChecked = false;
    }

    minutes = 4;
  }

  // Handle the "Six Minutes" time setting checkbox
  @FXML
  private void checkedSixMins() {
    currentTimeSetting = TimeSetting.SIX;
    isTimeChecked = true;

    if (chkbxSixMins.isSelected()) {
      deselectTimeBoxes(currentTimeSetting, isTimeChecked);
    } else if (isTimeChecked == true) {
      isTimeChecked = false;
    }

    minutes = 6;
  }

  // Helper method to deselect other difficulty checkboxes
  private void deselectDifficultyBoxes(Difficulty difficulty, Boolean isDifficultyChecked) {
    switch (difficulty) {
      case EASY:
        chkbxMedium.setSelected(false);
        chkbxHard.setSelected(false);
        break;
      case MEDIUM:
        chkbxEasy.setSelected(false);
        chkbxHard.setSelected(false);
        break;
      case HARD:
        chkbxEasy.setSelected(false);
        chkbxMedium.setSelected(false);
        break;
    }
  }

  // Helper method to deselect other time setting checkboxes
  private void deselectTimeBoxes(TimeSetting timeSetting, Boolean isTimeChecked) {
    switch (timeSetting) {
      case TWO:
        chkbxFourMins.setSelected(false);
        chkbxSixMins.setSelected(false);
        break;
      case FOUR:
        chkbxTwoMins.setSelected(false);
        chkbxSixMins.setSelected(false);
        break;
      case SIX:
        chkbxTwoMins.setSelected(false);
        chkbxFourMins.setSelected(false);
        break;
    }
  }
}
