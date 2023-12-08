package org.herac.tuxguitar.util;

import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;

import java.util.*;

public class TGBeatRange {

    private List<TGBeat> beats;
    private List<TGMeasure> measures;

    public TGBeatRange(List<TGBeat> beats) {
        TreeSet<TGMeasure> measures = new TreeSet<>(Comparator.comparingLong(a -> a.getHeader().getNumber()));
        this.beats = beats;
        for (TGBeat beat : beats) {
            measures.add(beat.getMeasure());
        }
        this.measures = new ArrayList<>(measures);
    }

    public boolean isEmpty() {
        return this.beats.isEmpty();
    }

    public List<TGBeat> getBeats() {
        return beats;
    }

    public List<TGMeasure> getMeasures() {
        return measures;
    }

    public TGMeasure firstMeasure() {
        return this.measures.get(0);
    }

    public TGMeasure lastMeasure() {
        return this.measures.get(this.measures.size() - 1);
    }

    public boolean containsMeasure(TGMeasure measure) {
        int index = Collections.binarySearch(measures, measure, Comparator.comparingLong(a -> a.getHeader().getNumber()));
        return index >= 0 && index < measures.size() && measures.get(index) == measure;
    }

    public static TGBeatRange single(TGBeat note) {
        return new TGBeatRange(Collections.singletonList(note));
    }

    public static TGBeatRange empty() {
        return new TGBeatRange(Collections.emptyList());
    }
}
