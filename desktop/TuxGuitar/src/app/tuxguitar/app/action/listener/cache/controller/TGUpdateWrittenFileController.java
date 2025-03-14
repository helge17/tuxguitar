package app.tuxguitar.app.action.listener.cache.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.action.impl.file.TGWriteFileAction;
import app.tuxguitar.app.document.TGDocumentListManager;
import app.tuxguitar.app.helper.TGFileHistory;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGException;

public class TGUpdateWrittenFileController extends TGUpdateItemsController {

	public TGUpdateWrittenFileController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		try {
			String fileName = actionContext.getAttribute(TGWriteFileAction.ATTRIBUTE_FILE_NAME);
			// update document uri only for native file format
			if( (fileName != null) && Boolean.TRUE.equals(actionContext.getAttribute(TGWriteFileAction.ATTRIBUTE_NATIVE_FILE_FORMAT)) ) {
				URI uri = new File(fileName).toURI();
				URL url = uri.toURL();

				TGDocumentListManager.getInstance(context).findCurrentDocument().setUri(uri);

				TGFileHistory tgFileHistory = TGFileHistory.getInstance(context);
				tgFileHistory.reset(url);
				tgFileHistory.setChooserPath( url );
			}

			super.update(context, actionContext);
		} catch (MalformedURLException e) {
			throw new TGException(e);
		}
	}
}
