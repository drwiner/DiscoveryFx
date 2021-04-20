package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterDatum;
import com.discoveryfx.com.kasisto.cluster.ClusterServiceOutput;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.discoveryfx.DatumInteractionManager.TableViewEnum.*;

public class Client extends Application{

    private static final int LEFT_WIDTH = 800;
    private static final int RIGHT_WIDTH = 800;
    private static final int HEIGHT = 800;

    private static ClusterServiceOutput clusterReport;
    private static Map<String, float[]> procUttToEmb;
    private static ModelIO.DataPackageModel dataPackageModel;

    private static List<IntentDataDocument> intentDataDocuments;
    private static MagicSelectionTable magicSelectionTable;
    private static AudienceSelectionTable audienceSelectionTable;
    private BooleanBinding noMagicSelected;
    private static ClusterTreeReport clusterTreeReport;
    private static IntentDataTree intentDataTree;

    public static ClusterTreeReport getClusterTreeReport() {
        return clusterTreeReport;
    }

    public static IntentDataTree getIntentDataTree() {
        return intentDataTree;
    }

    public static ModelIO.DataPackageModel getDataPackageModel() {
        return dataPackageModel;
    }

    public static List<IntentDataDocument> getIntentDataDocuments() {
        return intentDataDocuments;
    }

    public static ClusterServiceOutput getClusterReport() {
        return clusterReport;
    }

    public static Map<String, float[]> getProcUttToEmb() {
        return procUttToEmb;
    }

    public static MagicSelectionTable getMagicSelectionTable() {
        return magicSelectionTable;
    }

    public static AudienceSelectionTable getAudienceSelectionTable() {
        return audienceSelectionTable;
    }

    private Parent createRoot() {
        BorderPane root = new BorderPane();
        root.setPrefWidth(LEFT_WIDTH + RIGHT_WIDTH);
        root.setPrefHeight(HEIGHT);

        magicSelectionTable = new MagicSelectionTable();
        TitledPane magicSelector = new TitledPane("Magic Selector", magicSelectionTable);
        magicSelectionTable.setPrefHeight(200);
        magicSelector.setExpanded(true);

        Button clear = new Button("Clear");
        clear.disableProperty().bind(magicSelectionTable.isClear);
        clear.setOnAction(e -> {
            magicSelectionTable.clear();
        });
        Button remove = new Button("Remove");
        noMagicSelected = Bindings.isEmpty(magicSelectionTable.getSelectionModel().getSelectedItems());
        remove.disableProperty().bind(noMagicSelected);
        remove.setOnAction(e -> {
            List<ClusterDatum> selectedItems = new ArrayList<>(magicSelectionTable.getSelectionModel().getSelectedItems());
            if (magicSelectionTable.getSelectionModel() == null)
                System.out.println("Fund");
            else {
                for (ClusterDatum selected : selectedItems)
                    magicSelectionTable.remove(selected);
            }
        });
        HBox magicFields = new HBox(clear, remove);
        magicSelector.setGraphic(magicFields);

        VBox leftBox = new VBox();
        VBox rightBox = new VBox();

        TabPane leftSideHeader = new TabPane();
        Tab clusterReportTab = new Tab("Cluster Report 1");
        clusterReportTab.setClosable(false);
//        Tab reort2Tab = new Tab("Cluster Report 2");
//        reort2Tab.setClosable(true);

        leftSideHeader.getTabs().addAll(clusterReportTab);


        TabPane rightSideHeader = new TabPane();
        Tab intentTab = new Tab("Intent Data");
        intentTab.setClosable(false);
//        Tab clusterVizTab = new Tab("Cluster Visualization");
//        clusterVizTab.setClosable(false);
        rightSideHeader.getTabs().addAll(intentTab);


        Pane leftSideReport = new Pane();
        Pane rightSideData = new Pane();

        clusterTreeReport = new ClusterTreeReport(getClusterReport());
        clusterTreeReport.minWidthProperty().bind(leftSideReport.widthProperty());
        clusterTreeReport.minHeightProperty().bind(leftSideReport.heightProperty());
        leftSideReport.getChildren().add(clusterTreeReport);

        intentDataTree = new IntentDataTree(getIntentDataDocuments());
        intentDataTree.minWidthProperty().bind(rightSideData.widthProperty());
        intentDataTree.minHeightProperty().bind(rightSideData.heightProperty());
        rightSideData.getChildren().add(intentDataTree);

        HBox.setHgrow(leftSideReport, Priority.ALWAYS);
        VBox.setVgrow(leftSideReport, Priority.ALWAYS);

        leftBox.getChildren().addAll(leftSideHeader, leftSideReport);
        leftBox.setPrefWidth(LEFT_WIDTH);
        rightBox.getChildren().addAll(rightSideHeader, rightSideData);
        rightBox.setPrefWidth(RIGHT_WIDTH);

        HBox hBox = new HBox(leftBox, rightBox);
        root.setCenter(hBox);

        HBox bottom = new HBox();

//        root.setTop(magicSelector);

        magicSelector.setPrefWidth(LEFT_WIDTH);
        bottom.getChildren().add(magicSelector);


        // ToDO: track when data leaves the cluster report / intent data so it doesn't appear here. or just set opacity or something.
        audienceSelectionTable = new AudienceSelectionTable();

        ChoiceBox<DatumInteractionManager.TableViewEnum> tableViewEnumChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(ALL, CLUSTER, INTENT));
        tableViewEnumChoiceBox.setValue(ALL);

        TextField simField = new TextField("0.8");
        simField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1 != null) {
                try {
                    double v = Double.parseDouble(t1);
                    audienceSelectionTable.minMagicConf.setValue(v);
                } catch (NumberFormatException ignored) {

                }
            }
        });

        HBox audienceFilters = new HBox(tableViewEnumChoiceBox, simField);

        TitledPane magicAudience = new TitledPane("Magic Audience", audienceSelectionTable);
        magicAudience.setGraphic(audienceFilters);
        audienceSelectionTable.setPrefHeight(200);
        magicAudience.setExpanded(true);
//        root.setBottom(magicAudience);
        magicAudience.setPrefWidth(RIGHT_WIDTH);
        bottom.getChildren().add(magicAudience);
        root.setBottom(bottom);

        return root;
    }


    @Override
    public void start(Stage stage) {

        String clusterReportFile = ApplicationProperties.getProperty(ApplicationProperties.Property.CLUSTER_REPORT);
        clusterReport = ClusterReportIO.readClusterReportFile(clusterReportFile);

        String inputDataModel = ApplicationProperties.getProperty(ApplicationProperties.Property.INPUT_DATA_MODEL);
        List<float[]> inputModel = ModelIO.loadFromBinary(inputDataModel);

        String inputDataText = ApplicationProperties.getProperty(ApplicationProperties.Property.INPUT_DATA_TXT);
        List<String> inputData = ClusterReportIO.readDataFromInput(inputDataText);

        assert(inputData.size() == inputModel.size());

        procUttToEmb = new HashMap<>();

        for (int i=0; i< inputData.size(); i++){
            procUttToEmb.put(inputData.get(i), inputModel.get(i));
        }

        intentDataDocuments = IntentDataDocument.getAllIntents();
        dataPackageModel = new ModelIO.DataPackageModel();

        stage.setScene(new Scene(createRoot()));
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
