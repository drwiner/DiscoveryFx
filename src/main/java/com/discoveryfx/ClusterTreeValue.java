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

public class ClusterTreeValue {

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

//        dynamicColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(0d));

//        Client.getMagicSelectionTable().avgUpdated.addListener((observableValue1, number, t11) -> {
//            if (t11 != null && t11)
//                dynamicColumn.setCellValueFactory(param -> {
//                    if (Client.getMagicSelectionTable() == null)
//                        return new ReadOnlyObjectWrapper<>(0d);
//
//                    float[] currentAvg = Client.getMagicSelectionTable().getCurrentAvg();
//                    float[] ourVector = Client.getProcUttToEmb().getOrDefault(param.getValue().getDatum(), new float[768]);
//
//                    if (currentAvg == null || ourVector == null){
////                LOG.info(param.getValue().getDatum());
//                        param.getValue().setMagicValue(0d);
//                        return new ReadOnlyObjectWrapper<>(0d);
//                    }
//
//                    double v = ModelIO.cosineSim(currentAvg, ourVector);
//                    param.getValue().setMagicValue(v);
//                    return new ReadOnlyObjectWrapper<>(DataTableHelper.round(v, 4));
//                });
//        });
//
        List<TableColumn<ClusterDatum, ?>> arrayList = new ArrayList<>();
        arrayList.add(sentenceColumn);
        arrayList.add(countColumn);
        arrayList.add(confidenceColumn);
        arrayList.add(dynamicColumn); // expected to be last.

        return arrayList;

    }

//    private ReadOnlyObjectWrapper<Double> setCellFactoryAverages(TableColumn.CellDataFeatures<ClusterDatum, Double> param){
//        if (Client.getMagicSelectionTable() == null)
//            return new ReadOnlyObjectWrapper<>(0d);
//
//        float[] currentAvg = Client.getMagicSelectionTable().getCurrentAvg();
//        float[] ourVector = Client.getProcUttToEmb().getOrDefault(param.getValue().getDatum(), new float[768]);
//
//        if (currentAvg == null || ourVector == null){
////                LOG.info(param.getValue().getDatum());
//            param.getValue().setMagicValue(0d);
//            return new ReadOnlyObjectWrapper<>(0d);
//        }
//
//        double v = ModelIO.cosineSim(currentAvg, ourVector);
//        param.getValue().setMagicValue(v);
//        return new ReadOnlyObjectWrapper<>(DataTableHelper.round(v, 4));
//    }
}
