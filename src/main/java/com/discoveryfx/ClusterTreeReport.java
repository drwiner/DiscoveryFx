package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterResult;
import com.discoveryfx.com.kasisto.cluster.ClusterResultSummary;
import com.discoveryfx.com.kasisto.cluster.ClusterServiceOutput;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.ArrayList;
import java.util.List;

public class ClusterTreeReport  extends TreeView<ClusterTreeValue> implements TableReportInterface<ClusterTreeValue>{

    public List<InteractiveTableView> tables = new ArrayList<>();


    public ClusterTreeReport(ClusterServiceOutput clusterReport) {
        super();

        setRoot(buildRoot(clusterReport.getClusters()));

        getRoot().setExpanded(true);
        setShowRoot(false);

        setCellFactory(c -> new ClusterTreeCell());
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }


    @Override
    public List<InteractiveTableView> getTables() {
        return tables;
    }

    @Override
    public TreeItem<ClusterTreeValue> getTableAtIndex(int index){
//        return null;
        if (getRoot().getChildren().size() <= index)
            return null;
        return getRoot().getChildren().get(index).getChildren().get(0);
    }


    @Override
    public void removeTable(TreeItem<ClusterTreeValue> item){
        tables.remove(item.getValue().getTable());
        TreeItem<ClusterTreeValue> parent = item.getParent();
        getRoot().getChildren().remove(parent);
    }

    private TreeItem<ClusterTreeValue> buildRoot(List<ClusterResult> results){
        int clusterNum = 0;

        TreeItem<ClusterTreeValue> root = new TreeItem<>();
        for (ClusterResult result: results){
            if (result == null)
                continue;

            ClusterResultSummary summary = result.getSummary();

            String clusterTableName = "C" + clusterNum++;

            // Create Header.
            ClusterTreeValue clusterTreeValue = new ClusterTreeValue(clusterTableName, summary.getClusterSize(), summary.getRepresentativeSentence());
            TreeItem<ClusterTreeValue> clusterHeaderItem = new TreeItem<>(clusterTreeValue);
            root.getChildren().add(clusterHeaderItem);
            clusterHeaderItem.setExpanded(false);


            /*
                Table,
                Closest Intents
                    - each intent is an intent header,
                    - each intent has some kind of control panel.
             */

            ClusterTreeValue clusterTableValue = new ClusterTreeValue(clusterTableName, result);
            TreeItem<ClusterTreeValue> clusterTableItem = new TreeItem<>(clusterTableValue);
            clusterHeaderItem.getChildren().add(clusterTableItem);
            tables.add(clusterTableValue.getTable());

//            if (result.getSummary().getClosestIntents() != null){
//                for (ClusterClosestIntent intent: result.getSummary().getClosestIntents()){
//                    ClusterTreeValue clusterClosestIntentValue = new ClusterTreeValue(intent);
//                }
//            }


        }

        return root;
    }

}
