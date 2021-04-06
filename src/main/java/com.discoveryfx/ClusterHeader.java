package com.discoveryfx;

import javafx.scene.layout.GridPane;

public class ClusterHeader extends GridPane {
    private String name;
    private Integer size;
    private String repSentence;

    public ClusterHeader(String name, Integer size, String repSentence) {
        super();
        this.name = name;
        this.size = size;
        this.repSentence = repSentence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getRepSentence() {
        return repSentence;
    }

    public void setRepSentence(String repSentence) {
        this.repSentence = repSentence;
    }
}
