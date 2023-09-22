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
  @FXML private CheckBox chkbxEasy;
  @FXML private CheckBox chkbxMedium;
  @FXML private CheckBox chkbxHard;
  @FXML private CheckBox chkbxTwoMins;
  @FXML private CheckBox chkbxFourMins;
  @FXML private CheckBox chkbxSixMins;
  @FXML private Label lblSelectBoxesWarning;

  // Enumerations for difficulty levels
  public enum Difficulty {
    EASY,
    MEDIUM,
    HARD
  }

  // Enumerations for time settings
  public enum TimeSetting {
    TWO,
    FOUR,
    SIX
  }

  // Default settings and variables
  int minutes = 4;
  boolean isDifficultyChecked, isTimeChecked = false;

  // Variables to track the current difficulty and time settings
  private Difficulty currentDifficulty;
  private TimeSetting currentTimeSetting;

  /**
   * Function to handle switching to intro scene.
   *
   * @param event The event that triggered the function.
   */
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

  /**
   * Handles the EASY checkbox.
   *
   * @param event The event that triggered the function.
   */
  @FXML
  private void checkedEasy(ActionEvent event) {
    // Change game state
    currentDifficulty = Difficulty.EASY;
    isDifficultyChecked = true;
    LabController.numHints = 6;

    // Handle when switching difficulties
    if (chkbxEasy.isSelected()) {
      deselectDifficultyBoxes(currentDifficulty, isDifficultyChecked);
    } else if (isDifficultyChecked == true) {
      isDifficultyChecked = false;
    }
  }

  /**
   * Handles the MEDIUM checkbox.
   *
   * @param event The event that triggered the function.
   */
  @FXML
  private void checkedMedium(ActionEvent event) {
    // Change game state
    currentDifficulty = Difficulty.MEDIUM;
    isDifficultyChecked = true;

    // Handle when switching difficulties
    if (chkbxMedium.isSelected()) {
      deselectDifficultyBoxes(currentDifficulty, isDifficultyChecked);
    } else if (isDifficultyChecked == true) {
      isDifficultyChecked = false;
    }
  }

  /**
   * Handles the HARD checkbox.
   *
   * @param event The event that triggered the function.
   */
  @FXML
  private void checkedHard(ActionEvent event) {
    // Change game state
    currentDifficulty = Difficulty.HARD;
    isDifficultyChecked = true;

    // Handle when switching difficulties
    if (chkbxHard.isSelected()) {
      deselectDifficultyBoxes(currentDifficulty, isDifficultyChecked);
    } else if (isDifficultyChecked == true) {
      isDifficultyChecked = false;
    }
  }

  /** Handles the 2 Minutes checkbox. */
  @FXML
  private void checkedTwoMins() {
    // Change game state
    currentTimeSetting = TimeSetting.TWO;
    isTimeChecked = true;

    // Handle when switching time settings
    if (chkbxTwoMins.isSelected()) {
      deselectTimeBoxes(currentTimeSetting, isTimeChecked);
    } else if (isTimeChecked == true) {
      isTimeChecked = false;
    }

    minutes = 2;
  }

  /** Handles the 4 Minutes checkbox. */
  @FXML
  private void checkedFourMins() {
    // Change game state
    currentTimeSetting = TimeSetting.FOUR;
    isTimeChecked = true;

    // Handle when switching time settings
    if (chkbxFourMins.isSelected()) {
      deselectTimeBoxes(currentTimeSetting, isTimeChecked);
    } else if (isTimeChecked == true) {
      isTimeChecked = false;
    }

    minutes = 4;
  }

  /** Handles the 6 Minutes checkbox. */
  @FXML
  private void checkedSixMins() {
    // Change game state
    currentTimeSetting = TimeSetting.SIX;
    isTimeChecked = true;

    // Handle when switching time settings
    if (chkbxSixMins.isSelected()) {
      deselectTimeBoxes(currentTimeSetting, isTimeChecked);
    } else if (isTimeChecked == true) {
      isTimeChecked = false;
    }

    minutes = 6;
  }

  /**
   * Helper method to deselect other difficulty checkboxes.
   *
   * @param difficulty The difficulty to deselect.
   * @param isDifficultyChecked Whether the difficulty is checked.
   */
  private void deselectDifficultyBoxes(Difficulty difficulty, Boolean isDifficultyChecked) {
    // Deselect the unwanted difficulty checkboxes
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

  /**
   * Helper method to deselect other time setting checkboxes.
   *
   * @param timeSetting The time setting to deselect.
   * @param isTimeChecked Whether the time setting is checked.
   */
  private void deselectTimeBoxes(TimeSetting timeSetting, Boolean isTimeChecked) {
    // Deselect the unwanted time setting checkboxes
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
