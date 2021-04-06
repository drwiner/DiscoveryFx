package com.discoveryfx;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.Optional;

public class DragableBox extends Rectangle {

    private Optional<Point2D> previousDragPosition = Optional.empty();

    private ScrollPane scrollPane;
    private Pane gridRoot;

    public DragableBox(ScrollPane scrollPane, Pane gridRoot, double x, double y, double width, double height) {
        super(0, 0, width, height);
        this.scrollPane = scrollPane;
        this.gridRoot = gridRoot;
        setTranslateX(x);
        setTranslateY(y);

        addEventHandler(MouseEvent.DRAG_DETECTED, mouseEvent -> {

            /*
             * Scroll pane's bounds are where it is relative to entire scene, since this is a first child.
             */
            Bounds scrollBoundary = scrollPane.getBoundsInParent();

            if (mouseEvent.getSceneX() < scrollBoundary.getMinX()
                    || mouseEvent.getSceneY() < scrollBoundary.getMinY())
                return;

            previousDragPosition = Optional.of(new Point2D(mouseEvent.getScreenX(), mouseEvent.getScreenY()));

        });




        addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            Bounds scrollBounds = scrollPane.getBoundsInParent();
            Bounds viewportBounds = scrollPane.getViewportBounds();

            Point2D dragPosition = new Point2D(mouseEvent.getScreenX(), mouseEvent.getScreenY());

            if (getTranslateX() < 0) {
                setTranslateX(1);
                return;
            }

            if (getTranslateY() < 0) {
                setTranslateY(1);
                return;
            }

            if (checkAndDoOffGridMin(scrollBounds, dragPosition, mouseEvent))
                return;


            if (checkAndDoOffScreenMax(scrollBounds, viewportBounds, dragPosition, mouseEvent))
                return;

            if (previousDragPosition.isEmpty())
                return;

            Point2D delta = previousDragPosition.get().subtract(dragPosition);

            if (Math.abs(delta.getX()) < 4 && Math.abs(delta.getY()) < 4)
                return;

            previousDragPosition = Optional.of(dragPosition);

            this.setTranslateX(getTranslateX() - delta.getX());
            this.setTranslateY(getTranslateY() - delta.getY());
        });

        addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
            previousDragPosition = Optional.empty();
        });

