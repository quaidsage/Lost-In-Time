package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;

public class difficultyController {
  @FXML private Button btnSwitchToTimeMachine;
  @FXML private CheckBox chkbxEasy, chkbxMedium, chkbxHard;
  @FXML private CheckBox chkbxTwoMins, chkbxFourMins, chkbxSixMins;
  @FXML private Label lblSelectBoxesWarning;

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

  int minutes = 4;
  boolean isDifficultyChecked, isTimeChecked = false;

  public static Difficulty currentDifficulty;
  TimeSetting currentTimeSetting;

  @FXML
  private void switchToIntro(ActionEvent event) {
    if (!isDifficultyChecked || !isTimeChecked) {
      lblSelectBoxesWarning.setText("Select a difficulty and time to begin");
    } else {
      if (currentDifficulty == Difficulty.EASY) {
        GameState.isDifficultyEasy = true;
      } else if (currentDifficulty == Difficulty.MEDIUM) {
        GameState.isDifficultyMedium = true;
      } else if (currentDifficulty == Difficulty.HARD) {
        GameState.isDifficultyHard = true;
      }
      introController.minutes = minutes;
      Thread appendThread = new Thread(introController.appendTask);
      appendThread.start();
      App.setUi(AppUi.INTRO);
    }
  }

  @FXML
  private void checkedEasy(ActionEvent event) {
    currentDifficulty = Difficulty.EASY;
    isDifficultyChecked = true;
    GptPromptEngineering.hints = "You can give the user as many hints as they ask for";

    if (chkbxEasy.isSelected()) {
      deselectDifficultyBoxes(currentDifficulty, isDifficultyChecked);
    } else if (isDifficultyChecked == true) {
      isDifficultyChecked = false;
    }
  }

  @FXML
  private void checkedMedium(ActionEvent event) {
    currentDifficulty = Difficulty.MEDIUM;
    isDifficultyChecked = true;
    GptPromptEngineering.hints = "a total of 5 hints, given one at a time";

    if (chkbxMedium.isSelected()) {
      deselectDifficultyBoxes(currentDifficulty, isDifficultyChecked);
    } else if (isDifficultyChecked == true) {
      isDifficultyChecked = false;
    }
  }

  @FXML
  private void checkedHard(ActionEvent event) {
    currentDifficulty = Difficulty.HARD;
    isDifficultyChecked = true;
    GptPromptEngineering.hints = "no hints under any circumstances";

    if (chkbxHard.isSelected()) {
      deselectDifficultyBoxes(currentDifficulty, isDifficultyChecked);
    } else if (isDifficultyChecked == true) {
      isDifficultyChecked = false;
    }
  }

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
