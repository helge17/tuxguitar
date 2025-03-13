package app.tuxguitar.io.tef3.base;

public class TEComponentAccent extends TEComponentBase {
    private int volume;
    private int yPosition;
    private int xPosition;
    private TEAnchorPosition anchorPosition;

    public TEComponentAccent(TEPosition position, int volume, int yPosition, int xPosition, TEAnchorPosition anchorPosition)
    {
        super(position);

        this.volume = volume;
        this.yPosition = yPosition;
        this.xPosition = xPosition;
        this.anchorPosition = anchorPosition;
    }

    public int getVolume() {
        return this.volume;
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
        return 160;
    }
}
