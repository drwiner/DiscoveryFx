package com.discoveryfx;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ModelIO {
    private static final Logger LOG = LoggerFactory.getLogger(ModelIO.class);

    private static Map<String, Integer> wordToIdxMap;
    private static ConcurrentMap<String, String> unknownToKnownWordMap;

    /*
     * Special token keys
     */
    private static final String START_TOKEN = "[CLS]";
    private static final String END_TOKEN = "[SEP]";
    private static final String PAD_TOKEN = "[PAD]";
    private static final String UNK_TOKEN = "[UNK]";
    private static final char SPACE = ' ';
    private static int startTokenIdx;
    private static int endTokenIdx;
    private static int padTokenIdx;
    private static int unkTokenIdx;

    static {
        wordToIdxMap = new LinkedHashMap<>();

        String vocabFile = ApplicationProperties.getProperty(ApplicationProperties.Property.VOCAB_FILE);

        int wordCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(vocabFile))) {
            String word = br.readLine();
            while (word != null) {
                wordToIdxMap.put(word.trim(), wordCount++);
                word = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        startTokenIdx = wordToIdxMap.get(START_TOKEN);
        endTokenIdx = wordToIdxMap.get(END_TOKEN);
        padTokenIdx = wordToIdxMap.get(PAD_TOKEN);
        unkTokenIdx = wordToIdxMap.get(UNK_TOKEN);


        unknownToKnownWordMap = new ConcurrentHashMap<>(51200);
    }

    public static String processUtterance(String utterance) {
        if (utterance == null) return "";

        utterance = utterance.trim().toLowerCase();
        if (utterance.isEmpty()) return utterance;

        return preprocess(utterance, wordToIdxMap, true);
    }

    private static final String NUMBER_TAG = "<number>";
    private static final String ONE_SPACE = " ";
    private static final String EN = "en";
    private static final String FR = "fr";
    private static final String ZH = "zh";

    public static final Map<String, Pattern> TO_SPACE_REGEX_PATTERN_BY_LANG = new HashMap<>();
    public static final Map<String, Pattern> SPACE_REGEX_PATTERN_BY_LANG = new HashMap<>();
    public static final Map<String, Pattern> NUMBER_REGEX_PATTERN_BY_LANG = new HashMap<>();
    private static final Map<String, Integer> MAX_TOKEN_NUM_BY_LANG = new HashMap<>();

    static {
        TO_SPACE_REGEX_PATTERN_BY_LANG.put(EN, Pattern.compile("[^a-z\\d ]+"));
        TO_SPACE_REGEX_PATTERN_BY_LANG.put(FR, Pattern.compile("[^a-z\\dàâäèéêëîïôœùûüÿç ]+"));
        TO_SPACE_REGEX_PATTERN_BY_LANG.put(ZH, Pattern.compile("([^a-z" + "\u2E80-\u33FF" + "\u4E00-\u9FFF" + "\u3400-\u4DBF" + "\\d ]" + "|[（）《》【】「」『』：；“”／，。、——…？！～⋯°　])+"));

        SPACE_REGEX_PATTERN_BY_LANG.put(EN, Pattern.compile(" +"));
        SPACE_REGEX_PATTERN_BY_LANG.put(FR, Pattern.compile(" +"));
        SPACE_REGEX_PATTERN_BY_LANG.put(ZH, Pattern.compile(" +"));

        NUMBER_REGEX_PATTERN_BY_LANG.put(EN, Pattern.compile("\\d+"));
        NUMBER_REGEX_PATTERN_BY_LANG.put(FR, Pattern.compile("\\d+"));
        NUMBER_REGEX_PATTERN_BY_LANG.put(ZH, Pattern.compile("\\d+"));

        MAX_TOKEN_NUM_BY_LANG.put(EN, 28);
        MAX_TOKEN_NUM_BY_LANG.put(FR, 28);
        MAX_TOKEN_NUM_BY_LANG.put(ZH, 38);
    }

    public static String preprocess(String utterance, Map<String, Integer> wordToIdxMap, boolean useSubWord) {


        Matcher utterToSpaceMatcher = TO_SPACE_REGEX_PATTERN_BY_LANG.get(EN).matcher(utterance);
        utterance = utterToSpaceMatcher.replaceAll(ONE_SPACE);
        String[] words = SPACE_REGEX_PATTERN_BY_LANG.get(EN).split(utterance);
        Pattern numberRegexPattern = NUMBER_REGEX_PATTERN_BY_LANG.get(EN);

        StringBuilder procUtterSb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            } else if (numberRegexPattern.matcher(word).matches()) {
                procUtterSb.append(ONE_SPACE).append(NUMBER_TAG);
            } else {
                word = numberRegexPattern.matcher(word).replaceAll(ONE_SPACE).trim();

                boolean hasSplit = false;
                if (wordToIdxMap != null && useSubWord) {
                    if (word.length() > 3 && word.endsWith("s")) {
                        for (int cnt = 1; word.length() - cnt >= cnt && cnt <= 3; cnt++) {
                            String subWord = word.substring(0, word.length() - cnt);
                            String leftWord = word.substring(word.length() - cnt);
                            if (wordToIdxMap.containsKey(subWord) && wordToIdxMap.containsKey(leftWord)) {
                                procUtterSb.append(ONE_SPACE).append(subWord);
                                hasSplit = true;
                                break;
                            }
                        }
                    }
                }

                if (!hasSplit) {
                    if (wordToIdxMap != null && !wordToIdxMap.containsKey(word)) {
                        word = searchForPartialWord(word);
                    }
                    procUtterSb.append(ONE_SPACE).append(word);
                }
            }
        }

        if (procUtterSb.length() > 0) {
            return procUtterSb.substring(1);
        } else {
            return procUtterSb.toString();
        }
    }

    private static final double UNKNOWN_COVERAGE_WORD_PERCENT = 0.75;

    private static String searchForPartialWord(String word) {
        if (unknownToKnownWordMap.containsKey(word)) {
            return unknownToKnownWordMap.get(word);
        }
        double fullestPercent = 0.0;
        String fullestWord = null;
        for (String potentialWord : wordToIdxMap.keySet()) {
            if (word.length() <= potentialWord.length() || !word.contains(potentialWord)) continue;

            double coveragePercent = 1.0 * potentialWord.length() / word.length();
            if (coveragePercent > fullestPercent) {
                fullestPercent = coveragePercent;
                fullestWord = potentialWord;
            }
        }

        if (fullestWord != null && fullestPercent >= UNKNOWN_COVERAGE_WORD_PERCENT) {
            unknownToKnownWordMap.put(word, fullestWord);
            return fullestWord;
        } else {
            return word;
        }
    }

    public static List<float[]> loadFromBinary(String source) {
        List<float[]> embeddings = new ArrayList<>();
        try (InputStream in = new FileInputStream(source)) {

            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(in);
            while (unpacker.hasNext()) {
                byte[] bytes = new byte[unpacker.unpackBinaryHeader()];
                unpacker.readPayload(bytes);
                float[] floats = byteArrayToFloatArray(bytes);
                embeddings.add(floats);
            }

            unpacker.close();
            return embeddings;

        } catch (IOException e) {
            LOG.error("Could not read file at " + source, e);
            return null;
        }
    }


    public static boolean saveToBinary(List<float[]> embeddings, String destination) {
        try (OutputStream out = new FileOutputStream(destination)) {
            MessagePacker packer = MessagePack.newDefaultPacker(out);
            for (float[] obj : embeddings) {
                packer.packBinaryHeader(obj.length * 4);
                packer.writePayload(floatArray2ByteArray(obj));
            }
            packer.close();
        } catch (IOException e) {
            LOG.error("Could not save embeddings file", e);
            return false;
        } catch (NullPointerException e){
            LOG.error("Some embedding is null.", e);
            return false;
        }
        return true;
    }


    public static List<Integer> loadIndicesFromBinary(String source){
        List<Integer> indices = new ArrayList<>();
        try (InputStream in = new FileInputStream(source)) {

            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(in);
            while (unpacker.hasNext())
                indices.add(unpacker.unpackInt());
            unpacker.close();
            return indices;

        } catch (IOException e) {
            LOG.error("Could not read file at " + source, e);
            return null;
        }
    }


    public static void saveTargetsToBinary(List<String> targets, String destination){
        try (OutputStream out = new FileOutputStream(destination)) {
            MessagePacker packer = MessagePack.newDefaultPacker(out);
            for (String target : targets)
                packer.packString(target);
            packer.close();
        } catch (IOException e) {
            LOG.error("Could not save targets to binary", e);
        }
    }


    public static List<String> loadTargetsFromBinary(String source){
        List<String> targets = new ArrayList<>();
        try (InputStream in = new FileInputStream(source)) {
            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(in);
            while (unpacker.hasNext())
                targets.add(unpacker.unpackString());
            unpacker.close();
            return targets;

        } catch (IOException e) {
            LOG.error("Could not read file at " + source, e);
            return null;
        }
    }


    public static void saveIndicesToBinary(List<Integer> indices, String destination){
        try (OutputStream out = new FileOutputStream(destination)) {
            MessagePacker packer = MessagePack.newDefaultPacker(out);
            for (int index : indices)
                packer.packInt(index);
            packer.close();
        } catch (IOException e) {
            LOG.error("Could not save indices file", e);
        }
    }


    /*
     * Helpers for storing float arrays
     */
    private static byte[] floatArray2ByteArray(float[] values){
        byte[] balue = new byte[values.length * 4];

        for (int i =0; i < values.length; i++)
            ByteBuffer.wrap(balue, i*4, 4).putFloat(values[i]);

        return balue;
    }


    private static float[] byteArrayToFloatArray(byte[] bytes) {
        float[] floats = new float[bytes.length/4];

        for (int i=0; i < bytes.length; i+=4)
            floats[i/4] = ByteBuffer.wrap(bytes, i, 4).getFloat();

        return floats;
    }

    // This used for apache clustering methods.
    public static class CosineSim implements DistanceMeasure {
        @Override
        public double compute(double[] doubles, double[] doubles1) throws DimensionMismatchException {
            return cosineSim(doubles, doubles1);
        }

        public static double cosineSim(double[] we1, double[] we2) {
            if (we1.length != we2.length) {
                LOG.info("Your word embedding lengths were unequal, returning 0");
                return 0;
            }

            double numerator = dotProduct(we1, we2);

            double denom  = norm(we1) * norm(we2);

            if (denom == 0)
                return 0;

            return numerator / denom;

        }

        private static float dotProduct(double[] we1, double[] we2) {
            float result = 0;
            for (int i=0; i < we1.length; i++){
                result += we1[i] * we2[i];
            }
            return result;
        }

        private static double norm(double[] we){
            float result = 0;
            for (double v : we) {
                result += Math.pow(v, 2);
            }
            return Math.sqrt(result);
        }
    }


    public static double cosineSim(float[] we1, float[] we2) {
        if (we1.length != we2.length) {
            LOG.info("Your word embedding lengths were unequal, returning 0");
            return 0;
        }

        double numerator = dotProduct(we1, we2);

        double denom  = norm(we1) * norm(we2);

        if (denom == 0)
            return 0;

        return numerator / denom;
    }

    public static double cosineDist(float[] we1, float[] we2) {
        return 1 - cosineSim(we1, we2);
    }

    private static float dotProduct(float[] we1, float[] we2) {
        float result = 0;
        for (int i=0; i < we1.length; i++){
            result += we1[i] * we2[i];
        }
        return result;
    }

    private static double norm(float[] we){
        float result = 0;
        for (float v : we) {
            result += Math.pow(v, 2);
        }
        return Math.sqrt(result);
    }

    public static class DataPackageModel {

        Map<String, String> intentDisplaySentences;
        Map<Integer, float[]> intentCentroids;
        List<float[]> packageEmbeddings;
        List<String> packageUtterances;
        Map<String, Integer> packageUtteranceToIndex;
        Map<Integer, Integer> packageIndexToLabel;

        Map<String, Integer> labelToInt;
        Map<Integer, String> intToLabel;

        public Map<String, String> getIntentDisplaySentences() {
            return intentDisplaySentences;
        }

        public Map<Integer, float[]> getIntentCentroids() {
            return intentCentroids;
        }

        public List<float[]> getPackageEmbeddings() {
            return packageEmbeddings;
        }

        public List<String> getPackageUtterances() {
            return packageUtterances;
        }

        public Map<String, Integer> getLabelToInt() {
            return labelToInt;
        }

        public Map<Integer, String> getIntToLabel() {
            return intToLabel;
        }

        public Map<String, Integer> getPackageUtteranceToIndex() {
            return packageUtteranceToIndex;
        }

        public Map<Integer, Integer> getPackageIndexToLabel() {
            return packageIndexToLabel;
        }

        public DataPackageModel(){

            String baseProperty = ApplicationProperties.getProperty(ApplicationProperties.Property.APP_EMBEDDING_DIR);
//            String addFt = "_ft";
//            String addFt = "";

            String packageModel = baseProperty + "package.model";
            String packageIndex = baseProperty + "indices.model";
            String packageUtt = baseProperty + "package_utterances.model";
            String packageRep = baseProperty + "representatives.model";
            String targetModel = baseProperty + "targets.model";

            loadFullPackageModelFromBinary(packageModel, packageIndex, packageUtt, packageRep, targetModel);


            String centerModel = baseProperty + "centers.model";
            String centerIndex = baseProperty + "center_indices.model";

            loadCentroidsFromBinary(centerModel, centerIndex);

        }

        private boolean loadFullPackageModelFromBinary(String packageModel, String packageIndex, String packageUtterance, String packageRep, String targetModel) {
            LOG.info("Loading full package model from binary");

            List<Integer> indices = loadIndicesFromBinary(packageIndex);
            List<String> reps = loadTargetsFromBinary(packageRep);
            intentDisplaySentences = new HashMap<>();
            if (intToLabel == null || labelToInt == null){
                intToLabel = new HashMap<>();
                labelToInt = new HashMap<>();
                List<String> targets = loadTargetsFromBinary(targetModel);

                if (targets != null)
                    IntStream.range(0, targets.size()).forEach(i -> {
                        intToLabel.put(i, targets.get(i));
                        labelToInt.put(targets.get(i), i);
                        if (reps != null)
                            intentDisplaySentences.put(targets.get(i), reps.get(i));
                    });
            }

            packageEmbeddings = loadFromBinary(packageModel);
            packageUtterances = loadTargetsFromBinary(packageUtterance);
//            packagePartition = indices.stream().distinct().map(i -> new HashSet<Integer>()).collect(Collectors.toList());

            packageUtteranceToIndex = new HashMap<>();
            packageIndexToLabel = new HashMap<>();
            if (indices != null)
                for (int i=0; i< indices.size(); i++){
                    if (packageUtterances != null)
                        packageUtteranceToIndex.put(packageUtterances.get(i), i);

                    packageIndexToLabel.put(i, indices.get(i));
                }

            return true;
        }

        private boolean loadCentroidsFromBinary(String centerModel, String centerIndices){
            LOG.info("Loading centroids from binary: " + centerModel);

            intentCentroids = new HashMap<>();
            List<float[]> centers = loadFromBinary(centerModel);
            List<Integer> indices = loadIndicesFromBinary(centerIndices);

            IntStream.range(0, centers.size()).forEach(i -> intentCentroids.put(indices.get(i), centers.get(i).clone()));
            LOG.info("Successfully loaded centroids from binary");
            return true;
        }
    }




}
