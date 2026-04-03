package app.tuxguitar.song.helpers;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGVoice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class TGBeatRangeNoteIterator implements Iterator<TGNote> {
    private final TGBeatRangeIterator beatRangeIterator;
    private TGBeat currentBeat;

    private Collection<Integer> voices;
    private Iterator<Integer> voiceIterator;
    private TGVoice currentVoice;

    private Iterator<TGNote> noteIterator;

    public TGBeatRangeNoteIterator(TGBeat start, TGBeat end, Collection<Integer> voices) {
        this.beatRangeIterator = new TGBeatRangeIterator(start, end);
        this.voices = voices;
        updateBeat();
    }

    public boolean hasNext() {
        moveToActiveNoteIteratorOrEnd();
        return noteIterator.hasNext();
    }

    public TGNote next() {
        moveToActiveNoteIteratorOrEnd();
        return noteIterator.next();
    }

    private void moveToActiveNoteIteratorOrEnd() {
        while(!noteIterator.hasNext() && !isInTheEnd()) {
            if (voiceIterator.hasNext()) {
                updateVoice();
            }
            else if (beatRangeIterator.hasNext()) {
                updateBeat();
            }
        }
    }

    private void updateBeat() {
        currentBeat = beatRangeIterator.next();
        this.voiceIterator = voices.iterator();
        updateVoice();
    }

    private void updateVoice() {
        currentVoice = currentBeat.getVoice(voiceIterator.next());
        noteIterator = currentVoice.getNotes().iterator();
    }

    private boolean isInTheEnd() {
        return !noteIterator.hasNext() && !beatRangeIterator.hasNext() && !voiceIterator.hasNext();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public List<TGNote> toList() {
        ArrayList<TGNote> selectedNotes = new ArrayList<TGNote>();
        while (this.hasNext()) {
            selectedNotes.add(this.next());
        }
        return selectedNotes;
    }

}
