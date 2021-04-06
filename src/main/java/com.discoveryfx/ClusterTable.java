package com.discoveryfx;

import javafx.scene.control.TableView;

public class ClusterTable extends TableView<Cluster> {

    private Cluster cluster;

    public ClusterTable() {
        super();
    }

    public ClusterTable(Cluster cluster) {
        this.cluster = cluster;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }
}
