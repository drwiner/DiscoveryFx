package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterResult;
import com.discoveryfx.com.kasisto.cluster.ClusterResultSummary;
import com.discoveryfx.com.kasisto.cluster.ClusterServiceOutput;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.List;

public class ClusterTreeReport extends TreeView<ClusterTreeValue> {

    public ClusterTreeReport(ClusterServiceOutput clusterReport) {
        super();

        setRoot(buildRoot(clusterReport.getClusters()));

        getRoot().setExpanded(true);
        setShowRoot(false);

        setCellFactory(c -> new ClusterTreeCell());

    }

    private TreeItem<ClusterTreeValue> buildRoot(List<ClusterResult> results){
        int clusterNum = 0;

        TreeItem<ClusterTreeValue> root = new TreeItem<>();
        for (ClusterResult result: results){
            if (result == null)
                continue;

            ClusterResultSummary summary = result.getSummary();

            // Create Header.
            ClusterTreeValue clusterTreeValue = new ClusterTreeValue("C" + clusterNum++, summary.getClusterSize(), summary.getRepresentativeSentence());
            TreeItem<ClusterTreeValue> clusterHeaderItem = new TreeItem<>(clusterTreeValue);
            root.getChildren().add(clusterHeaderItem);

            /*
                Table,
                Closest Intents
                    - each intent is an intent header,
                    - each intent has some kind of control panel.
             */

            ClusterTreeValue clusterTableValue = new ClusterTreeValue(result);
            TreeItem<ClusterTreeValue> clusterTableItem = new TreeItem<>(clusterTableValue);
            clusterHeaderItem.getChildren().add(clusterTableItem);

//            if (result.getSummary().getClosestIntents() != null){
//                for (ClusterClosestIntent intent: result.getSummary().getClosestIntents()){
//                    ClusterTreeValue clusterClosestIntentValue = new ClusterTreeValue(intent);
//                }
//            }


        }

        return root;
    }
}