package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterDatum;
import com.discoveryfx.com.kasisto.cluster.ClusterResult;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.discoveryfx.com.kasisto.cluster.Cluster.DF;

public class TableActions {

    private TableActions(){
    }

    public static void merge(List<Integer> tableIndices, ClusterTreeReport treeReport){
        /*
         * Put all the content in the i>0'th table into the 0th table
         */

        float[] sum = new float[768];

        Map<String, float[]> procUttToEmb = Client.getProcUttToEmb();
        TreeItem<ClusterTreeValue> originalTable = treeReport.getTableAtIndex(tableIndices.get(0));

        int actualVectorsCount = 0;
        for (ClusterDatum datum: originalTable.getValue().getTable().getData()){
            if (! procUttToEmb.containsKey(datum.getDatum()))
                continue;
            actualVectorsCount += 1;
            float[] vector = procUttToEmb.get(datum.getDatum());
            for (int i = 0; i < vector.length; i++) {
                sum[i] += vector[i];
            }
        }
        int n = originalTable.getValue().getTable().getData().size();

        /*
         * Also need to update all the confidence values. Create a new centroid.
         */


        List<ClusterDatum> newMembers = new ArrayList<>();
        for (int i=1; i< tableIndices.size(); i++){
            TreeItem<ClusterTreeValue> tableAtIndex = treeReport.getTableAtIndex(tableIndices.get(i));
            for (ClusterDatum datum: tableAtIndex.getValue().getTable().getData()){
                newMembers.add(datum);
                if (! procUttToEmb.containsKey(datum.getDatum()))
                    continue;

                actualVectorsCount += 1;
                float[] vector = procUttToEmb.get(datum.getDatum());
                for (int k = 0; k < vector.length; k++) {
                    sum[k] += vector[k];
                }
            }
        }
        n += newMembers.size();

        float[] centroid = new float[sum.length];
        for (int i=0; i<sum.length; i++){
            centroid[i] = sum[i] / actualVectorsCount;
        }

        for (ClusterDatum datum: originalTable.getValue().getTable().getData()){
            float[] vector = procUttToEmb.getOrDefault(datum.getDatum(), new float[768]);
            double v = ModelIO.cosineSim(vector, centroid);
            datum.setSim((float) v);
            datum.setConfidence(DF.format(v));
        }

        remove(tableIndices.subList(1, tableIndices.size()), treeReport);

        treeReport.tables.stream().map(t -> new ClusterResult(t.getData()));

    }

    public static void split(Integer treeIndex, List<Integer> treeIndices, ClusterTreeReport treeReport){

    }

    public static void remove(List<Integer> tableIndices, ClusterTreeReport treeReport){
        List<TreeItem<ClusterTreeValue>> items = new ArrayList<>();
        for (Integer i: tableIndices){
            TreeItem<ClusterTreeValue> treeItem = treeReport.getTableAtIndex(i);
            if (treeItem != null){
                items.add(treeItem);
            }
        }
        items.forEach(treeReport::removeTable);
    }

    public static void remove(Integer treeIndex, List<Integer> rowIndices, ClusterTreeReport treeReport){

    }

}
