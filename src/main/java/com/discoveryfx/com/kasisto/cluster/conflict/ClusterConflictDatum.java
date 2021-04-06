package com.discoveryfx.com.kasisto.cluster.conflict;

import com.discoveryfx.com.kasisto.cluster.ClusterClosestIntent;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.discoveryfx.com.kasisto.cluster.Cluster.DF;

public class ClusterConflictDatum {

    @JsonProperty("utterance")
    private String utterance;

    @JsonProperty("closest_intent")
    private ClusterClosestIntent clusterClosestIntent;

    @JsonProperty("closest_intent_seed")
    private String closestIntentSeed;

    @JsonProperty("sim_to_closest_intent")
    private String simToClosestIntent;

    @JsonProperty("sim_to_own_centroid")
    private String simToOwnCentroid;

    public ClusterConflictDatum() {
    }

    public ClusterConflictDatum(String utterance, ClusterClosestIntent clusterClosestIntent, String closestIntentSeed, Double simToClosestIntent, Double simToOwnCentroid) {
        this.utterance = utterance;
        this.clusterClosestIntent = clusterClosestIntent;
        this.closestIntentSeed = closestIntentSeed;
        this.simToClosestIntent = DF.format(simToClosestIntent);
        this.simToOwnCentroid = DF.format(simToOwnCentroid);
    }

    public ClusterClosestIntent getClusterClosestIntent() {
        return clusterClosestIntent;
    }

    public void setClusterClosestIntent(ClusterClosestIntent clusterClosestIntent) {
        this.clusterClosestIntent = clusterClosestIntent;
    }

    public String getUtterance() {
        return utterance;
    }

    public void setUtterance(String utterance) {
        this.utterance = utterance;
    }

    public String getClosestIntentSeed() {
        return closestIntentSeed;
    }

    public void setClosestIntentSeed(String closestIntentSeed) {
        this.closestIntentSeed = closestIntentSeed;
    }

    public String getSimToClosestIntent() {
        return simToClosestIntent;
    }

    public void setSimToClosestIntent(String simToClosestIntent) {
        this.simToClosestIntent = simToClosestIntent;
    }

    public String getSimToOwnCentroid() {
        return simToOwnCentroid;
    }

    public void setSimToOwnCentroid(String simToOwnCentroid) {
        this.simToOwnCentroid = simToOwnCentroid;
    }
}
