package app.tuxguitar.io.tef3.base;

public class TEComponentChord extends TEComponentBase {
    private int chordIndex;
    private int yPosition;
    private int xPosition;
    private TEAnchorPosition anchorPosition;

    public TEComponentChord(TEPosition position, int chordIndex, int yPosition, int xPosition, TEAnchorPosition anchorPosition) {
        super(position);

        this.chordIndex = chordIndex;
        this.yPosition = yPosition;
        this.xPosition = xPosition;
        this.anchorPosition = anchorPosition;
    }

    public int getChordIndex() {
        return this.chordIndex;
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
        return 10;
    }
}
