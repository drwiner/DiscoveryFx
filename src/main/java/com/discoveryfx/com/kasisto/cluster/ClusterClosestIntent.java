package com.discoveryfx.com.kasisto.cluster;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

import static com.discoveryfx.com.kasisto.cluster.Cluster.DF;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClusterClosestIntent {

    @JsonProperty("intent_display_name")
    private String intentDisplayName;

    @JsonProperty("confidence")
    private String confidence;

    @JsonProperty("representative_sentences")
    private List<String> representativeSentences;

    public ClusterClosestIntent() {
    }

    public ClusterClosestIntent(ClusterDatum datum, String representative) {
        intentDisplayName = datum.getDatum();
        confidence = DF.format((double) datum.getSim());
        representativeSentences = Collections.singletonList(representative);
    }

    public ClusterClosestIntent(String intentDisplayName, String confidence) {
        this.intentDisplayName = intentDisplayName;
        this.confidence = confidence;
    }

    public String getIntentDisplayName() {
        return intentDisplayName;
    }

    public String getConfidence() {
        return confidence;
    }

    public List<String> getRepresentativeSentences() {
        return representativeSentences;
    }
}
