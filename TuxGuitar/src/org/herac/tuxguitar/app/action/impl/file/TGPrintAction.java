package org.herac.tuxguitar.app.action.impl.file;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.app.graphics.TGResourceFactoryImpl;
import org.herac.tuxguitar.app.printer.PrintController;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintSettingsDialog;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintSettingsDialogController;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintSettingsHandler;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrinterChooserDialog;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrinterChooserDialogController;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrinterChooserHandler;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.TGDimension;
import org.herac.tuxguitar.graphics.TGMargins;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.control.TGFactoryImpl;
import org.herac.tuxguitar.graphics.control.print.TGPrintController;
import org.herac.tuxguitar.graphics.control.print.TGPrintDocument;
import org.herac.tuxguitar.graphics.control.print.TGPrintLayout;
import org.herac.tuxguitar.graphics.control.print.TGPrintSettings;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.printer.UIPrinter;
import org.herac.tuxguitar.ui.printer.UIPrinterJob;
import org.herac.tuxguitar.ui.printer.UIPrinterPage;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGPrintAction extends TGActionBase{
	
	public static final String NAME = "action.file.print";
	
	public static final String ATTRIBUTE_STYLES = TGPrintSettings.class.getName();
	public static final String ATTRIBUTE_PRINTER = UIPrinter.class.getName();
	
	public TGPrintAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGPrintSettings styles = context.getAttribute(ATTRIBUTE_STYLES);
		if( styles == null ) {
			this.configureStyles(context);
			return;
		}
		
		UIPrinter printer = context.getAttribute(ATTRIBUTE_PRINTER);
		if( printer == null ) {
			this.configurePrinterData(context);
			return;
		}
		
		TGSongManager manager = new TGSongManager(new TGFactoryImpl());
		TGSong sourceSong = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGSong targetSong = sourceSong.clone(manager.getFactory());
		TGRectangle printerArea = this.getPrinterArea(printer, 10f);
		TGDimension pageSize = new TGDimension(printerArea.getWidth(), printerArea.getHeight());
		TGMargins pageMargins = new TGMargins(printerArea.getY(), printerArea.getX(), 0, 0);
		
		TGPrintController controller = new PrintController(this.getContext(), targetSong, manager, new TGResourceFactoryImpl(printer.getResourceFactory()));
		TGPrintLayout printLayout = new TGPrintLayout(controller, styles);
		printLayout.loadStyles(printer.getDpiScale(), 1f);
		printLayout.updateSong();
		printLayout.makeDocument(new PrintDocumentImpl(printLayout, printer, pageSize, pageMargins));
		printLayout.getResourceBuffer().disposeAllResources();
	}
	
	public void configureStyles(final TGActionContext context) {
		context.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPrintSettingsDialogController());
		context.setAttribute(TGPrintSettingsDialog.ATTRIBUTE_HANDLER, new TGPrintSettingsHandler() {
			public void updatePrintSettings(TGPrintSettings styles) {
				context.setAttribute(ATTRIBUTE_STYLES, styles);
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
	
	protected TGRectangle getPrinterArea(UIPrinter printer, float margin) {
		Float scale = printer.getDpiScale();
		Float scaledMargin = (margin * (scale != null ? scale : 1f));
		UIRectangle bounds = printer.getBounds();
		
		return new TGRectangle(bounds.getX() + scaledMargin, bounds.getY() + scaledMargin, bounds.getWidth() - (scaledMargin * 2f), bounds.getHeight() - (scaledMargin * 2f));
	}
	
	private class PrintDocumentImpl implements TGPrintDocument{
		
		private TGPrintLayout layout;
		private UIPrinter printer;
		private UIPrinterJob printerJob;
		private UIPrinterPage printerPage;
		
		private TGPainterImpl painter;
		private TGDimension size;
		private TGMargins margins;
		
		public PrintDocumentImpl(TGPrintLayout layout, UIPrinter printer, TGDimension size, TGMargins margins){
			this.layout = layout;
			this.printer = printer;
			this.size = size;
			this.margins = margins;
			this.painter = new TGPainterImpl(printer.getResourceFactory());
		}
		
		public TGPainter getPainter() {
			return this.painter;
		}
		
		public TGDimension getSize() {
			return this.size;
		}

		public TGMargins getMargins() {
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