package com.discoveryfx;

import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;

import java.util.List;

public interface TableReportInterface<T>{

    List<InteractiveTableView> getTables();

    TreeItem<T> getTableAtIndex(int index);
    void removeTable(TreeItem<T> item);

    MultipleSelectionModel<TreeItem<T>> getSelectionModel();


}
