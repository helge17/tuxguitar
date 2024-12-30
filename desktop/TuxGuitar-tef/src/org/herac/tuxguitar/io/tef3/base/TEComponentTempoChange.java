package org.herac.tuxguitar.io.tef3.base;

public class TEComponentTempoChange extends TEComponentBase {
    private int bpm;
    private int rallentandoDuration;

    public TEComponentTempoChange(TEPosition position, int bpm, int rallentandoDuration) {
        super(position);

        this.bpm = bpm;
        this.rallentandoDuration = rallentandoDuration;
    }

    public int getBpm() {
        return this.bpm;
    }

    public int getRallentandoDuration() {
        return this.rallentandoDuration;
    }

    public int getSortOrder() {
        return 40;
    }
}