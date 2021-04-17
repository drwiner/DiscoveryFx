package com.discoveryfx;

import javafx.scene.control.TreeCell;

public class IntentTreeCell extends TreeCell<IntentTreeValue> {

    public IntentTreeCell() {
        super();
    }

    @Override
    protected void updateItem(IntentTreeValue item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (item.getType().equals(IntentTreeValue.CELLTYPE.HEADER)){
                setGraphic(item.getHeader());
            } else {

                // for now only other type is TABLE
                setGraphic(item.getTable());
            }
            setText(null);
        }
    }
}
