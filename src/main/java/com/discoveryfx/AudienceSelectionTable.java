package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterDatum;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.discoveryfx.Client.getClusterTreeReport;
import static com.discoveryfx.com.kasisto.cluster.Cluster.DF;

public class AudienceSelectionTable extends InteractiveTableView {

    private static final Logger LOG= LoggerFactory.getLogger(AudienceSelectionTable.class);
    private AllTrueBinding allTrueBinding;


    public DoubleProperty minMagicConf = new SimpleDoubleProperty(0.8);

    public DatumInteractionManager.TableViewEnum typeFilter = DatumInteractionManager.TableViewEnum.ALL;

//    private List<ClusterDatum> data = new ArrayList<>();


    public AudienceSelectionTable() {
        super("AUDIENCE", DatumInteractionManager.TableViewEnum.AUDIENCE, new ArrayList<>(), new ArrayList<>(), false, false);

        TableColumn<ClusterDatum, String> sentenceColumn = new TableColumn<>("Request Text");
        sentenceColumn.setMinWidth(500);
        sentenceColumn.setPrefWidth(400);
        sentenceColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getDatum()));

        TableColumn<ClusterDatum, Double> confColumn = new TableColumn<>("Confidence");
        confColumn.setMinWidth(50);
        confColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(DataTableHelper.round(param.getValue().getMagicValue(), 4)));

        TableColumn<ClusterDatum, String> sourceColumn = new TableColumn<>("Source");
        sourceColumn.setMinWidth(50);
        sourceColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getSource()));

        getColumns().add(sentenceColumn);
        getColumns().add(confColumn);
        getColumns().add(sourceColumn);

//        data = allData;

//        Client.getMagicSelectionTable().audienceUpdated.addListener((observableValue, aBoolean, t1) -> {
//            /*
//             *Every time the average changes, we will sort this list by magic.
//             */
//
//            if (t1 != null && t1) {
//                refilterData();
//            }
//        });

        minMagicConf.addListener((observableValue, number, t1) -> {
            refilterData(typeFilter);
        });

        allTrueBinding = new AllTrueBinding(FXCollections.observableArrayList(
                Stream.concat(getClusterTreeReport().tables.stream(), Client.getIntentDataTree().tables.stream())
                .map(t -> t.updatedValuesProperty).collect(Collectors.toList())));


        allTrueBinding.addListener((observableValue, aBoolean, t1) -> {
            if (t1 != null && t1) {
                refilterData(Client.getTableViewEnumChoiceBox().getValue());
                Stream.concat(getClusterTreeReport().tables.stream(), Client.getIntentDataTree().tables.stream())
                        .forEach(t -> t.updatedValuesProperty.setValue(false));
            }
        });

        Client.getTableViewEnumChoiceBox().getSelectionModel().selectedItemProperty().addListener((observableValue, tableViewEnum, t1) -> {
            if (t1 != tableViewEnum  && t1 != null){
                refilterData(t1);
            }
        });

//        Bindings.createBooleanBinding(() ->
//            Stream.of(Client.getClusterTreeReport().tables).allMatch(t -> t.updatedValuesProperty)
//        );
//        BooleanBinding.booleanExpression(Client.getClusterTreeReport().tables.updatedValuesProperty)
//
//        Client.getClusterTreeReport().tables.forEach(table -> {
//            table.getMagicColumn().cellValueFactoryProperty().addListener((observableValue, cellDataFeaturesObservableValueCallback, t1) -> {
//                if (t1 != null)
//                    refilterData();
//            });
//        });
//        Client.getClusterTreeReport().tables.stream().flatMap(t -> t.getItems().stream()),
//                Client.getIntentDataTree().tables.stream().flatMap(t -> t.getItems().stream()))
    }


    public void refilterData(DatumInteractionManager.TableViewEnum whichData){
        LOG.info("Refiltering Data in Audience");


        Platform.runLater(() -> {
            getItems().clear();
//            getItems().removeAll(data);
//            setItems(FXCollections.emptyObservableList());
//        setItems(FXCollections.emptyObservableList());

            if (whichData.equals(DatumInteractionManager.TableViewEnum.ALL)) {
                data = Stream.concat(
                        getClusterTreeReport().tables.stream().flatMap(t -> t.getItems().stream()),
                        Client.getIntentDataTree().tables.stream().flatMap(t -> t.getItems().stream()))
                        .filter(datum -> datum.getMagicValue() >= minMagicConf.get())
                        .sorted(new ClusterDatum.MagicComparator())
                        .collect(Collectors.toList());
            } else if (whichData.equals(DatumInteractionManager.TableViewEnum.CLUSTER)){
                data = getClusterTreeReport().tables.stream().flatMap(t -> t.getItems().stream())
                        .filter(datum -> datum.getMagicValue() >= minMagicConf.get())
                        .sorted(new ClusterDatum.MagicComparator())
                        .collect(Collectors.toList());
            } else if (whichData.equals(DatumInteractionManager.TableViewEnum.INTENT)) {
                data = Client.getIntentDataTree().tables.stream().flatMap(t -> t.getItems().stream())
                        .filter(datum -> datum.getMagicValue() >= minMagicConf.get())
                        .sorted(new ClusterDatum.MagicComparator())
                        .collect(Collectors.toList());
            }


            setItems(FXCollections.observableArrayList(data));
            refresh();
            LOG.info("Finished Refiltering Data in Audience");
        });



//        data.sort(new ClusterDatum.MagicComparator());
//        getItems().clear();
//
//
////        for (int i=0; i< 3; i++) {
////            LOG.info(data.get(i).getDatum() + ": " + DF.format(data.get(i).getMagicValue()));
////        }
//        setItems(FXCollections.observableArrayList(data.stream().filter(datum -> datum.getMagicValue() >= minMagicConf.get()).collect(Collectors.toList())));
    }

    // TODO methods to Add and remove data.


}
