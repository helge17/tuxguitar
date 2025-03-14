package app.tuxguitar.android.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.util.TGContext;

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
