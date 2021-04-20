package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterDatum;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MagicSelectionTable extends InteractiveTableView {
    public BooleanProperty isClear = new SimpleBooleanProperty(true);
    public BooleanProperty avgUpdated = new SimpleBooleanProperty(false);
    public BooleanProperty audienceUpdated = new SimpleBooleanProperty(false);

    private static final Logger LOG= LoggerFactory.getLogger(AudienceSelectionTable.class);

    private List<ClusterDatum> data = new ArrayList<>();


    private float[] currentSum = new float[768];
    private float[] currentAvg = new float[768];

    public float[] getCurrentAvg() {
        return currentAvg;
    }

    private void updateListeners(){
        // Idea is this toggles listeners.
        LOG.info("Start Updated Average");
        avgUpdated.setValue(true);
        avgUpdated.setValue(false);

//        Client.getAudienceSelectionTable().refilterData();

    }

    private void updateAudience(){
        LOG.info("Start Updated Audience");

//        Client.getAudienceSelectionTable().refilterData();
        audienceUpdated.setValue(true);
        audienceUpdated.setValue(false);
//
//        PauseTransition pause = new PauseTransition(Duration.seconds(1));
//        pause.setOnFinished(e -> {
//            audienceUpdated.setValue(true);
//            audienceUpdated.setValue(false);
//        });
//        pause.play();
        LOG.info("End Updated Audience");
    }

    public MagicSelectionTable() {
        super("MAGIC", DatumInteractionManager.TableViewEnum.MAGIC, new ArrayList<>(), new ArrayList<>(), false, true);

        TableColumn<ClusterDatum, String> sentenceColumn = new TableColumn<>("Request Text");
        sentenceColumn.setMinWidth(500);
        sentenceColumn.setPrefWidth(400);
        sentenceColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getDatum()));

        TableColumn<ClusterDatum, String> sourceColumn = new TableColumn<>("Source");
        sourceColumn.setMinWidth(50);
        sourceColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getSource()));

        getColumns().add(sentenceColumn);
        getColumns().add(sourceColumn);

        avgUpdated.addListener((observableValue, aBoolean, t1) -> {
            if (!t1 && aBoolean) {
                LOG.info("Triggered Audience");
                updateAudience();
            }
        });
    }


    @Override
    public void add(ClusterDatum datum){
        getItems().add(datum);
        data.add(datum);

        float[] floats = Client.getProcUttToEmb().getOrDefault(datum.getDatum(), new float[768]);

        for (int i=0; i < floats.length; i++){
            currentSum[i] += floats[i];
            currentAvg[i] = currentSum[i]/data.size();
        }

        LOG.info("Calculated Magic Average");

        isClear.setValue(false);

        updateListeners();
//        updateAudience();
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

        LOG.info("Calculated Magic Average");

        getItems().remove(datum);

        updateListeners();
//        updateAudience();
    }


    public void clear(){
        isClear.setValue(true);
        currentSum = new float[768];
        currentAvg = new float[768];
        getItems().clear();
        data.clear();

        LOG.info("Calculated Magic Average");

        updateListeners();
//        updateAudience();
    }
}
