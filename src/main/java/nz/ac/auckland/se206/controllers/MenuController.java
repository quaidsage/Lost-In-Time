package nz.ac.auckland.se206.controllers;

import javafx.animation.PathTransition;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 * A controller class for managing the state and animations of a dropdown menu (Pane).
 */
public class MenuController {
    private boolean menuOpen = false;
    private Pane dropdownMenu;

    /**
     * Create a MenuController for the specified menu Pane.
     *
     * @param dropdownMenu The Pane representing the dropdown menu.
     */
    public MenuController(Pane dropdownMenu) {
        this.dropdownMenu = dropdownMenu;
    }


    /**
     * Checks if the menu is currently open.
     *
     * @return true if the menu is open, false otherwise.
     */
    public boolean isMenuOpen() {
        return menuOpen;
    }

    /**
     * Opens the menu with a sliding animation if it's not already open.
     */
    public void openMenu() {
        System.out.println("Open menu called");
        if (!menuOpen) {
            // Run the open animation
            openDropdownMenuAnimation(300);
            menuOpen = true;
        }
    }

    /**
     * Closes the menu with a sliding animation if it's open.
     */
    public void closeMenu() {
        if (menuOpen) {
            // Run the close animation
            closeDropdownMenuAnimation(300);
            menuOpen = false;
        }
    }

    /**
     * Performs the animation to open the dropdown menu.
     *
     * @param duration The duration of the animation in milliseconds.
     */
    private void openDropdownMenuAnimation(int duration) {
        System.out.println("Open dropdown animation called");
        Line lineAcross = new Line(-133, 375, 133, 375);
        Duration duration2 = Duration.millis(duration);
        new PathTransition(duration2, lineAcross, dropdownMenu).play();
    }

    /**
     * Performs the animation to close the dropdown menu.
     *
     * @param duration The duration of the animation in milliseconds.
     */
    private void closeDropdownMenuAnimation(int duration) {
        Line lineAcross = new Line(133, 375, -133, 375);
        Duration duration2 = Duration.millis(duration);
        new PathTransition(duration2, lineAcross, dropdownMenu).play();
    }
}
