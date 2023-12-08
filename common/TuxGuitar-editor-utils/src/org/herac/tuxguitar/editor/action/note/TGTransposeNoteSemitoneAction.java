package org.herac.tuxguitar.editor.action.note;

import java.util.Iterator;

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
	protected abstract boolean canTransposeSemiTone(TGMeasureManager measureManager, TGMeasure measure, TGBeat beat, TGNote note);

	protected void processAction(TGActionContext context){
		TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGSongManager songManager = getSongManager(context);
		if (noteRange!=null && !noteRange.isEmpty()) {
			// before doing the job, check it's possible for all notes
			boolean success = true;
			Iterator<TGNote> it = noteRange.getNotes().iterator();
			while (it.hasNext() && success) {
				TGNote note = it.next();
				TGBeat beat = note.getVoice().getBeat();
				TGMeasure measure = beat.getMeasure();
				success &= canTransposeSemiTone(songManager.getMeasureManager(), measure, beat, note);
			}
			if (success) {
				boolean moreThanOneBeat = false;  // count number of beats: if 1 single beat then play sound
				TGBeat refBeat = null;
				for (TGNote note : noteRange.getNotes()) {
					TGBeat beat = note.getVoice().getBeat();
					TGMeasure measure = beat.getMeasure();
					moreThanOneBeat |= (refBeat!=null && beat!=refBeat);
					refBeat = beat;
					transposeSemiTone(songManager.getMeasureManager(), measure, beat, note);
				}
				if (!moreThanOneBeat) {
					context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
				}
			}
		}
	}
}
