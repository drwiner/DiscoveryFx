package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterDatum;
import javafx.scene.input.DataFormat;

import java.util.ArrayList;
import java.util.List;

public class DatumInteractionManager {
    /*
     * Table Events Row Factory
     */

    public static DatumInteractionManager INSTANCE = new DatumInteractionManager();

    public static DatumInteractionManager getINSTANCE() {
        return INSTANCE;
    }

    private DatumInteractionManager() {
    }

    private ArrayList<ClusterDatum> selections = new ArrayList<>();

    public enum TableViewEnum {ALL, CLUSTER, INTENT, MAGIC, AUDIENCE}

    public static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");


    public void clear(){
        selections.clear();
    }

    public void select(List<ClusterDatum> selection){
        selections.addAll(selection);
    }

    public boolean isClear(){
        return selections.isEmpty();
    }

    public List<ClusterDatum> getSelected(){
        return new ArrayList<>(selections);
    }


}
