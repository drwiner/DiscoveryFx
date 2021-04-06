package com.discoveryfx.com.kasisto.cluster;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"text", "count"})
public class ClusterUtterance {
    @JsonProperty("text")
    private String utterance;

    @JsonProperty("count")
    private int count;

    public ClusterUtterance(String utterance) {
        this.utterance = utterance;
        count = 1;
    }

    public String getUtterance() {
        return utterance;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void increment(){
        count += 1;
    }

    @Override
    public int hashCode() {
        return utterance.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof ClusterUtterance))
            return false;
        return ((ClusterUtterance) obj).getUtterance().equalsIgnoreCase(utterance);
    }

    public void setUtterance(String utterance) {
        this.utterance = utterance;
    }
}
