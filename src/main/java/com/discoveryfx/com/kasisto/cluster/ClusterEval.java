package com.discoveryfx.com.kasisto.cluster;

import com.discoveryfx.Client;
import com.discoveryfx.IntentDataDocument;
import com.discoveryfx.ModelIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.discoveryfx.com.kasisto.cluster.Cluster.DF;

public class ClusterEval {

    private static final Logger LOG= LoggerFactory.getLogger(ClusterEval.class);

    public static double getSilhouetteCoefficient(List<ClusterResult> results){
        //https://arxiv.org/pdf/2012.08987.pdf

        if (results.size() ==0)
            return 0d;

        double clusterIndexValidity = 0d;
        double denom = 0;

        for (int i=0; i<results.size(); i++){

            double sumOnCluster = 0;

            for (ClusterDatum datum: results.get(i).getDatums()) {

                double silhouetteCoefficient = getSilhouetteCoefficient(datum, results.get(i), results);

                if (silhouetteCoefficient < -1) {
                    datum.setSilhouette("NA");
                    continue;
                } else {
                    datum.setSilhouette(DF.format(silhouetteCoefficient));
                }

                clusterIndexValidity += silhouetteCoefficient;
                sumOnCluster += silhouetteCoefficient;
                denom += 1;

            }

            LOG.info("Cluster: C" + i + ": " + sumOnCluster / results.get(i).getDatums().size());
        }

        if (denom == 0)
            return 0;

        return clusterIndexValidity / denom;

    }

    private static final float[] ZeroVector = new float[768];

    public static double getSilhouetteCoefficient(ClusterDatum item, IntentDataDocument result, List<IntentDataDocument> others){
        int sizeCi = result.getIntentData().size();
        if (sizeCi <=1){
            return 0;
        }

        Map<String, float[]> procUttToEmb = Client.getProcUttToEmb();

        if (! procUttToEmb.containsKey(item.getDatum()))
            return -10;

        float[] itemVector = procUttToEmb.getOrDefault(item.getDatum(), new float[768]);

        double ai = result.getIntentData().stream()
                .filter(j -> !j.getId().equals(item.getIntentDataId()) && procUttToEmb.containsKey(j.getTriggerSentenceText()))
                .map(j -> ModelIO.cosineDist(itemVector, procUttToEmb.getOrDefault(j.getTriggerSentenceText(), new float[768])))
                .reduce(0d, Double::sum) / (sizeCi - 1);


        if (others.size() <= 1)
            return 1;

        double bi = others.stream().filter(o -> o != result).map(o -> o.getIntentData().stream()
                .filter(j -> procUttToEmb.containsKey(j.getTriggerSentenceText()))
                .map(j -> ModelIO.cosineDist(itemVector, procUttToEmb.getOrDefault(j.getTriggerSentenceText(), new float[768])))
                .reduce(0d, Double::sum) / (o.getIntentData().size()))
                .mapToDouble(d -> d).min().orElse(0d);

        if (ai == bi)
            return 0;

        double si = (bi - ai) / (Math.max(bi, ai));

        return si;
    }

    public static double getSilhouetteCoefficient(ClusterDatum item, ClusterResult result, List<ClusterResult> others){
        //https://en.wikipedia.org/wiki/Silhouette_(clustering)
        int sizeCi = result.getDatums().size();
        if (sizeCi <=1){
            return 0;
        }

        Map<String, float[]> procUttToEmb = Client.getProcUttToEmb();

        if (! procUttToEmb.containsKey(item.getDatum()))
            return -10;

        float[] itemVector = procUttToEmb.getOrDefault(item.getDatum(), new float[768]);

        double ai = result.getDatums().stream()
                .filter(j -> j!= item && procUttToEmb.containsKey(j.getDatum()))
                .map(j -> ModelIO.cosineDist(itemVector, procUttToEmb.getOrDefault(j.getDatum(), new float[768])))
                .reduce(0d, Double::sum) / (sizeCi - 1);


        if (others.size() <= 1)
            return 1;

        double bi = others.stream().filter(o -> o != result).map(o -> o.getDatums().stream()
                .filter(j -> procUttToEmb.containsKey(j.getDatum()))
                .map(j -> ModelIO.cosineDist(itemVector, procUttToEmb.getOrDefault(j.getDatum(), new float[768])))
                .reduce(0d, Double::sum) / (o.getDatums().size()))
                .mapToDouble(d -> d).min().orElse(0d);

        if (ai == bi)
            return 0;

        double si = (bi - ai) / (Math.max(bi, ai));

        return si;
    }
}
