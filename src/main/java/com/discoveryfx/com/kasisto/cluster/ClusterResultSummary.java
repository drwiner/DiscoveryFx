package com.discoveryfx.com.kasisto.cluster;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"total_requests", "total_request_text", "representative_sentence", "closest_intents"})
class ClusterResultSummary {

    @JsonProperty("total_request_text")
    private int clusterSize;

    @JsonProperty("closest_intents")
    private List<ClusterClosestIntent> closestIntents;

    @JsonProperty("total_requests")
    private int totalRequestText;

    @JsonProperty("representative_sentence")
    private String representativeSentence = null;

    ClusterResultSummary() {
    }


    void setClusterSize(int clusterSize) {
        this.clusterSize = clusterSize;
    }

    void setClosestIntents(List<ClusterClosestIntent> closestIntents) {

        this.closestIntents = closestIntents;
    }

    void setTotalRequestText(int totalRequestText) {
        this.totalRequestText = totalRequestText;
    }

    void setRepresentativeSentence(String representativeSentence) {
        this.representativeSentence = representativeSentence;
    }

    public int getClusterSize() {
        return clusterSize;
    }

    public List<ClusterClosestIntent> getClosestIntents() {
        return closestIntents;
    }

    public int getTotalRequestText() {
        return totalRequestText;
    }

    public String getRepresentativeSentence() {
        return representativeSentence;
    }
}

