package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.controllers.AudioController;
import nz.ac.auckland.se206.controllers.MainmenuController;
import nz.ac.auckland.se206.gpt.ChatTaskGenerator;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  public static AppUi currentUi = AppUi.MAINMENU;

  private static Scene scene;

  public static AudioController audio;

  public static void main(final String[] args) {
    launch();
  }

  public static void setRoot(String fxml) throws IOException {
    // If restarting to main menu, ensure fxml files are not loaded twice
    scene.setRoot(loadFxml(fxml));

    // Clear TTS
    ChatTaskGenerator.textToSpeech.clear();
  }

  /**
   * Returns the node associated to the input file. The method expects that the file is located in
   * "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The node of the input file.
   * @throws IOException If the file is not found.
   */
  public static Parent loadFxml(final String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {
    // Load game starting with the main menu
    SceneManager.addUi(AppUi.MAINMENU, App.loadFxml("mainmenu"));
    scene = new Scene(SceneManager.getUiRoot(AppUi.MAINMENU), 1400, 750);
    stage.setScene(scene);
    stage.setResizable(false);
    stage.getIcons().add(new Image("file:src/main/resources/images/scientist-process.png"));
    stage.setOnCloseRequest(
        e -> {
          try {
            ChatTaskGenerator.textToSpeech.clear();
            ChatTaskGenerator.textToSpeech.terminate();
            audio.stop();
          } catch (Exception er) {
            stage.close();
            System.exit(1);
          }
        });
    stage.show();
  }

  // Sets the Ui without resetting the state
  public static void setUi(AppUi newUi) {
    if (newUi == AppUi.MAINMENU) {
      MainmenuController.hasRestarted = true;
      ChatTaskGenerator.textToSpeech.clear();
      RestartManager.restartGame();
      MainmenuController.newContextResponse();
    }

    scene.setRoot(SceneManager.getUiRoot(newUi));

    // Remove tts queue when switching non-game scenes
    if (currentUi == AppUi.MAINMENU || currentUi == AppUi.DIFFICULTY || currentUi == AppUi.INTRO) {
      ChatTaskGenerator.textToSpeech.clear();
    }

    currentUi = newUi;
  }

  // Returns the current Ui as a String
  public static String getUi() {
    return SceneManager.convertUiType(currentUi);
  }
}
