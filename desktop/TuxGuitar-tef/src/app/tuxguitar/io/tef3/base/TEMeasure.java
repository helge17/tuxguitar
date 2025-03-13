package org.herac.tuxguitar.io.tef3.base;

public class TEMeasure {
    private boolean doNotPrintMetric;
    private boolean freeBarOne;
    private boolean freeBarTwo;
    private boolean pickUpMeasure;
    private boolean adlibMeasure;
    private boolean minorKey;
    private boolean dottedBarLine;
    private boolean halfBarLine;
    private int keySignature;
    private TETimeSignature timeSignature;
    private int leftWidthPadding;

    public boolean getDoNotPrintMetric() {
        return this.doNotPrintMetric;
    }

    public void setDoNotPrintMetric(boolean doNotPrintMetric) {
        this.doNotPrintMetric = doNotPrintMetric;
    }

    public boolean getFreeBarOne() {
        return this.freeBarOne;
    }

    public void setFreeBarOne(boolean freeBarOne) {
        this.freeBarOne = freeBarOne;
    }

    public boolean getFreeBarTwo() {
        return this.freeBarTwo;
    }

    public void setFreeBarTwo(boolean freeBarTwo) {
        this.freeBarTwo = freeBarTwo;
    }

    public boolean getPickupMeasure() {
        return this.pickUpMeasure;
    }

    public void setPickupMeasure(boolean pickUpMeasure) {
        this.pickUpMeasure = pickUpMeasure;
    }

    public boolean getAdlibMeasure() {
        return this.adlibMeasure;
    }

    public void setAdlibMeasure(boolean adlibMeasure) {
        this.adlibMeasure = adlibMeasure;
    }

    public boolean getMinorKey() {
        return this.minorKey;
    }

    public void setMinorKey(boolean minorKey) {
        this.minorKey = minorKey;
    }

    public boolean getDottedBarLine() {
        return this.dottedBarLine;
    }

    public void setDottedBarLine(boolean dottedBarLine) {
        this.dottedBarLine = dottedBarLine;
    }

    public boolean getHalfBarLine() {
        return this.halfBarLine;
    }

    public void setHalfBarLine(boolean halfBarLine) {
        this.halfBarLine = halfBarLine;
    }

    public int getKeySignature() {
        return this.keySignature;
    }

    public void setKeySignature(int keySignature) {
        this.keySignature = keySignature;
    }

    public TETimeSignature getTimeSignature() {
        return this.timeSignature;
    }

    public void setTimeSignature(TETimeSignature timeSignature) {
        this.timeSignature = timeSignature;
    }

    public int getLeftWidthPadding() {
        return this.leftWidthPadding;
    }

    public void setLeftWidthPadding(int leftWidthPadding) {
        this.leftWidthPadding = leftWidthPadding;
    }
}
