package app.tuxguitar.io.tef3.base;

public class TEComponentScaleDiagram extends TEComponentBase {
    private int yPosition;
    private int xPosition;
    private TEAnchorPosition anchorPosition;

    public TEComponentScaleDiagram(TEPosition position, int yPosition, int xPosition, TEAnchorPosition anchorPosition)
    {
        super(position);

        this.yPosition = yPosition;
        this.xPosition = xPosition;
        this.anchorPosition = anchorPosition;
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
        return 110;
    }
}
