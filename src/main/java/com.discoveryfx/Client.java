package com.discoveryfx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Client extends Application{

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    private Parent createRoot() {
        BorderPane root = new BorderPane();
        root.setPrefWidth(WIDTH);
        root.setPrefHeight(HEIGHT);

        VBox vBox = new VBox();

        TabPane header = new TabPane();
        Tab clusterReportTab = new Tab("Cluster Report");
        clusterReportTab.setClosable(false);
        Tab intentTab = new Tab("Intents");
        intentTab.setClosable(false);

        header.getTabs().addAll(clusterReportTab, intentTab);

        Pane contentPane = new Pane();
        Pane internalPane = new Pane();
        internalPane.setTranslateX(5);
        internalPane.setTranslateY(5);
        internalPane.prefWidthProperty().bind(contentPane.widthProperty().subtract(10));
        internalPane.prefHeightProperty().bind(contentPane.heightProperty().subtract(10));
        contentPane.getChildren().add(internalPane);


        HBox.setHgrow(contentPane, Priority.ALWAYS);
        VBox.setVgrow(contentPane, Priority.ALWAYS);

        vBox.getChildren().addAll(header, contentPane);
        root.setCenter(vBox);


        return root;
    }


    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(createRoot()));
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
