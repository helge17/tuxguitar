package org.herac.tuxguitar.app.system.error;

import org.eclipse.swt.SWT;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
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
		tgActionProcessor.setAttribute(TGMessageDialog.ATTRIBUTE_STYLE, SWT.ICON_ERROR);
		tgActionProcessor.setAttribute(TGMessageDialog.ATTRIBUTE_TITLE, TuxGuitar.getProperty("error"));
		tgActionProcessor.setAttribute(TGMessageDialog.ATTRIBUTE_MESSAGE, (throwable.getMessage() != null ? throwable.getMessage() : throwable.getClass().getName()));
		tgActionProcessor.process();
	}
}
