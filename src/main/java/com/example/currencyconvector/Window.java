package com.example.currencyconvector;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Window extends Application {
    private Controller controller;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Currency Convector");


        controller = fxmlLoader.getController();

        stage.setScene(scene);


        stage.setOnCloseRequest(event -> {
            if (controller != null) {
                controller.shutdown();
            }
        });

        stage.show();
    }

    @Override
    public void stop() throws Exception {

        if (controller != null) {
            controller.shutdown();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}