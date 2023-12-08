package org.herac.tuxguitar.app.system.error;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.action.listener.error.TGActionErrorHandler;
import org.herac.tuxguitar.app.view.dialog.message.TGMessageDialog;
import org.herac.tuxguitar.app.view.dialog.message.TGMessageDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorHandler;

public class TGGuiOutErrorHandler implements TGErrorHandler {
	
	private TGContext context;
	
	public TGGuiOutErrorHandler(TGContext context) {
		this.context = context;
	}
	
	public void handleError(Throwable throwable) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGMessageDialogController());
		tgActionProcessor.setAttribute(TGMessageDialog.ATTRIBUTE_STYLE, TGMessageDialog.STYLE_ERROR);
		tgActionProcessor.setAttribute(TGMessageDialog.ATTRIBUTE_TITLE, TuxGuitar.getProperty("error"));
		tgActionProcessor.setAttribute(TGMessageDialog.ATTRIBUTE_MESSAGE, (throwable.getMessage() != null ? throwable.getMessage() : throwable.getClass().getName()));
		
		// prevent handle error loop
		tgActionProcessor.setAttribute(TGActionErrorHandler.ATTRIBUTE_ERROR_HANDLER, new TGErrorHandler() {
			public void handleError(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		tgActionProcessor.process();
	}
}
