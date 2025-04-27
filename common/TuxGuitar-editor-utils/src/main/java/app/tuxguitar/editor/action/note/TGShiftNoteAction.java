package app.tuxguitar.editor.action.note;

import java.util.Collections;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public abstract class TGShiftNoteAction extends TGActionBase {

	public TGShiftNoteAction(TGContext context, String name) {
		super(context, name);
	}

	protected boolean sortStringsAscending;
	protected abstract int shiftNote(TGMeasureManager measureManager, TGNote note);

	protected void processAction(TGActionContext context){
		TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGNote caretNote = ((TGNote) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE));
		TGMeasureManager measureManager = getSongManager(context).getMeasureManager();

		if (noteRange!=null && !noteRange.isEmpty()) {
			List<TGNote> listNotes = noteRange.getNotes();
			if (this.sortStringsAscending) {
				Collections.sort(listNotes, (n1,n2) -> n1.getString()-n2.getString());
			} else {
				Collections.sort(listNotes, (n1,n2) -> n2.getString()-n1.getString());
			}
			boolean moreThanOneBeat = false;  // count number of beats: if 1 single beat then play sound
			TGBeat refBeat = null;
			for (TGNote note: listNotes) {
				TGBeat beat = note.getVoice().getBeat();
				moreThanOneBeat |= (refBeat!=null && beat!=refBeat);
				refBeat = beat;
				int nextString = this.shiftNote(measureManager, note);
				if (caretNote == note && nextString != 0){
					context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, note.getVoice().getBeat().getMeasure().getTrack().getString(nextString));
				}
			}
			if (!moreThanOneBeat) {
				context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
			}
		}
	}

}
