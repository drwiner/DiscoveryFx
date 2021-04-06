package com.discoveryfx.com.kasisto.cluster;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"closest_intent", "representative_sentence_from_intent", "unclustered_request_text"})
public class ClusterUnclusteredByClosestIntent {

    @JsonProperty("closest_intent")
    private String intentDisplayName;

    @JsonProperty("representative_sentence_from_intent")
    private String representativeSentence;

    @JsonProperty("unclustered_request_text")
    private List<ClusterDatum> unclusteredRequestTexts;

    public ClusterUnclusteredByClosestIntent() {
    }

    public ClusterUnclusteredByClosestIntent(String intentName, String representative) {
        this.intentDisplayName = intentName;
        this.representativeSentence = representative;
        unclusteredRequestTexts = new ArrayList<>();
    }

    public List<ClusterDatum> getUnclusteredRequestTexts() {
        return unclusteredRequestTexts;
    }

    public void setUnclusteredRequestTexts(List<ClusterDatum> unclusteredRequestTexts) {
        this.unclusteredRequestTexts = unclusteredRequestTexts;
    }

    public static class SizeComparator implements Comparator<ClusterUnclusteredByClosestIntent> {
        @Override // Reversed on purpose
        public int compare(ClusterUnclusteredByClosestIntent d1, ClusterUnclusteredByClosestIntent d2){
            return Float.compare(d2.getUnclusteredRequestTexts().size(), d1.getUnclusteredRequestTexts().size());
        }
    }

    public String getIntentDisplayName() {
        return intentDisplayName;
    }

    public void setIntentDisplayName(String intentDisplayName) {
        this.intentDisplayName = intentDisplayName;
    }

    public String getRepresentativeSentence() {
        return representativeSentence;
    }

    public void setRepresentativeSentence(String representativeSentence) {
        this.representativeSentence = representativeSentence;
    }


}
