package org.herac.tuxguitar.app.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.action.impl.edit.TGOpenMeasureErrorsDialogAction;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateReadSongController extends TGUpdateItemsController {
	
	public TGUpdateReadSongController() {
		super();
	}
	
	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		TGDocumentManager tgDocumentManager = TGDocumentManager.getInstance(context);
		if (!tgDocumentManager.getSongManager().getMeasureErrors(tgDocumentManager.getSong()).isEmpty()) {
			TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGOpenMeasureErrorsDialogAction.NAME);
			tgActionProcessor.process();
		}
	}

}
