package org.herac.tuxguitar.song.helpers;

import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;

import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Stream;

public class TGBeatRangeIterator implements Iterator<TGBeat> {

    private final TGMeasure lastMeasure;
    private Iterator<TGMeasure> measureIterator;
    private TGMeasure currentMeasure;

    private Iterator<TGBeat> beatIterator;
    private Stream<TGBeat> currentBeats;

    private final long start;
    private final long end;

    public TGBeatRangeIterator(TGBeat start, TGBeat end) {
        if (start.getMeasure().getTrack() != end.getMeasure().getTrack()) {
            throw new IllegalArgumentException("Beats must be on the same track");
        }
        if (start.getStart() > end.getStart()) {
            throw new IllegalArgumentException("Start beat must be before or equal to last beat");
        }

        this.lastMeasure = end.getMeasure();
        this.start = start.getStart();
        this.end = end.getStart();

        measureIterator = start.getMeasure().getTrack().getMeasures();
        while (currentMeasure != start.getMeasure())
            currentMeasure = measureIterator.next();

        updateBeats();
    }

    @Override
    public boolean hasNext() {
        moveToActiveBeatIteratorOrEnd();
        return beatIterator.hasNext();
    }

    @Override
    public TGBeat next() {
        moveToActiveBeatIteratorOrEnd();
        return beatIterator.next();
    }

    private void moveToActiveBeatIteratorOrEnd() {
        while(!beatIterator.hasNext() && !isInTheEnd()) {
            if (measureIterator.hasNext()) {
                currentMeasure = measureIterator.next();
                updateBeats();
            }
        }
    }

    private void updateBeats() {
        currentBeats = currentMeasure.getBeats().stream()
                .filter(b -> b.getStart() >= start && b.getStart() <= end)
                .sorted(Comparator.comparingLong(TGBeat::getStart));
        beatIterator = currentBeats.iterator();
    }

    private boolean isInTheEnd() {
        return currentMeasure == lastMeasure && !beatIterator.hasNext();
    }
}
