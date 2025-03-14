package app.tuxguitar.io.tef3.base;

public class TEComponentVoiceChange extends TEComponentBase {
    private int midiPatch;
    private int bankMSB;
    private int bankLSB;

    public TEComponentVoiceChange(TEPosition position, int midiPatch, int bankMSB, int bankLSB)
    {
        super(position);

        this.midiPatch = midiPatch;
        this.bankMSB = bankMSB;
        this.bankLSB = bankLSB;
    }

    public int getMidiPatch() {
        return this.midiPatch;
    }

    public int getBankMSB() {
        return this.bankMSB;
    }

    public int getBankLSB() {
        return this.bankLSB;
    }

    public int getSortOrder() {
        return 50;
    }
}
