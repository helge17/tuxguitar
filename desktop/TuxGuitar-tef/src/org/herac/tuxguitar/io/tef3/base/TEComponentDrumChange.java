package org.herac.tuxguitar.io.tef3.base;

public class TEComponentDrumChange extends TEComponentBase {
    private int drumPatch;
    private int volume;
    private int character;
    
    public TEComponentDrumChange(TEPosition position, int drumPatch, int volume, int character) {
        super(position);

        this.drumPatch = drumPatch;
        this.volume = volume;
        this.character = character;
    }

    public int getDrumPatch() {
        return this.drumPatch;
    }

    public int getVolume() {
        return this.volume;
    }

    public int getCharacter() {
        return this.character;
    }

    public int getSortOrder() {
        return 140;
    }
}
