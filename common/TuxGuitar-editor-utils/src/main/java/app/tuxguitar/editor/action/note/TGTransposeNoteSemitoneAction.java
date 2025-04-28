package app.tuxguitar.editor.action.note;

import java.util.Iterator;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

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
