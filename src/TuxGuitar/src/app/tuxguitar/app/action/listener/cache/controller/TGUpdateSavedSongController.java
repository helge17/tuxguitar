package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.action.impl.file.TGWriteFileAction;
import app.tuxguitar.app.document.TGDocument;
import app.tuxguitar.app.document.TGDocumentListManager;
import app.tuxguitar.app.helper.TGFileHistory;
import app.tuxguitar.app.transport.TGTransport;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.util.TGContext;

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
		if (Boolean.TRUE.equals(actionContext.getAttribute(TGWriteFileAction.ATTRIBUTE_NATIVE_FILE_FORMAT) )) {
			tgDocument.setUnsaved(false);
		}

		TGFileHistory tgFileHistory = TGFileHistory.getInstance(context);
		tgFileHistory.reset(null);

		TGTransport.getInstance(context).getCache().reset();
		TGWindow.getInstance(context).loadTitle();
		// ------------------------------------------------------ //

		this.findUpdateBuffer(context, actionContext).requestUpdateSavedSong();

		super.update(context, actionContext);
	}
}
