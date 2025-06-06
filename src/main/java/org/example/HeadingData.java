package org.example;

public class HeadingData {
    public final int level;
    public final String text;
    public final String numbering;

    public HeadingData(int level, String text, String numbering) {
        this.level = level;
        this.text = text != null ? text : "";
        this.numbering = numbering != null ? numbering : "";
    }
}
