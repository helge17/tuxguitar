package app.tuxguitar.io.tef3.base;

public class TEComponentSpacingMarker extends TEComponentBase {
    private int xPosition;

    public TEComponentSpacingMarker(TEPosition position, int xPosition) {
        super(position);

        this.xPosition = xPosition;
    }

    public int getXposition() {
        return this.xPosition;
    }

    public int getSortOrder() {
        return 100;
    }
}
