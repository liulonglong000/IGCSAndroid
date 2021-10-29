package com.jaygoo.selector;

public class MultiData {
    private String id;
    private String text;
    private boolean bSelect;
    private String extraInfo;

    public MultiData(String id, String text, boolean bSelect) {
        this.id = id;
        this.text = text;
        this.bSelect = bSelect;
    }

    public MultiData(String id, String text, boolean bSelect, String extraInfo) {
        this.id = id;
        this.text = text;
        this.bSelect = bSelect;
        this.extraInfo = extraInfo;
    }

    public String getText() {
        return text;
    }

    public String getId() {
        return id;
    }

    public boolean isbSelect() {
        return bSelect;
    }

    public void setbSelect(boolean bSelect) {
        this.bSelect = bSelect;
    }

    public String getExtraInfo() {
        return extraInfo;
    }
}
