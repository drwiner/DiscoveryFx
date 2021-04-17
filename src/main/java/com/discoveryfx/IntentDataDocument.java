package com.discoveryfx;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntentDataDocument {
    @JsonProperty("name")
    private String name;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("intent_name")
    private String intentName;

    @JsonProperty("scope")
    private IntentData.Scope scope;

    @JsonProperty("data")
    private List<IntentData> intentData;

    @JsonProperty("state")
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIntentName() {
        return intentName;
    }

    public void setIntentName(String intentName) {
        this.intentName = intentName;
    }

    public IntentData.Scope getScope() {
        return scope;
    }

    public void setScope(IntentData.Scope scope) {
        this.scope = scope;
    }

    public List<IntentData> getIntentData() {
        return intentData;
    }

    public void setIntentData(List<IntentData> intentData) {
        this.intentData = intentData;
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static IntentDataDocument createDocument(File file){
        try {
            return MAPPER.readValue(file, IntentDataDocument.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<IntentDataDocument> getAllIntents(){
        String intentDataPath = ApplicationProperties.getProperty(ApplicationProperties.Property.DATA_PACKAGE);
        File headFile = new File(intentDataPath);
        List<IntentDataDocument> intents = new ArrayList<>();
        if (headFile.exists()){

            List<Path> filePaths;

            try {
                filePaths = Files.list(Paths.get(intentDataPath)).collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }

            for (Path path: filePaths){
                if (Files.isDirectory(path))
                    continue;

                IntentDataDocument document = createDocument(path.toFile());
                intents.add(document);
            }
        }

        return intents;
    }
}
