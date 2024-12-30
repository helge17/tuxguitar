package org.herac.tuxguitar.io.tef3.base;

public class TEReadingListEntry {
    private int startMeasure;
    private int endMeasure;
    private String name;

    public TEReadingListEntry(int startMeasure, int endMeasure, String name) {
        this.startMeasure = startMeasure;
        this.endMeasure = endMeasure;
        this.name = name;
    }

    public int getStartMeasure() {
        return this.startMeasure;
    }

    public int getEndMeasure() {
        return this.endMeasure;
    }

    public String getName() {
        return this.name;
    }
}
