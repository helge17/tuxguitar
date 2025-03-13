package app.tuxguitar.io.tef2.base;

public class TEComponentTempoChange extends TEComponent {
    private int bpm;

    public TEComponentTempoChange(int position,int measure, int string, int bpm) {
        super(position, measure, string);

        this.bpm = bpm;
    }

    public int getBpm() {
        return this.bpm;
    }
}
