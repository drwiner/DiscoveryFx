package com.discoveryfx;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class GraphPane extends Pane {

    private double currentHeight;
    private double currentWidth;

    Group gridLines;
    Group boxes;

    /*
     * Lock on drawing lines
     */
    boolean drawing = false;

    private static final int GRID_SPACING = 10;


    public GraphPane(double width, double height) {
        setPrefWidth(width);
        setPrefHeight(height);

        gridLines = new Group();
        getChildren().add(gridLines);

        drawLines( 0, 0, width, height);

        boxes = new Group();
        getChildren().add(boxes);

        currentHeight = height;
        currentWidth = width;

        boundsInParentProperty().addListener((observableValue, bounds, t1) -> {
            if (t1.getMaxX() > currentWidth && !drawing){
                drawLines(0, 0, t1.getMaxX()-1, currentHeight);
                currentWidth = t1.getMaxX();
            }
            else if (t1.getMaxY() > currentHeight && !drawing) {
                drawLines(0, 0, currentWidth, t1.getMaxY() -1);
                currentHeight = t1.getMaxY();
            }
        });
    }

    public void addBox(DragableBox box){
        getChildren().add(box);
    }


    private void drawLines(int startX, int startY, double width, double height){
        drawing = true;
        gridLines.getChildren().clear();

        for (int i=startX; i<width; i+=GRID_SPACING) {
            // Vertical Lines
            Line vertLine = new Line(i, startY, i, height);
            vertLine.setStroke(Color.LIGHTGRAY);
            gridLines.getChildren().add(vertLine);
        }

        for (int i=startY; i<height; i+=GRID_SPACING){
            Line horiLine = new Line(startX, i, width, i);
            horiLine.setStroke(Color.LIGHTGRAY);
            gridLines.getChildren().add(horiLine);
        }

        drawing = false;
    }


}
