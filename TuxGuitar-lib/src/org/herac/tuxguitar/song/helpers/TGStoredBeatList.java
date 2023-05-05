package org.herac.tuxguitar.song.helpers;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.TGNoteRange;

import java.util.ArrayList;
import java.util.List;

public class TGStoredBeatList {
    private List<TGBeat> beats;
    private long length = 0;

    public TGStoredBeatList(List<TGBeat> range, TGFactory factory) {
        this.beats = new ArrayList<>();
        long first = -1;
        for (TGBeat beat : range) {
            if (first == -1) {
                first = beat.getStart();
            }
            beat = beat.clone(factory);
            beat.setStart(beat.getStart() - first);
            beat.setMeasure(null);
            this.length += beat.getVoice(0).getDuration().getTime();
            this.beats.add(beat);
        }
    }

    public List<TGBeat> getBeats() {
        return beats;
    }

    public long getLength() {
        return length;
    }
}
