package org.herac.tuxguitar.android.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.undo.TGUndoableManager;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateSavedSongController extends TGUpdateItemsController {

	public TGUpdateSavedSongController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		TGUndoableManager.getInstance(context).discardAllEdits();

		this.findUpdateBuffer(context).requestUpdateSavedSong();
		
		super.update(context, actionContext);
	}
}
