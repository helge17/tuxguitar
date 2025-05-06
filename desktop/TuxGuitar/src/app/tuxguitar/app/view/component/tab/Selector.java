package app.tuxguitar.app.view.component.tab;

import app.tuxguitar.app.document.TGDocument;
import app.tuxguitar.app.document.TGDocumentListManager;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.graphics.control.TGTrackImpl;
import app.tuxguitar.song.helpers.TGBeatRangeIterator;
import app.tuxguitar.song.models.TGBeat;
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
			if (this.beatsOrderIsConsistent(beat)) {
				active = true;
				if (initial.getMeasure().getNumber() < beat.getMeasure().getNumber()
						|| initialIsEarlierInTheSameMeasure(beat)) {
					start = initial;
					end = beat;
				} else {
					start = beat;
					end = initial;
				}
				this.saveState();
			}
			else {
				active = false;
			}
		}
	}

	public void clearSelection() {
		initializeSelection(null);
	}

	private boolean initialIsEarlierInTheSameMeasure(TGBeat beat) {
		return initial.getMeasure().getNumber() == beat.getMeasure().getNumber()
				&& initial.getStart() < beat.getStart();
	}

	private boolean beatsOrderIsConsistent(TGBeat beatToSelect) {
		// in free edition mode, some measures may be invalid, e.g. too long
		// in this case, some beats in a measure may have a start attribute bigger than notes in the next measure!
		// in other words: whenever one measure is too long, the order of beats shown on score/tab is not
		// consistent with their .start attribute
		if ( (beatToSelect.getMeasure().getNumber() < start.getMeasure().getNumber())
				&& (beatToSelect.getStart() >= start.getStart()) ) {
			return false;
		}
		if ( (beatToSelect.getMeasure().getNumber() > end.getMeasure().getNumber())
				&& (beatToSelect.getStart() <= end.getStart()) ) {
			return false;
		}
		return true;
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
