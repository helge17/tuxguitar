package app.tuxguitar.io.tef3.base;

public class TEComponentStemLength extends TEComponentBase {
    private int yPosition;

    public TEComponentStemLength(TEPosition position, int yPosition) {
        super(position);

        this.yPosition = yPosition;
    }

    public int getYposition() {
        return this.yPosition;
    }

    public int getSortOrder() {
        return 90;
    }
}
