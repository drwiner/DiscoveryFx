package com.discoveryfx;

import javafx.scene.control.TableColumn;

public class DataColumnFormat {
    public static enum ColumnType {STRING, DECIMAL, INTEGER}
    private int minWidth;
    private String title;
    private ColumnType columnType;

    public int getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

    public DataColumnFormat(int minWidth, String title, ColumnType columnType) {
        this.minWidth = minWidth;
        this.title = title;
        this.columnType = columnType;
    }

    //    TableColumn<ClusterDatum, String> sentenceColumn = new TableColumn<>("Request Text");
//        sentenceColumn.setMinWidth(500);
//        sentenceColumn.setCellValueFactory(param -> {
//            if (param == null)
//                return null;
//
//            if (param.getValue() == null)
//                return null;
//
//            if (param.getValue().getDatum() == null)
//                return null;
//
//            return new ReadOnlyStringWrapper(param.getValue().getDatum());
//        });
}
