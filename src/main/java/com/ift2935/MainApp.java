package com.ift2935;

import java.sql.Connection;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        System.out.println("App started");
    
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("✅ Connection successful!");
        } catch (Exception e) {
            System.out.println("❌ Connection failed:");
            e.printStackTrace();
        }
    
        stage.setTitle("DB Test");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
