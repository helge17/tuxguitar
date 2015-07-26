package org.herac.tuxguitar.app.action.listener.cache.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.file.TGWriteFileAction;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;

public class TGUpdateWrittenFileController extends TGUpdateItemsController {

	public TGUpdateWrittenFileController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		try {
			String fileName = actionContext.getAttribute(TGWriteFileAction.ATTRIBUTE_FILE_NAME);
			if( fileName != null ) {
				URL url = new File(fileName).toURI().toURL();
				
				TuxGuitar.getInstance().getFileHistory().reset(url);
				TuxGuitar.getInstance().getFileHistory().setChooserPath( url );
			}
			
			super.update(context, actionContext);
		} catch (MalformedURLException e) {
			throw new TGException(e);
		}
	}
}
