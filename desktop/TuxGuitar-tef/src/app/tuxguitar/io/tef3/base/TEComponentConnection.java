package org.herac.tuxguitar.io.tef3.base;

public class TEComponentConnection extends TEComponentBase {
    private int duration;
    private boolean isDashed;
    private int amplitude;
    private int number;
    private int yPosition;
    private boolean isBottomUp;
    private boolean isBracket;

    public TEComponentConnection(TEPosition position, int duration, boolean isDashed, int amplitude, int number, int yPosition, boolean isBottomUp, boolean isBracket)
    {
        super(position);

        this.duration = duration;
        this.isDashed = isDashed;
        this.amplitude = amplitude;
        this.number = number;
        this.yPosition = yPosition;
        this.isBottomUp = isBottomUp;
        this.isBracket = isBracket;
    }

    // In 16th notes
    public int getDuration() {
        return this.duration;
    }

    public boolean getIsDashed() {
        return isDashed;
    }

    public int getAmplitude() {
        return this.amplitude;
    }

    public int getNumber() {
        return this.number;
    }

    public int getYposition() {
        return this.yPosition;
    }

    public boolean getIsBottomUp() {
        return this.isBottomUp;
    }

    public boolean getIsBracket() {
        return this.isBracket;
    }

    public int getSortOrder() {
        return 170;
    }
}
