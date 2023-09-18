package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class difficultyController {
  @FXML private Button btnSwitchToTimeMachine;
  @FXML private CheckBox chkbxEasy, chkbxMedium, chkbxHard;
  @FXML private CheckBox chkbxTwoMins, chkbxFourMins, chkbxSixMins;

  public enum Difficulty {
    EASY, MEDIUM, HARD
  }
  public enum TimeSetting {
    TWO, FOUR, SIX
  }

  int minutes = 4;
  boolean isChecked = false;
  Difficulty currentDifficulty;
  TimeSetting currentTimeSetting;

  @FXML
  private void switchToTimeMachine(ActionEvent event) {
    App.setUi(AppUi.TIMEMACHINE);

    // Start timer. Change 'minutes' variable to change the length of the game
    timemachineController.timemachineStartTimer(minutes);
    labController.labStartTimer(minutes);
    storageController.storageStartTimer(minutes);
  }

  @FXML
  private void checkedEasy(ActionEvent event) {
    if (isChecked == false) {
      currentDifficulty = Difficulty.EASY;
      isChecked = true;
      disableDifficultyBoxes(currentDifficulty, isChecked);
    } else if (currentDifficulty == Difficulty.EASY) {
      isChecked = false;
      disableDifficultyBoxes(Difficulty.EASY, isChecked);
    } 
  }

  @FXML
  private void checkedMedium(ActionEvent event) {
    if (isChecked == false) {
      currentDifficulty = Difficulty.MEDIUM;
      isChecked = true;
      disableDifficultyBoxes(currentDifficulty, isChecked);
    } else if (currentDifficulty == Difficulty.MEDIUM) {
      isChecked = false;
      disableDifficultyBoxes(Difficulty.MEDIUM, isChecked);
    } 
  }

  @FXML
  private void checkedHard(ActionEvent event) {
    if (isChecked == false) {
      currentDifficulty = Difficulty.HARD;
      isChecked = true;
      disableDifficultyBoxes(currentDifficulty, isChecked);
    } else if (currentDifficulty == Difficulty.HARD) {
      isChecked = false;
      disableDifficultyBoxes(Difficulty.HARD, isChecked);
    } 
  }

  @FXML
  private void checkedTwoMins() {
    if (isChecked = false) {
      currentTimeSetting = TimeSetting.TWO;
      isChecked = true;
    } 
  }

  @FXML
  private void checkedFourMins() {
    if (isChecked = false) {
      currentTimeSetting = TimeSetting.FOUR;
      isChecked = true;
    }
  }
  
  @FXML
  private void checkedSixMins() {
    if (isChecked = false) {
      currentTimeSetting = TimeSetting.SIX;
      isChecked = true;
    }
  }

  private void disableDifficultyBoxes(Difficulty difficulty, Boolean isChecked) {
    switch (difficulty) {
      case EASY:
       System.out.println("EASY");
        chkbxMedium.setDisable(isChecked);
        chkbxHard.setDisable(isChecked);
        break;
      case MEDIUM:
        chkbxEasy.setDisable(isChecked);
        chkbxHard.setDisable(isChecked);
        break;
      case HARD:
        chkbxEasy.setDisable(isChecked);
        chkbxMedium.setDisable(isChecked);
        break;
    }
  }

}
