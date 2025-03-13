package app.tuxguitar.io.pdf;

import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import app.tuxguitar.app.io.persistence.TGPersistenceSettingsMode;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import app.tuxguitar.app.view.dialog.printer.TGPrintSettingsDialog;
import app.tuxguitar.app.view.dialog.printer.TGPrintSettingsDialogController;
import app.tuxguitar.app.view.dialog.printer.TGPrintSettingsHandler;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.graphics.control.TGLayoutStyles;
import app.tuxguitar.graphics.control.print.TGPrintSettings;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class PDFSettingsHandler implements TGPersistenceSettingsHandler, TGPluginSettingsHandler{

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
		TGLayoutStyles tgLayoutStyles = new PDFLayoutStylesUI(TGConfigManager.getInstance(this.context), this.context);
		context.setAttribute(TGLayoutStyles.class.getName(), tgLayoutStyles);

		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPrintSettingsDialogController());
		tgActionProcessor.setAttribute(TGPrintSettingsDialog.ATTRIBUTE_HANDLER, new TGPrintSettingsHandler() {
			public void updatePrintSettings(TGPrintSettings styles, int zoomValue) {
				context.setAttribute(TGPrintSettings.ATTRIBUTE_PRINT_STYLES, styles);
				context.setAttribute(TGPrintSettings.ATTRIBUTE_PRINT_ZOOM, zoomValue);
				callback.run();
			}
		});
		tgActionProcessor.process();
	}

	@Override
	public void openSettingsDialog(UIWindow parent) {
		new PDFSettingsDialog(this.context).configure(parent);
	}
}
