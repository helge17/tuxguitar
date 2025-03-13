package app.tuxguitar.io.tef3.base;

public abstract class TEComponentBase {
    private TEPosition position;

    public TEComponentBase(TEPosition position) {
        this.position = position;
    }

    public TEPosition getPosition() {
        return this.position;
    }

    public abstract int getSortOrder();
}
