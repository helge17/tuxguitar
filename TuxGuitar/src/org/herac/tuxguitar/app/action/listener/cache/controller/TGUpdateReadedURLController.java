package org.herac.tuxguitar.app.action.listener.cache.controller;

import java.net.URL;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.file.TGReadURLAction;
import org.herac.tuxguitar.app.document.TGDocumentListManager;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateReadedURLController extends TGUpdateItemsController {

	public TGUpdateReadedURLController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		URL url = actionContext.getAttribute(TGReadURLAction.ATTRIBUTE_URL);
		if( url != null ) {
			TGDocumentListManager.getInstance(context).findCurrentDocument().setUrl(url);
			
			TuxGuitar.getInstance().getFileHistory().reset(url);
			TuxGuitar.getInstance().getFileHistory().setChooserPath( url );
		}
		
		super.update(context, actionContext);
	}
}
