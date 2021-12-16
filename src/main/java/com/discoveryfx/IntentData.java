package com.discoveryfx;

import com.fasterxml.jackson.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntentData implements Comparable<IntentData> {

    public enum Type {
        TRIGGER("TRIGGER"),
        CLARIFY("CLARIFY");

        private String m_description;

        private Type(String description) {
            m_description = description;
        }

        public String getDescription() {
            return m_description;
        }

        @JsonCreator
        public static Type fromString(String value) {
            return value == null || value.isEmpty()? null : Type.valueOf(value.toUpperCase());
        }

        @Override
        @JsonValue
        public String toString() {
            return m_description;
        }
    }

    public enum Scope
    {
        SEED("SEED"),
        TRAIN("TRAIN"),
        TEST("TEST"),
        TEST_GOLD("TEST_GOLD"),
        TEST_PLATINUM("TEST_PLATINUM");

        private String m_description;

        private Scope(String description) {
            m_description = description;
        }

        public String getDescription() {
            return m_description;
        }

        @JsonCreator
        public static Scope fromString(String value) {
            return value == null || value.isEmpty()? null : Scope.valueOf(value.toUpperCase());
        }

        public static List<Scope> fromStrings(List<String> values) {
            return values == null ? null : values.stream().map(v->Scope.valueOf(v.toUpperCase())).collect(Collectors.toList());
        }

        public static boolean isValid(String value)
        {
            try {
                return fromString(value)!=null;
            }catch(Exception e)
            {
                return false;
            }
        }


        @Override
        @JsonValue
        public String toString() {
            return m_description;
        }
    }



    private String id;


    @JsonProperty("trigger_sentence")
    private String triggerSentenceText;

    @JsonProperty("intent_name")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String intentName;

    private Scope scope;

    private String source;

    private Date updatedDate;

    private Type type = Type.TRIGGER;

    @JsonProperty("tags")
    private List<String> tags;


    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String category;

    @JsonIgnore
    private int version;

    private String notes;

    private String categorization;

    public String getCategorization() {
        return categorization;
    }

    public void setCategorization(String categorization) {
        this.categorization = categorization;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @JsonIgnore
    public int getVersion() { return version; }
    @JsonIgnore
    public void setVersion(int version) { this.version = version; }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id=id;
    }

    @JsonProperty("trigger_sentence")
    public String getTriggerSentenceText() {
        return triggerSentenceText;
    }

    @JsonProperty("trigger_sentence")
    public void setTriggerSentenceText(String triggerSentenceText) {
        this.triggerSentenceText = triggerSentenceText;
    }

    @JsonProperty("intent_name")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getIntentName() {
        return intentName;
    }

    @JsonProperty("intent_name")
    public void setIntentName(String intentName) {
        this.intentName = intentName;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    @JsonIgnore
    public Date getUpdatedDate() {
        return updatedDate;
    }

    @JsonIgnore
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean mustHaveIntent(){
        return type == Type.TRIGGER || type == Type.CLARIFY;
    }




    @JsonIgnore
    @Override
    public int compareTo(IntentData intentData) {
        if(intentData==null)
            return -1;

        if(isObjectsEqual(getTriggerSentenceText(), intentData.getTriggerSentenceText()) &&
                isObjectsEqual(getCategory(), intentData.getCategory()) &&
                isObjectsEqual(getIntentName(), intentData.getIntentName()) &&
                isObjectsEqual(getScope(), intentData.getScope()) &&
                isObjectsEqual(getType(), intentData.getType()) &&
                isObjectsEqual(getId(), intentData.getId()))
            return 0;

        return -1;
    }


    private boolean isObjectsEqual(Object obj1, Object obj2) {
        if(obj1==null && obj2==null)
            return true;

        if(obj1==null || obj2==null)
            return false;

        return obj1.equals(obj2);
    }


}
