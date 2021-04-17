package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterServiceOutput;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client extends Application{

    private static final int LEFT_WIDTH = 800;
    private static final int RIGHT_WIDTH = 600;
    private static final int HEIGHT = 800;

    private static ClusterServiceOutput clusterReport;
    private static Map<String, float[]> procUttToEmb;
    private static ModelIO.DataPackageModel dataPackageModel;

    private static List<IntentDataDocument> intentDataDocuments;

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

    private Parent createRoot() {
        BorderPane root = new BorderPane();
        root.setPrefWidth(LEFT_WIDTH + RIGHT_WIDTH);
        root.setPrefHeight(HEIGHT);

        VBox leftBox = new VBox();
        VBox rightBox = new VBox();

        TabPane leftSideHeader = new TabPane();
        Tab clusterReportTab = new Tab("Cluster Report 1");
        clusterReportTab.setClosable(false);
        Tab reort2Tab = new Tab("Cluster Report 2");
        reort2Tab.setClosable(true);

        leftSideHeader.getTabs().addAll(clusterReportTab, reort2Tab);


        TabPane rightSideHeader = new TabPane();
        Tab intentTab = new Tab("Intent Data");
        intentTab.setClosable(false);
        Tab clusterVizTab = new Tab("Cluster Visualization");
        clusterVizTab.setClosable(false);
        rightSideHeader.getTabs().addAll(intentTab, clusterVizTab);


        Pane leftSideReport = new Pane();
        Pane rightSideData = new Pane();

        ClusterTreeReport clusterTreeReport = new ClusterTreeReport(getClusterReport());
        clusterTreeReport.minWidthProperty().bind(leftSideReport.widthProperty());
        clusterTreeReport.minHeightProperty().bind(leftSideReport.heightProperty());
        leftSideReport.getChildren().add(clusterTreeReport);

        IntentDataTree intentDataTree = new IntentDataTree(getIntentDataDocuments());
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

        MagicSelectionTable magicSelectionTable = new MagicSelectionTable();
        TitledPane magicSelector = new TitledPane("Magic Selector", magicSelectionTable);
        magicSelectionTable.setPrefHeight(200);
        magicSelector.setExpanded(false);
        root.setBottom(magicSelector);

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
