package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterDatum;
import com.discoveryfx.com.kasisto.cluster.ClusterEval;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.discoveryfx.com.kasisto.cluster.Cluster.DF;

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
        ModelIO.DataPackageModel dataPackageModel = Client.getDataPackageModel();
        Map<String, Integer> packageUtteranceToIndex = dataPackageModel.packageUtteranceToIndex;
        Map<Integer, Integer> packageIndexToLabel = dataPackageModel.packageIndexToLabel;
        Map<String, float[]> procUttToEmb = Client.getProcUttToEmb();
        Map<String, Integer> visitedUtterances = new HashMap<>();
        ObservableList<ClusterDatum> data = FXCollections.observableArrayList(intentDataDocument.getIntentData().stream()
                .map(datum -> {

                    if (visitedUtterances.containsKey(datum.getTriggerSentenceText())){
                        // Then go add count if it's in the same intent. which for now we assume is true.
                        visitedUtterances.put(datum.getTriggerSentenceText(), visitedUtterances.get(datum.getTriggerSentenceText()) + 1);
                        return null;
                    } else
                        visitedUtterances.put(datum.getTriggerSentenceText(), 1);

                    int index = packageUtteranceToIndex.get(datum.getTriggerSentenceText());
                    double sim = 0d;
                    float[] vector = new float[768];
                    if (index >=0) {
                        vector = dataPackageModel.getPackageEmbeddings().get(index);
                        if (!procUttToEmb.containsKey(datum.getTriggerSentenceText())){
                            procUttToEmb.put(datum.getTriggerSentenceText(), vector);
                        }
                        Integer integer = packageIndexToLabel.get(index);
                        float[] centroid = dataPackageModel.getIntentCentroids().get(integer);
                        sim = ModelIO.cosineSim(vector, centroid);
                    }
                    ClusterDatum cDatum = new ClusterDatum((float) sim, datum.getTriggerSentenceText(), index, 1);
                    cDatum.setIntentDataId(datum.getId());
                    return cDatum;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        data.stream().filter(datum -> visitedUtterances.get(datum.getDatum()) > 1).forEach(datum -> {
            datum.setCount(visitedUtterances.get(datum.getDatum()));
            datum.setSilhouette(DF.format(ClusterEval.getSilhouetteCoefficient(datum, intentDataDocument, Client.getIntentDataDocuments())));
        });

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
        dynamicColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(DataTableHelper.round(param.getValue().getMagicValue(), 4)));

        List<TableColumn<ClusterDatum, ?>> columns = new ArrayList<>();
        columns.add(sentenceColumn);
        columns.add(countColumn);
        columns.add(confidenceColumn);
        columns.add(dynamicColumn);

        return columns;
    }
}
