package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.action.impl.edit.TGOpenMeasureErrorsDialogAction;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.util.TGContext;

public class TGUpdateReadSongController extends TGUpdateItemsController {
	
	public TGUpdateReadSongController() {
		super();
	}
	
	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		TGDocumentManager tgDocumentManager = TGDocumentManager.getInstance(context);
		if (!tgDocumentManager.getSongManager().isValid(tgDocumentManager.getSong())) {
			TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGOpenMeasureErrorsDialogAction.NAME);
			tgActionProcessor.process();
		}
	}

}
