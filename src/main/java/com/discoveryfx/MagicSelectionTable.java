package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterDatum;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class MagicSelectionTable extends TableView<ClusterDatum> {
    private BooleanProperty isClear = new SimpleBooleanProperty(false);

    private List<ClusterDatum> data = new ArrayList<>();

    private float[] currentSum = new float[768];
    private float[] currentAvg = new float[768];

    public MagicSelectionTable() {
        super();

        TableColumn<ClusterDatum, String> sentenceColumn = new TableColumn<>("Request Text");
        sentenceColumn.setMinWidth(500);
        sentenceColumn.setPrefWidth(900);
        sentenceColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getDatum()));

        TableColumn<ClusterDatum, String> sourceColumn = new TableColumn<>("Source");
        sourceColumn.setMinWidth(50);
        sourceColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getSource()));

        getColumns().add(sentenceColumn);
        getColumns().add(sourceColumn);

    }



    public void add(ClusterDatum datum, String source){
        datum.setSource(source);
        data.add(datum);

        float[] floats = Client.getProcUttToEmb().getOrDefault(datum.getDatum(), new float[768]);

        for (int i=0; i < floats.length; i++){
            currentSum[i] += floats[i];
            currentAvg[i] /= currentSum[i]/data.size();
        }

        isClear.setValue(false);
    }

    public void remove(ClusterDatum datum){
        data.remove(datum);

        if (data.size() == 0){
            clear();
            return;
        }

        float[] floats = Client.getProcUttToEmb().getOrDefault(datum.getDatum(), new float[768]);

        for (int i=0; i < floats.length; i++){
            currentSum[i] -= floats[i];
            currentAvg[i] = currentSum[i]/data.size();
        }
    }

    public void clear(){
        isClear.setValue(true);
        currentSum = new float[768];
        currentAvg = new float[768];
    }
}
