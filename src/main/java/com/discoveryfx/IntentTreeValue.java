package com.discoveryfx;

public class IntentTreeValue {

    private IntentHeader header;
    private IntentDataTable table;
    private CELLTYPE type;

    public enum CELLTYPE {HEADER, TABLE}

    public IntentTreeValue(String name, Integer size, String repSentence) {
        header = new IntentHeader(name, size, repSentence);
        this.type = CELLTYPE.HEADER;
    }

    public IntentTreeValue(IntentDataDocument intentDataDocument) {
        table = new IntentDataTable(intentDataDocument);
        this.type = CELLTYPE.TABLE;
    }


    public IntentHeader getHeader() {
        return header;
    }

    public void setHeader(IntentHeader header) {
        this.header = header;
    }

    public IntentDataTable getTable() {
        return table;
    }

    public void setTable(IntentDataTable table) {
        this.table = table;
    }

    public CELLTYPE getType() {
        return type;
    }

    public void setType(CELLTYPE type) {
        this.type = type;
    }
}
