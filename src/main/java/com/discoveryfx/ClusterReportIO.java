package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterServiceOutput;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusterReportIO {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger LOG= LoggerFactory.getLogger(ClusterReportIO.class);

    public static ClusterServiceOutput readClusterReportFile(String fileName){
        File file = new File(fileName);
        if (!file.exists())
            return null;

        try {
            InputStream input = new FileInputStream(fileName);
            return OBJECT_MAPPER.readValue(input, ClusterServiceOutput.class);

        } catch (JsonMappingException e) {
            e.printStackTrace();
            LOG.debug(e.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static List<String> readDataFromInput(String fileName){
        List<String> rawData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();
            while (line != null) {
                String utter = ModelIO.processUtterance(line.trim());
                if (! utter.isEmpty()) {
                    rawData.add(line.trim());
                }

                line = br.readLine();
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return null;
        }

        LOG.info("Found " + rawData.size() + " utterances in input.");
        return rawData;
    }

    public static Map<String, String> readDataFromInputAsMap(String fileName){
        Map<String, String> rawData = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();
            while (line != null) {
                String utter = ModelIO.processUtterance(line.trim());
                if (! utter.isEmpty()) {
                    rawData.put(line.trim(), utter);
                }

                line = br.readLine();
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return null;
        }

        LOG.info("Found " + rawData.size() + " utterances in input.");
        return rawData;
    }

}
