package app.tuxguitar.util;

import app.tuxguitar.song.helpers.TGBeatRangeNoteIterator;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;

import java.util.*;

/**
 * Created by tubus on 26.01.17.
 */
public class TGNoteRange {
	private List<TGNote> notes;
	private List<TGMeasure> measures;
	private List<TGBeat> beats;

	public TGNoteRange(List<TGNote> notes) {
		TreeSet<TGMeasure> measures = new TreeSet<>(Comparator.comparingLong(a -> a.getHeader().getNumber()));
		TreeSet<TGBeat> beats = new TreeSet<>(Comparator.comparingLong(TGBeat::getStart));
		this.notes = notes;
		for (TGNote note : notes) {
			TGBeat beat = note.getVoice().getBeat();
			beats.add(beat);
			measures.add(beat.getMeasure());
		}
		this.measures = new ArrayList<>(measures);
		this.beats = new ArrayList<>(beats);
	}

	public TGNoteRange(TGBeat start, TGBeat end, Collection<Integer> voices) {
		this(new TGBeatRangeNoteIterator(start, end, voices).toList());
	}

	public static TGNoteRange single(TGNote note) {
		return new TGNoteRange(Collections.singletonList(note));
	}

	public static TGNoteRange empty() {
		return new TGNoteRange(Collections.emptyList());
	}

	public List<TGNote> getNotes() {
		return notes;
	}

	public boolean isEmpty() {
		return notes.isEmpty();
	}

	public TGMeasure firstMeasure() {
		return this.measures.get(0);
	}

	public TGMeasure lastMeasure() {
		return this.measures.get(this.measures.size() - 1);
	}

	public List<TGMeasure> getMeasures() {
		return measures;
	}

	public List<TGBeat> getBeats() {
		return beats;
	}
}
