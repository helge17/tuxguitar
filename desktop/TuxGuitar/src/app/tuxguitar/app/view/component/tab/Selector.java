package app.tuxguitar.app.view.component.tab;

import app.tuxguitar.app.document.TGDocument;
import app.tuxguitar.app.document.TGDocumentListManager;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.graphics.control.TGTrackImpl;
import app.tuxguitar.song.helpers.TGBeatRangeIterator;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGNoteRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by tubus on 20.12.16.
 */
public class Selector {

	private final Tablature tablature;
	private TGBeat initial;
	private TGBeat start;
	private TGBeat end;
	private boolean active;

	public Selector(Tablature tablature) {
		this.tablature = tablature;
	}

	public void initializeSelection(TGBeat beat) {
		initial = beat;
		start = beat;
		end = beat;
		active = false;

		this.saveState();
	}

	public void updateSelection(TGBeat beat) {
		if (initial == null || beat == null) {
			initializeSelection(beat);
		} else {
			active = true;
			// checking if beats order is consistent
			// it may be false if some measures are invalid: then, break selection
			TGTrack track = beat.getMeasure().getTrack();
			boolean overlap = false;
			if (initial.getMeasure().getNumber() < beat.getMeasure().getNumber()
					|| initialIsEarlierInTheSameMeasure(beat)) {
				// left to right selection
				int firstSelectabledMeasureNb = initial.getMeasure().getNumber();
				for (int m = firstSelectabledMeasureNb; m<beat.getMeasure().getNumber(); m++) {
					if (!measureCanBeSelected(track.getMeasure(m-1))) {
						// invalid overlap
						overlap = true;
						firstSelectabledMeasureNb = m;
					}
				}
				if (overlap) {
					// found at least an invalid measure, restart selection from first measure that can be selected
					start = track.getMeasure(firstSelectabledMeasureNb).getBeat(0);
					initial = start;
					end = beat;
				}
				else {
					// no invalid measure found, normal selection
					start = initial;
					end = beat;
				}
			}
			else {
				// right to left selection
				int lastSelectableMeasureNb = initial.getMeasure().getNumber();
				for (int m = lastSelectableMeasureNb; m>beat.getMeasure().getNumber(); m--) {
					if (!measureCanBeSelected(track.getMeasure(m-2))) {
						// invalid overlap
						overlap = true;
						lastSelectableMeasureNb = m-1;
					}
				}
				if (overlap) {
					// found at least an invalid measure, restart selection from last beat of last measure that can be selected
					start = beat;
					end = track.getMeasure(lastSelectableMeasureNb-1).getBeat(track.getMeasure(lastSelectableMeasureNb-1).countBeats()-1);
					initial = end;
				}
				else {
					// no invalid measure found, normal selection
					start = beat;
					end = initial;
				}
			}
		}
	}

	public void clearSelection() {
		initializeSelection(null);
	}

	private boolean measureCanBeSelected(TGMeasure measure) {
		int measureNb = measure.getNumber();
		if (measureNb == measure.getTrack().countMeasures()) return true;
		// measure.getNumber: first is 1
		// track.getMeasure: first is 0
		return (measure.getBeat(measure.countBeats()-1).getPreciseStart() < measure.getTrack().getMeasure(measureNb).getBeat(0).getPreciseStart());
	}

	private boolean initialIsEarlierInTheSameMeasure(TGBeat beat) {
		return initial.getMeasure().getNumber() == beat.getMeasure().getNumber()
				&& initial.getStart() < beat.getStart();
	}

	public TGBeat getInitialBeat() {
		return initial;
	}

	public TGBeat getStartBeat() {
		return start;
	}

	public TGBeat getEndBeat() {
		return end;
	}

	public boolean isActive() {
		return active;
	}

	public void paintSelectedArea(TGLayout viewLayout, UIPainter painter) {
		if (isActive()) {
			int activeTrackNumber = tablature.getTrackSelection();
			TGTrackImpl track = (TGTrackImpl) initial.getMeasure().getTrack();
			// paint only if track is displayed
			if (activeTrackNumber < 0 || (activeTrackNumber == track.getNumber())) {
				track.paintBeatSelection(viewLayout, painter, start, end);
			}
		}
	}

	public TGNoteRange getNoteRange(Collection<Integer> voices) {
		return new TGNoteRange(start, end, voices);
	}

	public TGBeatRange getBeatRange() {
		if (!isActive()) {
			return TGBeatRange.empty();
		}
		List<TGBeat> beats = new ArrayList<>();
		for (TGBeatRangeIterator it = new TGBeatRangeIterator(start, end); it.hasNext();) {
			beats.add(it.next());
		}
		return new TGBeatRange(beats);
	}

	private void saveState() {
		TGDocumentListManager documents = TGDocumentListManager.getInstance(this.tablature.getContext());
		TGDocument document = this.initial == null ? documents.findCurrentDocument()
				: documents.findDocument(this.initial.getMeasure().getTrack().getSong());
		if (isActive()) {
			document.setSelectionStart(this.getStartBeat());
			document.setSelectionEnd(this.getEndBeat());
		} else {
			document.setSelectionStart(null);
			document.setSelectionEnd(null);
		}
	}

	public void restoreStateFrom(TGDocument document) {
		TGBeat start = document.getSelectionStart();
		TGBeat end = document.getSelectionEnd();
		if ((start != null) && (end != null)) {
			this.initializeSelection(start);
			this.updateSelection(end);
		} else {
			this.clearSelection();
		}
	}
}
