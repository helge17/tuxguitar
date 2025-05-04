package app.tuxguitar.app.action.impl.file;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.printer.PrintController;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.dialog.printer.TGPrintSettingsDialog;
import app.tuxguitar.app.view.dialog.printer.TGPrintSettingsDialogController;
import app.tuxguitar.app.view.dialog.printer.TGPrintSettingsHandler;
import app.tuxguitar.app.view.dialog.printer.TGPrinterChooserDialog;
import app.tuxguitar.app.view.dialog.printer.TGPrinterChooserDialogController;
import app.tuxguitar.app.view.dialog.printer.TGPrinterChooserHandler;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.graphics.control.TGFactoryImpl;
import app.tuxguitar.graphics.control.print.TGPrintController;
import app.tuxguitar.graphics.control.print.TGPrintDocument;
import app.tuxguitar.graphics.control.print.TGPrintLayout;
import app.tuxguitar.graphics.control.print.TGPrintPainter;
import app.tuxguitar.graphics.control.print.TGPrintSettings;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.printer.UIPrinter;
import app.tuxguitar.ui.printer.UIPrinterJob;
import app.tuxguitar.ui.printer.UIPrinterPage;
import app.tuxguitar.ui.resource.UIInset;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;

public class TGPrintAction extends TGActionBase{

	public static final String NAME = "action.file.print";

	public static final String ATTRIBUTE_PRINTER = UIPrinter.class.getName();

	public TGPrintAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGPrintSettings styles = context.getAttribute(TGPrintSettings.ATTRIBUTE_PRINT_STYLES);
		if( styles == null ) {
			this.configureStyles(context);
			return;
		}
		Integer zoomValue = context.getAttribute(TGPrintSettings.ATTRIBUTE_PRINT_ZOOM);
		if (zoomValue == null) {
			zoomValue = 100;
		}

		UIPrinter printer = context.getAttribute(ATTRIBUTE_PRINTER);
		if( printer == null ) {
			this.configurePrinterData(context);
			return;
		}

		TGSongManager manager = new TGSongManager(new TGFactoryImpl());
		TGSong sourceSong = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGSong targetSong = sourceSong.clone(manager.getFactory());
		UIRectangle printerArea = this.getPrinterArea(printer, 10f);
		UISize pageSize = new UISize(printerArea.getWidth(), printerArea.getHeight());
		UIInset pageMargins = new UIInset(printerArea.getY(), printerArea.getX(), 0, 0);

		TGPrintController controller = new PrintController(this.getContext(), targetSong, manager, printer.getResourceFactory());
		TGPrintLayout printLayout = new TGPrintLayout(controller, styles);
		float scale = (float) zoomValue/100f;
		printLayout.loadStyles(printer.getDpiScale()*scale, printer.getDpiFontScale()*scale);
		printLayout.updateSong();
		printLayout.makeDocument(new PrintDocumentImpl(printLayout, printer, pageSize, pageMargins));
		printLayout.getResourceBuffer().disposeAllResources();
	}

	public void configureStyles(final TGActionContext context) {
		context.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPrintSettingsDialogController());
		context.setAttribute(TGPrintSettingsDialog.ATTRIBUTE_HANDLER, new TGPrintSettingsHandler() {
			public void updatePrintSettings(TGPrintSettings styles, int zoomValue) {
				context.setAttribute(TGPrintSettings.ATTRIBUTE_PRINT_STYLES, styles);
				context.setAttribute(TGPrintSettings.ATTRIBUTE_PRINT_ZOOM, zoomValue);
				executeActionInNewThread(TGPrintAction.NAME, context);
			}
		});
		executeActionInNewThread(TGOpenViewAction.NAME, context);
	}

	public void configurePrinterData(final TGActionContext context) {
		context.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPrinterChooserDialogController());
		context.setAttribute(TGPrinterChooserDialog.ATTRIBUTE_HANDLER, new TGPrinterChooserHandler() {
			public void updatePrinter(UIPrinter printer) {
				context.setAttribute(ATTRIBUTE_PRINTER, printer);
				executeActionInNewThread(TGPrintAction.NAME, context);
			}
		});
		executeActionInNewThread(TGOpenViewAction.NAME, context);
	}

	public void executeActionInNewThread(final String id, final TGActionContext context) {
		new Thread(new Runnable() {
			public void run() {
				TGActionManager.getInstance(getContext()).execute(id, context);
			}
		}).start();
	}

	protected UIRectangle getPrinterArea(UIPrinter printer, float margin) {
		Float scale = printer.getDpiScale();
		Float scaledMargin = (margin * (scale != null ? scale : 1f));
		UIRectangle bounds = printer.getBounds();

		return new UIRectangle(bounds.getX() + scaledMargin, bounds.getY() + scaledMargin, bounds.getWidth() - (scaledMargin * 2f), bounds.getHeight() - (scaledMargin * 2f));
	}

	private class PrintDocumentImpl implements TGPrintDocument{

		private TGPrintPainter painter;
		private TGPrintLayout layout;
		private UIPrinter printer;
		private UIPrinterJob printerJob;
		private UIPrinterPage printerPage;

		private UISize size;
		private UIInset margins;

		public PrintDocumentImpl(TGPrintLayout layout, UIPrinter printer, UISize size, UIInset margins){
			this.layout = layout;
			this.printer = printer;
			this.size = size;
			this.margins = margins;
			this.painter = new TGPrintPainter();
		}

		public UIPainter getPainter() {
			return this.painter;
		}

		public UISize getSize() {
			return this.size;
		}

		public UIInset getMargins() {
			return this.margins;
		}

		public void pageStart() {
			if( this.printerJob != null && !this.printerJob.isDisposed() ){
				this.printerPage = this.printerJob.createPage();
				this.painter.setHandle(this.printerPage.getPainter());
			}
		}

		public void pageFinish() {
			if( this.printerPage != null && !this.printerPage.isDisposed() ){
				this.printerPage.dispose();
			}
		}

		public void start() {
			this.printerJob = this.printer.createJob(getJobName());
		}

		public void finish() {
			if( this.printerJob != null && !this.printerJob.isDisposed() ){
				this.printerJob.dispose();
				this.printerJob = null;
				TGSynchronizer.getInstance(getContext()).executeLater(new Runnable(){
					public void run() {
						dispose();
					}
				});
			}
		}

		public boolean isTransparentBackground() {
			return true;
		}

		public boolean isPaintable(int page){
			Integer startPage = this.printer.getStartPage();
			if( startPage != null && startPage > 0 && startPage > page){
				return false;
			}

			Integer endPage = this.printer.getEndPage();
			if( endPage != null && endPage > 0 && endPage < page){
				return false;
			}
			return true;
		}

		public String getJobName(){
			String prefix = TGApplication.NAME;
			String song = this.layout.getSong().getName();
			return ( song != null && song.length() > 0 ? (prefix + "-" + song) : prefix );
		}

		public void dispose(){
			if(!this.printer.isDisposed()){
				this.printer.dispose();
			}
		}
	}
}