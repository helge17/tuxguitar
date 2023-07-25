package org.herac.tuxguitar.editor.action.note;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGMeasureManager;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;

public abstract class TGShiftNoteAction extends TGActionBase {

	public TGShiftNoteAction(TGContext context, String name) {
		super(context, name);
	}
	
	protected boolean sortStringsAscending;
	protected abstract boolean canShiftIndividualNote(TGMeasureManager measureManager, TGNote note);
	protected abstract int shiftNote(TGMeasureManager measureManager, TGNote note);

	protected void processAction(TGActionContext context){
		TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		TGNote caretNote = ((TGNote) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE));
		TGMeasureManager measureManager = getSongManager(context).getMeasureManager();
		
		if (noteRange!=null && !noteRange.isEmpty()) {
			List<TGNote> listNotes = noteRange.getNotes();
			if (this.sortStringsAscending) {
				Collections.sort(listNotes, (n1,n2) -> n1.getString()-n2.getString());
			} else {
				Collections.sort(listNotes, (n1,n2) -> n2.getString()-n1.getString());
			}
			boolean success = true;
			Iterator<TGNote> it = listNotes.iterator();
			while (it.hasNext() && success) {
				TGNote note = it.next();
				success &= this.canShiftIndividualNote(measureManager, note);
			}
			if (success) {
				for (TGNote note: listNotes) {
					int nextString = this.shiftNote(measureManager, note);
					if (caretNote == note){
						context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, note.getVoice().getBeat().getMeasure().getTrack().getString(nextString));
					}
				}
				context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
			}
		} else if( caretNote != null && measure != null ){
			int nextString = this.shiftNote(measureManager, caretNote);
			if( nextString > 0 ){
				context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, track.getString(nextString));
				context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
			}
		}
	}

}
