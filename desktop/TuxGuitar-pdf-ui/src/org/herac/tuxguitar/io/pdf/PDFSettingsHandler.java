package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsMode;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintSettingsDialog;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintSettingsDialogController;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintSettingsHandler;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;
import org.herac.tuxguitar.graphics.control.print.TGPrintSettings;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;

public class PDFSettingsHandler implements TGPersistenceSettingsHandler {

	private TGContext context;
	
	public PDFSettingsHandler(TGContext context) {
		this.context = context;
	}
	
	public TGFileFormat getFileFormat() {
		return PDFSongWriter.FILE_FORMAT;
	}
	
	public TGPersistenceSettingsMode getMode() {
		return TGPersistenceSettingsMode.WRITE;
	}
	
	public void handleSettings(final TGSongStreamContext context, final Runnable callback) {
		TGLayoutStyles tgLayoutStyles = new PDFLayoutStylesUI(TGConfigManager.getInstance(this.context));
		context.setAttribute(TGLayoutStyles.class.getName(), tgLayoutStyles);
		
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPrintSettingsDialogController());
		tgActionProcessor.setAttribute(TGPrintSettingsDialog.ATTRIBUTE_HANDLER, new TGPrintSettingsHandler() {
			public void updatePrintSettings(TGPrintSettings styles) {
				context.setAttribute(TGPrintSettings.class.getName(), styles);
				callback.run();
			}
		});
		tgActionProcessor.process();
	}
}
