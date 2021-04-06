package com.discoveryfx.com.kasisto.cluster.conflict;

import com.discoveryfx.com.kasisto.cluster.ClusterClosestIntent;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"utterance", "closest_intents"})
public class ClusterIntentUtteranceSummary {

    @JsonProperty("utterance")
    private String utterance;

    @JsonProperty("closest_intents")
    private List<ClusterClosestIntent> closestIntents;


    public ClusterIntentUtteranceSummary(String utterance, List<ClusterClosestIntent> closestIntents) {
        this.utterance = utterance;
        this.closestIntents = closestIntents;
    }

    public String getUtterance() {
        return utterance;
    }

    public void setUtterance(String utterance) {
        this.utterance = utterance;
    }

    public List<ClusterClosestIntent> getClosestIntents() {
        return closestIntents;
    }

    public void setClosestIntents(List<ClusterClosestIntent> closestIntents) {
        this.closestIntents = closestIntents;
    }
}
