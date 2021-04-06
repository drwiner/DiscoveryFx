package com.discoveryfx.com.kasisto.cluster;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "code","state", "message", "created_by", "created_date", "from_date", "to_date", "parameters", "overall", "clusters", "unclustered_text" })
public class ClusterServiceOutput {

    @JsonProperty("id")
    private String id;

    @JsonProperty("code")
    private int code = 200;

    @JsonProperty("state")
    private String state= "GENERATED";

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("message")
    private String message = "The clustering job completed successfully";

    @JsonProperty("created_date")
    private String createdDate;

    @JsonProperty("from_date")
    private String fromDate;

    @JsonProperty("to_date")
    private String toDate;

    @JsonProperty("parameters")
    private ClusterParameters parameters;

    @JsonProperty("gold_metrics")
    private Map<String, String> goldMetrics;

    @JsonProperty("clusters")
    private List<ClusterResult> clusters;

    @JsonProperty("unclustered_text")
    private List<ClusterUnclusteredByClosestIntent> unclusteredText;

    public void setGoldMetrics(Map<String, String> goldMetrics) {
        this.goldMetrics = goldMetrics;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({"min_request_text_confidence", "min_cluster_size", "intents_from_data_included", "intents_from_data_excluded"})
    static class ClusterParameters{
        @JsonProperty("min_request_text_confidence")
        public double minRequestTextConfidence;

        @JsonProperty("max_predicted_score")
        public double maxPredictedScore = 1.0;

        @JsonProperty("max_clusters")
        public Double maxClusters = null;

        @JsonProperty("min_cluster_size")
        public int minClusterSize;

        @JsonProperty("intents_from_data_included")
        public List<String> intentsFromDataIncluded;

        @JsonProperty("intents_from_data_excluded")
        public List<String> intentsFromDataExcluded;

        public ClusterParameters(double minRequestTextConfidence, int minClusterSize, List<String> intentsToInclude, List<String> intentsToExcluded) {
            this.minRequestTextConfidence = minRequestTextConfidence;
            this.minClusterSize = minClusterSize;
            this.intentsFromDataIncluded = intentsToInclude;
            this.intentsFromDataExcluded = intentsToExcluded;
        }

        public ClusterParameters() {
        }

        public double getMinRequestTextConfidence() {
            return minRequestTextConfidence;
        }

        public int getMinClusterSize() {
            return minClusterSize;
        }

        public List<String> getIntentsFromDataIncluded() {
            return intentsFromDataIncluded;
        }

        public List<String> getIntentsFromDataExcluded() {
            return intentsFromDataExcluded;
        }

        public void setMinRequestTextConfidence(double minRequestTextConfidence) {
            this.minRequestTextConfidence = minRequestTextConfidence;
        }

        public double getMaxPredictedScore() {
            return maxPredictedScore;
        }

        public void setMaxPredictedScore(double maxPredictedScore) {
            this.maxPredictedScore = maxPredictedScore;
        }

        public Double getMaxClusters() {
            return maxClusters;
        }

        public void setMaxClusters(Double maxClusters) {
            this.maxClusters = maxClusters;
        }

        public void setMinClusterSize(int minClusterSize) {
            this.minClusterSize = minClusterSize;
        }

        public void setIntentsFromDataIncluded(List<String> intentsFromDataIncluded) {
            this.intentsFromDataIncluded = intentsFromDataIncluded;
        }

        public void setIntentsFromDataExcluded(List<String> intentsFromDataExcluded) {
            this.intentsFromDataExcluded = intentsFromDataExcluded;
        }
    }

    @JsonProperty("overall")
    private OverallCounts overall;

    @JsonPropertyOrder({"total_clusters_created", "total_requests", "total_request_texts", "total_request_text_clustered", "total_request_text_not_clustered"})
    private static class OverallCounts {
        @JsonProperty("total_clusters_created")
        private int totalClustersCreated;

        @JsonProperty("total_requests")
        private int totalRequestTexts;

        @JsonProperty("total_request_texts")
        private int totalDeduplicatedRequestTexts;

        @JsonProperty("total_request_text_clustered")
        private int totalRequestsInClusters;

        @JsonProperty("total_request_text_not_clustered")
        private int totalUnassignedRequests;

        public OverallCounts(int totalClustersCreated, int totalRequestTexts, int totalDeduplicatedRequestTexts, int totalRequestsInClusters, int totalUnassignedRequests) {
            this.totalClustersCreated = totalClustersCreated;
            this.totalRequestTexts = totalRequestTexts;
            this.totalDeduplicatedRequestTexts = totalDeduplicatedRequestTexts;
            this.totalRequestsInClusters = totalRequestsInClusters;
            this.totalUnassignedRequests = totalUnassignedRequests;
        }

        public OverallCounts() {
        }

        public int getTotalClustersCreated() {
            return totalClustersCreated;
        }

        public void setTotalClustersCreated(int totalClustersCreated) {
            this.totalClustersCreated = totalClustersCreated;
        }

        public int getTotalRequestTexts() {
            return totalRequestTexts;
        }

        public void setTotalRequestTexts(int totalRequestTexts) {
            this.totalRequestTexts = totalRequestTexts;
        }

        public int getTotalDeduplicatedRequestTexts() {
            return totalDeduplicatedRequestTexts;
        }

        public void setTotalDeduplicatedRequestTexts(int totalDeduplicatedRequestTexts) {
            this.totalDeduplicatedRequestTexts = totalDeduplicatedRequestTexts;
        }

        public int getTotalRequestsInClusters() {
            return totalRequestsInClusters;
        }

        public void setTotalRequestsInClusters(int totalRequestsInClusters) {
            this.totalRequestsInClusters = totalRequestsInClusters;
        }

        public int getTotalUnassignedRequests() {
            return totalUnassignedRequests;
        }

        public void setTotalUnassignedRequests(int totalUnassignedRequests) {
            this.totalUnassignedRequests = totalUnassignedRequests;
        }
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public void setParameters(ClusterParameters parameters) {
        this.parameters = parameters;
    }

    public void setOverall(OverallCounts overall) {
        this.overall = overall;
    }

    public void setClusters(List<ClusterResult> clusters) {
        this.clusters = clusters;
    }

    public void setUnclusteredText(List<ClusterUnclusteredByClosestIntent> unclusteredText) {
        this.unclusteredText = unclusteredText;
    }

    public String getId() {
        return id;
    }

    public int getCode() {
        return code;
    }

    public String getState() {
        return state;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public ClusterParameters getParameters() {
        return parameters;
    }

    public Map<String, String> getGoldMetrics() {
        return goldMetrics;
    }

    public OverallCounts getOverall() {
        return overall;
    }

    public List<ClusterResult> getClusters() {
        return clusters;
    }

    public List<ClusterUnclusteredByClosestIntent> getUnclusteredText() {
        return unclusteredText;
    }

}
