package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterResult;

public class ClusterTreeValue {

    private ClusterHeader header;
    private ClusterTable table;
    private CELLTYPE type;

    public enum CELLTYPE {HEADER, TABLE, CLOSEST_INTENT_HEADER, CLOSEST_INTENT_CONTROL_PANEL}

    ;

    public ClusterTreeValue(String name, Integer size, String repSentence) {
        header = new ClusterHeader(name, size, repSentence);
        this.type = CELLTYPE.HEADER;
    }

    public ClusterTreeValue(ClusterResult cluster) {
        table = new ClusterTable(cluster);
        this.type = CELLTYPE.TABLE;
    }

//    public CLusterTreeValue(

    public CELLTYPE getType() {
        return type;
    }

    public ClusterHeader getHeader() {
        return header;
    }

    public void setHeader(ClusterHeader header) {
        this.header = header;
    }

    public ClusterTable getTable() {
        return table;
    }

    public void setTable(ClusterTable table) {
        this.table = table;
    }

    public void setType(CELLTYPE type) {
        this.type = type;
    }
}
