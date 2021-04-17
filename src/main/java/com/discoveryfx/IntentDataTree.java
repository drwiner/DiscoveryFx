package com.discoveryfx;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntentDataTree extends TreeView<IntentTreeValue> {

    private Map<String, TreeItem<IntentTreeValue>> intentItemMap;

    public IntentDataTree(List<IntentDataDocument> intentData) {
        super();

        intentItemMap = new HashMap<>();
        setRoot(buildRoot(intentData));

        getRoot().setExpanded(true);
        setShowRoot(false);

        setCellFactory(c -> new IntentTreeCell());

    }

    private TreeItem<IntentTreeValue> buildRoot(List<IntentDataDocument> documents){

        TreeItem<IntentTreeValue> root = new TreeItem<>();
        for (IntentDataDocument result: documents) {
            if (result == null)
                continue;

            /*
             * For each intent,
             */

            if (result.getScope() != IntentData.Scope.SEED){
                continue;
            }


            IntentTreeValue header = new IntentTreeValue(result.getIntentName(), result.getIntentData().size(), Client.getDataPackageModel().getIntentDisplaySentences().get(result.getIntentName()));

            TreeItem<IntentTreeValue> intentHeaderItem = new TreeItem<>(header);
            root.getChildren().add(intentHeaderItem);

            IntentTreeValue intentTreeValue = new IntentTreeValue(result);
            TreeItem<IntentTreeValue> intentTableItem = new TreeItem<>(intentTreeValue);
            intentHeaderItem.getChildren().add(intentTableItem);


//
//            ClusterResultSummary summary = result.getSummary();
//
//            // Create Header.
//            ClusterTreeValue clusterTreeValue = new ClusterTreeValue("C" + clusterNum++, summary.getClusterSize(), summary.getRepresentativeSentence());
//            TreeItem<ClusterTreeValue> clusterHeaderItem = new TreeItem<>(clusterTreeValue);
//            root.getChildren().add(clusterHeaderItem);

            /*
                Table,
                Closest Intents
                    - each intent is an intent header,
                    - each intent has some kind of control panel.
             */

//            ClusterTreeValue clusterTableValue = new ClusterTreeValue(result);
//            TreeItem<ClusterTreeValue> clusterTableItem = new TreeItem<>(clusterTableValue);
//            clusterHeaderItem.getChildren().add(clusterTableItem);

//            if (result.getSummary().getClosestIntents() != null){
//                for (ClusterClosestIntent intent: result.getSummary().getClosestIntents()){
//                    ClusterTreeValue clusterClosestIntentValue = new ClusterTreeValue(intent);
//                }
//            }

        }


        return root;
    }
}
