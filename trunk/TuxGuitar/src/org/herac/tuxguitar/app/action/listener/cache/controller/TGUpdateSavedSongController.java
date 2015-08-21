package org.herac.tuxguitar.app.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.document.TGDocument;
import org.herac.tuxguitar.app.document.TGDocumentListManager;
import org.herac.tuxguitar.app.helper.TGFileHistory;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.editor.undo.TGUndoableManager;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateSavedSongController extends TGUpdateItemsController {

	public TGUpdateSavedSongController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		// ------------------------------------------------------ //		
		TGUndoableManager.getInstance(context).discardAllEdits();
		
		TGDocument tgDocument = TGDocumentListManager.getInstance(context).findCurrentDocument();
		tgDocument.setUnwanted(false);
		tgDocument.setUnsaved(false);
		
		TGFileHistory tgFileHistory = TGFileHistory.getInstance(context);
		tgFileHistory.reset(null);
		
		TuxGuitar.getInstance().getEditorCache().reset();
		TGWindow.getInstance(context).loadTitle();
		// ------------------------------------------------------ //
		
		this.findUpdateBuffer(context, actionContext).requestUpdateSavedSong();
		
		super.update(context, actionContext);
	}
}
