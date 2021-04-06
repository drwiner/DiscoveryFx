package com.discoveryfx.com.kasisto.cluster;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"summary", "request_text"})
public class ClusterResult {

    @JsonProperty("request_text")
    private List<ClusterDatum> datums;

    @JsonProperty("summary")
    private ClusterResultSummary summary;


    public ClusterResult() {
        datums = new ArrayList<>();
        summary = new ClusterResultSummary();
    }


    public List<ClusterDatum> getDatums() {
        return datums;
    }

    public void setDatums(List<ClusterDatum> datums) {
        this.datums = datums;
    }

    public ClusterResultSummary getSummary() {
        return summary;
    }

    public void setSummary(ClusterResultSummary summary) {
        this.summary = summary;
    }
}
