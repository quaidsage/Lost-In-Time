package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nz.ac.auckland.se206.SceneManager.AppUi;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  public static AppUi currentUi = AppUi.MAINMENU;

  private static Scene scene;

  public static void main(final String[] args) {
    launch();
  }

  public static void setRoot(String fxml) throws IOException {
    scene.setRoot(loadFxml(fxml));
  }

  /**
   * Returns the node associated to the input file. The method expects that the file is located in
   * "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The node of the input file.
   * @throws IOException If the file is not found.
   */
  private static Parent loadFxml(final String fxml) throws IOException {
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
    // Initialize scene instances;
    SceneManager.addUi(AppUi.LAB, loadFxml("lab"));
    SceneManager.addUi(AppUi.DIFFICULTY, loadFxml("difficulty"));
    SceneManager.addUi(AppUi.STORAGE, loadFxml("storage"));
    SceneManager.addUi(AppUi.MAINMENU, loadFxml("mainmenu"));
    SceneManager.addUi(AppUi.TIMEMACHINE, loadFxml("timemachine"));
    SceneManager.addUi(AppUi.MINIGAME, loadFxml("minigame"));

    // Load game starting with the main menu
    scene = new Scene(SceneManager.getUiRoot(AppUi.MAINMENU), 1400, 750);
    stage.setScene(scene);
    stage.show();
  }

  // Sets the Ui without resetting the state
  public static void setUi(AppUi newUi) {
    scene.setRoot(SceneManager.getUiRoot(newUi));
    currentUi = newUi;
  }

  // Returns the current Ui as a String
  public static String getUi() {
    return SceneManager.convertUiType(currentUi);
  }
}
