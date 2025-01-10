package org.herac.tuxguitar.io.tef3.base;

public class TEComponentCrescendo extends TEComponentBase {
    private boolean isDecrescendo;
    private int duration;
    private int yPosition;
    private TEAnchorPosition anchorPosition;

    public TEComponentCrescendo(TEPosition position, boolean isDecrescendo, int duration, int yPosition, TEAnchorPosition anchorPosition) {
        super(position);

        this.isDecrescendo = isDecrescendo;
        this.duration = duration;
        this.yPosition = yPosition;
        this.anchorPosition = anchorPosition;
    }

    public boolean getIsDecrescendo() {
        return this.isDecrescendo;
    }

    // In 16th notes
    public int getDuration() {
        return this.duration;
    }

    public int getYposition() {
        return this.yPosition;
    }

    public TEAnchorPosition getAnchorPosition() {
        return this.anchorPosition;
    }

    public int getSortOrder() {
        return 150;
    }
}
