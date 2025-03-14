package app.tuxguitar.io.tef3.base;

public class TEComponentSyncopation extends TEComponentBase {
    private int syncopationType;

    public TEComponentSyncopation(TEPosition position, int syncopationType) {
        super(position);

        this.syncopationType = syncopationType;
    }

    public int getSyncopationType() {
        return this.syncopationType;
    }

    public int getSortOrder() {
        return 70;
    }
}
