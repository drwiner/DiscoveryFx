package com.discoveryfx.com.kasisto.cluster.conflict;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"code", "message", "exception", "jod_id", "conflicts_discovered", "closest_intents_report"})
public class ClusterConflictResponse {

    @JsonProperty("code")
    public int code = 200;

    @JsonProperty("message")
    public String message;

    @JsonProperty("exception")
    public String exception;

    @JsonProperty("job_id")
    public String job_id;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @JsonProperty("conflicts_discovered")
    private List<ClusterConflictDatum> conflictsDiscovered;


    @JsonProperty("closest_intents_report")
    private List<ClusterIntentUtteranceSummary> closestIntentsReport;

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public List<ClusterConflictDatum> getConflictsDiscovered() {
        return conflictsDiscovered;
    }

    public void setConflictsDiscovered(List<ClusterConflictDatum> conflictsDiscovered) {
        this.conflictsDiscovered = conflictsDiscovered;
    }

    public List<ClusterIntentUtteranceSummary> getClosestIntentsReport() {
        return closestIntentsReport;
    }

    public void setClosestIntentsReport(List<ClusterIntentUtteranceSummary> closestIntentsReport) {
        this.closestIntentsReport = closestIntentsReport;
    }
}
