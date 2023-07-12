package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGMeasureManager;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;

public abstract class TGTransposeNoteSemitoneAction extends TGActionBase {

	public TGTransposeNoteSemitoneAction(TGContext context, String name) {
		super(context, name);
	}

	protected abstract boolean transposeSemiTone(TGMeasureManager measureManager, TGMeasure measure, TGBeat beat, TGNote note);

	protected void processAction(TGActionContext context){
		boolean success = false;
		TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		if (noteRange!=null && !noteRange.isEmpty()) {
			TGSongManager songManager = getSongManager(context);
			// count number of beats: if 1 single beat, set TGChangeNoteAction.ATTRIBUTE_SUCCESS to True, to play sound
			boolean moreThanOneBeat = false;
			TGBeat refBeat = null;
			for (TGNote note : noteRange.getNotes()) {
				TGBeat beat = note.getVoice().getBeat();
				TGMeasure measure = beat.getMeasure();
				moreThanOneBeat |= (refBeat!=null && beat!=refBeat);
				refBeat = beat;
				success |= transposeSemiTone(songManager.getMeasureManager(), measure, beat, note);
			}
			if (success) {
				context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
				if (moreThanOneBeat) {
					context.setAttribute(TGChangeNoteAction.ATTRIBUTE_SUCCESS, Boolean.FALSE);
				} else {
					context.setAttribute(TGChangeNoteAction.ATTRIBUTE_SUCCESS, Boolean.TRUE);
				}
			}
		}
		else {
			TGNote note = ((TGNote) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE));
			if( note != null ){
				TGBeat beat = ((TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
				TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
				TGSongManager songManager = getSongManager(context);
				
				if (transposeSemiTone(songManager.getMeasureManager(), measure, beat, note)) {
					context.setAttribute(TGChangeNoteAction.ATTRIBUTE_SUCCESS, Boolean.TRUE);
				}
			}
		}
	}

}