//        scrollPane.hvalueProperty().addListener((observableValue, number, t1) -> {
//            System.out.println("From Hvalue (" + number + ") to (" + t1 + ")");
//        });

    }

    private boolean checkAndDoOffGridMin(Bounds scrollBounds, Point2D dragPosition, MouseEvent mouseEvent){
        if (mouseEvent.getSceneX() < scrollBounds.getMinX()) {

            double dragDelta = dragPosition.getX() - previousDragPosition.get().getX();
            double percentDrag = dragDelta / (gridRoot.getMinWidth());

            if (scrollPane.getHvalue() > 0 && dragDelta < 0) {

                scrollPane.setHvalue(Math.max(0, scrollPane.getHvalue()  + percentDrag + (.5 * percentDrag)));
                double amountIntoGridRoot = (scrollPane.getHvalue() * gridRoot.getMinWidth()) - scrollPane.getWidth();
                setTranslateX(Math.max(getTranslateX() + dragDelta, amountIntoGridRoot));
//                setTranslateX(getTranslateX() + dragDelta);
                previousDragPosition = Optional.of(dragPosition);
                System.out.println("Scroll Left");
                return true;

            }else  {
                if (previousDragPosition.isPresent()){
                    if (previousDragPosition.get().getX() < mouseEvent.getScreenX()) {
                        previousDragPosition = Optional.of(new Point2D(mouseEvent.getScreenX(), mouseEvent.getScreenY()));
                    }
                }
            }
        }

        if (mouseEvent.getSceneY() < scrollBounds.getMinY()) {
            double dragDelta = dragPosition.getY() - previousDragPosition.get().getY();
            double percentDrag = dragDelta / (gridRoot.getMinHeight());

            if (scrollPane.getVvalue() > 0 && dragDelta < 0){
                scrollPane.setVvalue(Math.max(0, scrollPane.getVvalue()  + percentDrag + (.5 * percentDrag)));
                double amountIntoGridRoot = (scrollPane.getVvalue() * gridRoot.getMinHeight()) - scrollPane.getHeight();
                setTranslateY(Math.max(getTranslateY() + dragDelta, amountIntoGridRoot));
                previousDragPosition = Optional.of(dragPosition);
                System.out.println("Scroll Up");
                return true;
            }else  {
                if (previousDragPosition.isPresent()){
                    if (previousDragPosition.get().getY() < mouseEvent.getScreenY()) {
                        previousDragPosition = Optional.of(new Point2D(mouseEvent.getScreenX(), mouseEvent.getScreenY()));
                    }
                }
            }
        }

        return false;
    }


    private boolean checkAndDoOffScreenMax(Bounds scrollBounds, Bounds viewportBounds, Point2D dragPosition, MouseEvent mouseEvent) {
        /*
         * Mechanism for scrolling down
         */
        if (mouseEvent.getSceneY() > viewportBounds.getHeight() + scrollBounds.getMinY()) {

            if (gridRoot.getMinHeight() < getTranslateY() + getHeight()) {
                gridRoot.setMinHeight(getTranslateY() + getHeight());
                scrollPane.setVvalue(1);
                return true;
            }

            double percentIntoGrid = (getTranslateY()) / gridRoot.getMinHeight();
            double heightPercent = getHeight() / (gridRoot.getMinWidth());

            double dragDelta = dragPosition.getY() - previousDragPosition.get().getY();
            double percentDrag = dragDelta / (gridRoot.getMinHeight());

            if (scrollPane.getVvalue() < percentIntoGrid - heightPercent && dragDelta>= 0) {
                double originalVvalue = scrollPane.getVvalue();
                double increment = originalVvalue + (percentDrag) + (0.5  * percentDrag);
                scrollPane.setVvalue(Math.min(1, increment));
                System.out.println("Scrolling Down: " + dragDelta);
                setTranslateY(getTranslateY() + dragDelta);
                previousDragPosition = Optional.of(dragPosition);
                return true;
            } else  {
                if (previousDragPosition.isPresent()){
                    if (previousDragPosition.get().getY() > mouseEvent.getScreenY()) {
                        previousDragPosition = Optional.of(new Point2D(mouseEvent.getScreenX(), mouseEvent.getScreenY()));
                    }
                }
            }

        }


        /*
         * If the mouse position is past the scroll pane bounds
         */
        if (mouseEvent.getSceneX() > viewportBounds.getWidth() + scrollBounds.getMinX()  ){

            if (gridRoot.getMinWidth() < getTranslateX() + getWidth()) {
                gridRoot.setMinWidth(getTranslateX() + getWidth());
                scrollPane.setHvalue(1);
                return true;
            }

            double percentIntoGrid = (getTranslateX()) / gridRoot.getMinWidth();
            double widthPercent = getWidth() / (gridRoot.getMinWidth());

            double dragDelta = dragPosition.getX() - previousDragPosition.get().getX();
            double percentDrag = dragDelta / (gridRoot.getMinWidth());

            if (scrollPane.getHvalue() < percentIntoGrid - widthPercent && dragDelta>= 0) {
                double originalHvalue = scrollPane.getHvalue();
                double increment = originalHvalue + (percentDrag) + (0.5  * percentDrag);
                scrollPane.setHvalue(Math.min(1, increment));
                System.out.println("Scrolling Right: " + dragDelta);
                setTranslateX(getTranslateX() + dragDelta);
                previousDragPosition = Optional.of(dragPosition);
                return true;

            } else  {
                if (previousDragPosition.isPresent()){
                    if (previousDragPosition.get().getX() > mouseEvent.getScreenX()) {
                        previousDragPosition = Optional.of(new Point2D(mouseEvent.getScreenX(), mouseEvent.getScreenY()));
                    }
                }
            }
        }

        return false;

    }

}
