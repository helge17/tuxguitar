package org.herac.tuxguitar.io.tef3.base;

public class TEComponentSymbol extends TEComponentBase {
    private int symbol;
    private int yPosition;
    private int xPosition;
    private TEAnchorPosition anchorPosition;

    public TEComponentSymbol(TEPosition position, int symbol, int yPosition, int xPosition, TEAnchorPosition anchorPosition) {
        super(position);

        this.symbol = symbol;
        this.yPosition = yPosition;
        this.xPosition = xPosition;
        this.anchorPosition = anchorPosition;
    }

    public int getSymbol() {
        return this.symbol;
    }

    public int getYposition() {
        return this.yPosition;
    }

    public int getXposition() {
        return this.xPosition;
    }

    public TEAnchorPosition getAnchorPosition() {
        return this.anchorPosition;
    }

    public int getSortOrder() {
        return 80;
    }
}
