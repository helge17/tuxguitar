package org.herac.tuxguitar.app.action.impl.edit;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasurePasteDialogAction;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.note.TGPasteNoteAction;
import org.herac.tuxguitar.editor.clipboard.TGClipboard;
import org.herac.tuxguitar.util.TGContext;

public class TGPasteAction extends TGActionBase {

	public static final String NAME = "action.edit.paste";
	
	public TGPasteAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext tgActionContext){
		TGClipboard clipboard = TGClipboard.getInstance(getContext());
		if (clipboard.getSegment() != null) {
			TGActionManager.getInstance(this.getContext()).execute(TGOpenMeasurePasteDialogAction.NAME, tgActionContext);
		} else if (clipboard.getBeats() != null) {
			TGActionManager.getInstance(getContext()).execute(TGPasteNoteAction.NAME, tgActionContext);
		}
	}

}
