package org.herac.tuxguitar.io.tef3.base;

public class TEComponentLineBreak extends TEComponentBase {
    private int verticalSpacing;
    private int nextLineIndex;
    private int measure;
    private boolean isTruncateCurrentLine;
    private boolean isPageBreak;
    private boolean isDoubleBar;
    private boolean isTimeSign;

    public TEComponentLineBreak(TEPosition position, int verticalSpacing, int nextLineIndex, int measure, boolean isTruncateCurrentLine,
        boolean isPageBreak, boolean isDoubleBar, boolean isTimeSign)
    {
        super(position);

        this.verticalSpacing = verticalSpacing;
        this.nextLineIndex = nextLineIndex;
        this.measure = measure;
        this.isTruncateCurrentLine = isTruncateCurrentLine;
        this.isPageBreak = isPageBreak;
        this.isDoubleBar = isDoubleBar;
        this.isTimeSign = isTimeSign;
    }

    public int getVerticalSpacing() {
        return this.verticalSpacing;
    }

    public int getNextLineIndex() {
        return this.nextLineIndex;
    }

    public int getMeasure() {
        return this.measure;
    }

    public boolean getIsTruncateCurrentLine() {
        return this.isTruncateCurrentLine;
    }

    public boolean getIsPageBreak() {
        return this.isPageBreak;
    }

    public boolean getIsDoubleBar() {
        return this.isDoubleBar;
    }

    public boolean getIsTimeSign() {
        return this.isTimeSign;
    }

    public int getSortOrder() {
        return 120;
    }
}
