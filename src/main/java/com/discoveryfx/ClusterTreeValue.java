package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterDatum;
import com.discoveryfx.com.kasisto.cluster.ClusterResult;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ClusterTreeValue implements TabelTreeValueInterface{

    private ClusterHeader header;
    private InteractiveTableView table;
    private CELLTYPE type;

    public enum CELLTYPE {HEADER, TABLE, CLOSEST_INTENT_HEADER, CLOSEST_INTENT_CONTROL_PANEL}

    ;

    public ClusterTreeValue(String name, Integer size, String repSentence) {
        header = new ClusterHeader(name, size, repSentence);
        this.type = CELLTYPE.HEADER;
    }

    public ClusterTreeValue(String clusterTableName, ClusterResult cluster) {
        table = new InteractiveTableView(clusterTableName, DatumInteractionManager.TableViewEnum.CLUSTER, cluster.getDatums(), createColumns());
        this.type = CELLTYPE.TABLE;
    }

//    public CLusterTreeValue(

    public CELLTYPE getType() {
        return type;
    }

    public ClusterHeader getHeader() {
        return header;
    }

    public void setHeader(ClusterHeader header) {
        this.header = header;
    }

    public InteractiveTableView getTable() {
        return table;
    }

    public void setTable(InteractiveTableView table) {
        this.table = table;
    }

    public void setType(CELLTYPE type) {
        this.type = type;
    }

    private List<TableColumn<ClusterDatum, ?>> createColumns(){
        TableColumn<ClusterDatum, String> sentenceColumn = new TableColumn<>("Request Text");
        sentenceColumn.setMinWidth(500);
        sentenceColumn.setCellValueFactory(param -> {
            if (param == null)
                return null;

            if (param.getValue() == null)
                return null;

            if (param.getValue().getDatum() == null)
                return null;

            return new ReadOnlyStringWrapper(param.getValue().getDatum());
        });

        TableColumn<ClusterDatum, Integer> countColumn = new TableColumn<>("Count");
        countColumn.setMinWidth(50);
        countColumn.setCellValueFactory(param -> {
            if (param == null)
                return null;
            if (param.getValue() == null)
                return null;
            return new ReadOnlyObjectWrapper<>(param.getValue().getCount());
        });

        TableColumn<ClusterDatum, Double> confidenceColumn = new TableColumn<>("Confidence");
        confidenceColumn.setMinWidth(50);
        confidenceColumn.setCellValueFactory(param -> {
            if (param == null)
                return null;
            if (param.getValue() == null)
                return null;
            return new ReadOnlyObjectWrapper<>(new BigDecimal(param.getValue().getConfidence()).doubleValue());
        });

        TableColumn<ClusterDatum, Double> dynamicColumn = new TableColumn<>("Magic Sim");
        dynamicColumn.setMinWidth(50);
        dynamicColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(DataTableHelper.round(param.getValue().getMagicValue(), 4)));

        List<TableColumn<ClusterDatum, ?>> arrayList = new ArrayList<>();
        arrayList.add(sentenceColumn);
        arrayList.add(countColumn);
        arrayList.add(confidenceColumn);

        TableColumn<ClusterDatum, String> silhoutte = new TableColumn<>("Silhouette");
        silhoutte.setMinWidth(50);
        silhoutte.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getSilhouette()));

        arrayList.add(silhoutte);
        arrayList.add(dynamicColumn); // expected to be last.


        return arrayList;

    }
}
