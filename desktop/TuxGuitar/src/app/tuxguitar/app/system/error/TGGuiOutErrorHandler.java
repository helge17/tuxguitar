package app.tuxguitar.app.system.error;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.action.listener.error.TGActionErrorHandler;
import app.tuxguitar.app.view.dialog.message.TGMessageDialog;
import app.tuxguitar.app.view.dialog.message.TGMessageDialogController;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.error.TGErrorHandler;

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
