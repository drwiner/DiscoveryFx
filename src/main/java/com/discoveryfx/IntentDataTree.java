package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterEval;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.discoveryfx.com.kasisto.cluster.Cluster.DF;

public class IntentDataTree extends TreeView<IntentTreeValue> implements TableReportInterface<IntentTreeValue>{

    private Map<String, TreeItem<IntentTreeValue>> intentItemMap;

    public List<InteractiveTableView> tables = new ArrayList<>();

    public List<InteractiveTableView> getTables() {
        return tables;
    }

    @Override
    public TreeItem<IntentTreeValue> getTableAtIndex(int index) {
        if (getRoot().getChildren().size() <= index)
            return null;
        return getRoot().getChildren().get(index).getChildren().get(0);
    }

    @Override
    public void removeTable(TreeItem<IntentTreeValue> item) {
        tables.remove(item.getValue().getTable());
        TreeItem<IntentTreeValue> parent = item.getParent();
        getRoot().getChildren().remove(parent);
    }


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
            tables.add(intentTreeValue.getTable());


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

        // Create silhouettes
        tables.forEach(table -> {
            table.getData().forEach(datum -> {
                double si = ClusterEval.getSilhouetteCoefficient(datum, table.getData(), tables.stream().map(InteractiveTableView::getData).collect(Collectors.toList()));
                datum.setSilhouette(DF.format(si));
            });
            table.getColumns().get(table.getColumns().size()-1).setVisible(false);
            table.getColumns().get(table.getColumns().size()-1).setVisible(true);
        });

        return root;
    }
}
