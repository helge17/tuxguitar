package org.herac.tuxguitar.app.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
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
		
		TuxGuitar.getInstance().getFileHistory().reset(null);
		
		TuxGuitar.getInstance().getEditorCache().reset();
		TuxGuitar.getInstance().showTitle();
		// ------------------------------------------------------ //
		
		this.findUpdateBuffer(context, actionContext).requestUpdateSavedSong();
		
		super.update(context, actionContext);
	}
}
