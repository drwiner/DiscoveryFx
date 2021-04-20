package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterDatum;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IntentTreeValue {

    private IntentHeader header;
    private InteractiveTableView table;
    private CELLTYPE type;

    public enum CELLTYPE {HEADER, TABLE}

    public IntentTreeValue(String name, Integer size, String repSentence) {
        header = new IntentHeader(name, size, repSentence);
        this.type = CELLTYPE.HEADER;
    }

    public IntentTreeValue(IntentDataDocument intentDataDocument) {
        ObservableList<ClusterDatum> data = FXCollections.observableArrayList(intentDataDocument.getIntentData().stream()
                .map(datum -> new ClusterDatum(0f, datum.getTriggerSentenceText(), 0, 1))
                .collect(Collectors.toList()));
        table = new InteractiveTableView(intentDataDocument.getIntentName(), DatumInteractionManager.TableViewEnum.INTENT,
                data, createColumns());
        this.type = CELLTYPE.TABLE;
    }


    public IntentHeader getHeader() {
        return header;
    }

    public void setHeader(IntentHeader header) {
        this.header = header;
    }

    public InteractiveTableView getTable() {
        return table;
    }

    public void setTable(InteractiveTableView table) {
        this.table = table;
    }

    public CELLTYPE getType() {
        return type;
    }

    public void setType(CELLTYPE type) {
        this.type = type;
    }

    private List<TableColumn<ClusterDatum, ?>> createColumns(){
        TableColumn<ClusterDatum, String> sentenceColumn = new TableColumn<>("Request Text");
        sentenceColumn.setMinWidth(500);
        sentenceColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getDatum()));

        TableColumn<ClusterDatum, Integer> countColumn = new TableColumn<>("Count");
        countColumn.setMinWidth(50);
        countColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getCount()));

        TableColumn<ClusterDatum, Double> confidenceColumn = new TableColumn<>("Confidence");
        confidenceColumn.setMinWidth(50);
        confidenceColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(new BigDecimal(param.getValue().getConfidence()).doubleValue()));

        TableColumn<ClusterDatum, Double> dynamicColumn = new TableColumn<>("Magic Sim");
        dynamicColumn.setMinWidth(50);


        dynamicColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(0d));

        List<TableColumn<ClusterDatum, ?>> columns = new ArrayList<>();
        columns.add(sentenceColumn);
        columns.add(countColumn);
        columns.add(confidenceColumn);
        columns.add(dynamicColumn);

        return columns;
    }
}
