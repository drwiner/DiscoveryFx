package com.discoveryfx;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Client extends Application{

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int LEFT_SIDE_WIDTH = 200;
    private static final int RIGHT_SIDE_WIDTH = 200;

    private Parent createRoot() {
        BorderPane root = new BorderPane();
        GraphPane gridRoot = new GraphPane(WIDTH, HEIGHT);

        VBox vBoxLeft = new VBox();
        vBoxLeft.setPrefWidth(LEFT_SIDE_WIDTH);
        VBox vBoxRight = new VBox();
        vBoxRight.setPrefWidth(RIGHT_SIDE_WIDTH);
        root.setLeft(vBoxLeft);
        root.setRight(vBoxRight);

        ScrollPane scrollPane = new ScrollPane(gridRoot);
        scrollPane.setPannable(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        root.setCenter(scrollPane);

        DragableBox dragableBox = new DragableBox(scrollPane, gridRoot, 100, 100, 50, 50);
        gridRoot.addBox(dragableBox);

        DragableBox dragableBox1 = new DragableBox(scrollPane, gridRoot, 400, 100, 50, 50);
        dragableBox1.setFill(Color.AZURE);
        dragableBox1.setStrokeWidth(2);
        dragableBox1.setStroke(Color.BLACK);
        gridRoot.addBox(dragableBox1);
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
