package com.discoveryfx.com.kasisto.cluster;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Cluster {

    public static DecimalFormat DF = new DecimalFormat("#.####");
    static {
        DF.setRoundingMode(RoundingMode.HALF_EVEN);
    }

    float[] center;
    private int count;
    private float [] summation;
    int age;
    private float thresh;

    public Cluster(float[] vector, float initialThresh) {
        count = 1;
        center = vector.clone();
        age = 0;
        summation = vector.clone();
        thresh = initialThresh;
    }

    public void assign(float[] vec, boolean adaptThresh){
        count += 1;
        for(int i=0; i < vec.length; i++) {
            summation[i] += vec[i];
            center[i] = summation[i] / count;
        }

        if (adaptThresh)
            thresh += ((1- thresh) / (count * (Math.log(count))));
    }

    public float getThresh() {
        return thresh;
    }



    public float[] getCenter() {
        if (center == null)
            return null;
        return center.clone();
    }

    public int getCount() {
        return count;
    }

    public float[] getSummation() {
        return summation;
    }

    public int getAge() {
        return age;
    }

    public void passEon(){
        age += 1;
    }

    public void resetAge(){
        age = 0;
    }

    @Override
    public String toString() {
        return "Cluster{size=" + count + ", age=" + age + '}';
    }

}
