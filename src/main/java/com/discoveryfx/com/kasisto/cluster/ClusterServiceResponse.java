package com.discoveryfx.com.kasisto.cluster;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClusterServiceResponse  {

    public String exception;
    public int code;
    public String message;
    public String job_id;
    public String state;

    private Integer numClustersCreated = null;
    private Double ari = null;
    private Double majorityPrecision = null;

    public ClusterServiceResponse() {
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getNumClustersCreated() {
        return numClustersCreated;
    }

    public void setNumClustersCreated(Integer numClustersCreated) {
        this.numClustersCreated = numClustersCreated;
    }

    public Double getAri() {
        return ari;
    }

    public void setAri(Double ari) {
        this.ari = ari;
    }

    public Double getMajorityPrecision() {
        return majorityPrecision;
    }

    public void setMajorityPrecision(Double majorityPrecision) {
        this.majorityPrecision = majorityPrecision;
    }
}
