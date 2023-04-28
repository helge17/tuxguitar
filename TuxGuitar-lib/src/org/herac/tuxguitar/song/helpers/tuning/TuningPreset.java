package org.herac.tuxguitar.song.helpers.tuning;

public class TuningPreset {
    private TuningGroup parent;
    private String name;
    private int[] values;

    public TuningPreset(TuningGroup parent, String name, int[] values) {
        this.parent = parent;
        this.name = name;
        this.values = values;
    }
    public TuningGroup getParent() {
        return this.parent;
    }
    public String getName() {
        return this.name;
    }
    public int[] getValues() {
        return this.values;
    }
}
