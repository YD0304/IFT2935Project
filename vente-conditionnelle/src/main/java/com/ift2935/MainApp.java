package com.ift2935;

import com.ift2935.view.LoginView;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        // Delegate to the actual login view
        new LoginView().start(stage);
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}