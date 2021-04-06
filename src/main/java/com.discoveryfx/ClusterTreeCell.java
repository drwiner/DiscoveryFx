package com.discoveryfx;

import javafx.scene.control.TreeCell;

public class ClusterTreeCell extends TreeCell<ClusterTreeValue> {

    public ClusterTreeCell() {
        super();
    }

    @Override
    protected void updateItem(ClusterTreeValue item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (item.getType().equals(ClusterTreeValue.CELLTYPE.HEADER)){
                setGraphic(item.getHeader());
            } else {

                // for now only other type is TABLE
                setGraphic(item.getTable());
            }
            setText(null);
        }
    }
}
