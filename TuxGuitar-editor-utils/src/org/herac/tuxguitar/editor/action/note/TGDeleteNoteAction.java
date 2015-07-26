package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.util.TGContext;

public class TGDeleteNoteAction extends TGActionBase {
	
	public static final String NAME = "action.beat.general.delete-note";
	
	public TGDeleteNoteAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGNote note = (TGNote) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
		
		songManager.getMeasureManager().removeNote(note);
	}
}
