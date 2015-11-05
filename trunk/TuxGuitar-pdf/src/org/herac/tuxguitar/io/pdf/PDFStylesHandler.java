package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.app.printer.PrintStyles;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintStylesDialog;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintStylesDialogController;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintStylesHandler;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;

public class PDFStylesHandler implements TGSongStreamSettingsHandler {

	private TGContext context;
	
	public PDFStylesHandler(TGContext context) {
		this.context = context;
	}
	
	public String getProviderId() {
		return PDFSongExporter.PROVIDER_ID;
	}

	public void handleSettings(final TGSongStreamContext context, final Runnable callback) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPrintStylesDialogController());
		tgActionProcessor.setAttribute(TGPrintStylesDialog.ATTRIBUTE_HANDLER, new TGPrintStylesHandler() {
			public void updatePrintStyles(final PrintStyles styles) {
				context.setAttribute(PrintStyles.class.getName(), styles);
				callback.run();
			}
		});
		tgActionProcessor.process();
	}
}
