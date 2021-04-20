package com.discoveryfx.com.kasisto.cluster;

import com.fasterxml.jackson.annotation.*;
import javafx.beans.property.SimpleDoubleProperty;


import java.util.Comparator;

import static com.discoveryfx.com.kasisto.cluster.Cluster.DF;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"text", "confidence"})
public class ClusterDatum{

    @JsonProperty("confidence")
    private String confidence;

    public String getConfidence() {
        return confidence;
    }

    @JsonIgnore
    private String source;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @JsonIgnore
    private String dest;

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    @JsonIgnore
    private float sim;

    @JsonProperty("text")
    private String datum;

    @JsonIgnore
    private int dataID;

    @JsonIgnore
    private float [] vector;

    public float[] getVector() {
        return vector;
    }

    public void setVector(float[] vector) {
        this.vector = vector;
    }

    @JsonProperty("count")
    private int count;

    public int getCount() {
        return count;
    }

    @JsonIgnore
    public SimpleDoubleProperty magicValueProperty = new SimpleDoubleProperty(0d);

    public double getMagicValue() {
        return magicValueProperty.get();
    }

    public void setMagicValue(double magicValue) {
        this.magicValueProperty.setValue(magicValue);
    }

    public ClusterDatum() {
    }

    public ClusterDatum(float sim, String datum, int dataID, int count) {
        this.sim = sim;
        this.datum = datum;
        this.dataID = dataID;
        this.count = count;

        this.confidence = DF.format((double)sim);
    }

    public ClusterDatum(String datum, int dataID, float[] vector, int count) {
        this.datum = datum;
        this.dataID = dataID;
        this.vector = vector;
        this.count = count;
    }

    public float getSim() {
        return sim;
    }

    public void setSim(float sim) {
        this.sim = sim;
        this.confidence = DF.format((double)sim);
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public int getDataID() {
        return dataID;
    }

    public void increment() {
        count += 1;
    }

    public static class SimComparator implements Comparator<ClusterDatum> {
        @Override // Reversed on purpose
        public int compare(ClusterDatum d1, ClusterDatum d2){
            return Float.compare(d2.getSim(), d1.getSim());
        }
    }

    public static class MagicComparator implements Comparator<ClusterDatum> {
        @Override // Reversed on purpose
        public int compare(ClusterDatum d1, ClusterDatum d2){
            return Double.compare(d2.getMagicValue(), d1.getMagicValue());
        }
    }

    @Override
    public String toString() {
        return "ClusterDatum{" +
                "sim=" + sim +
                ", datum='" + datum + '\'' +
                '}';
    }
}