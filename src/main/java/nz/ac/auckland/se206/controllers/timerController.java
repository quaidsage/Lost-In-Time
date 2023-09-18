package nz.ac.auckland.se206.controllers;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class timerController extends Service<Void> {
    private int minutes = 2;
    private int seconds = 0;

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (minutes >= 0 && seconds >= 0) {
                    updateMessage(String.format("%02d:%02d", minutes, seconds));
                    Thread.sleep(1000); // Sleep for 1 second
                    if (seconds == 0) {
                        minutes--;
                        seconds = 59;
                    } else {
                        seconds--;
                    }
                }
                return null;
            }
        };
    }
}