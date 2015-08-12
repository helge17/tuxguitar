package org.herac.tuxguitar.app.action.impl.file;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.app.graphics.TGResourceFactoryImpl;
import org.herac.tuxguitar.app.printer.PrintController;
import org.herac.tuxguitar.app.printer.PrintDocument;
import org.herac.tuxguitar.app.printer.PrintLayout;
import org.herac.tuxguitar.app.printer.PrintStyles;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintStylesDialog;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintStylesDialogController;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintStylesHandler;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrinterDataDialog;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrinterDataDialogController;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrinterDataHandler;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.control.TGFactoryImpl;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGPrintAction extends TGActionBase{
	
	public static final String NAME = "action.file.print";
	
	public static final String ATTRIBUTE_STYLES = PrintStyles.class.getName();
	public static final String ATTRIBUTE_DATA = PrinterData.class.getName();
	
	public TGPrintAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		PrintStyles styles = context.getAttribute(ATTRIBUTE_STYLES);
		if( styles == null ) {
			this.configureStyles(context);
			return;
		}
		
		PrinterData printerData = context.getAttribute(ATTRIBUTE_DATA);
		if( printerData == null ) {
			this.configurePrinterData(context);
			return;
		}
		
		TGSongManager manager = new TGSongManager(new TGFactoryImpl());
		TGSong sourceSong = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGSong targetSong = sourceSong.clone(manager.getFactory());
		
		Printer printer = new Printer(printerData);
		PrintController controller = new PrintController(targetSong, manager, new TGResourceFactoryImpl(printer));
		PrintLayout printLayout = new PrintLayout(controller, styles);
		printLayout.loadStyles(getPrinterScale(printer), 1f);
		printLayout.updateSong();
		printLayout.makeDocument(new PrintDocumentImpl(printLayout, printer, printerData, getPrinterArea(printer, 0.5f)));
		printLayout.getResourceBuffer().disposeAllResources();
	}
	
	public void configureStyles(final TGActionContext context) {
		context.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPrintStylesDialogController());
		context.setAttribute(TGPrintStylesDialog.ATTRIBUTE_HANDLER, new TGPrintStylesHandler() {
			public void updatePrintStyles(PrintStyles styles) {
				context.setAttribute(ATTRIBUTE_STYLES, styles);
				executeActionInNewThread(TGPrintAction.NAME, context);
			}
		});
		executeActionInNewThread(TGOpenViewAction.NAME, context);
	}
	
	public void configurePrinterData(final TGActionContext context) {
		context.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPrinterDataDialogController());
		context.setAttribute(TGPrinterDataDialog.ATTRIBUTE_HANDLER, new TGPrinterDataHandler() {
			public void updatePrinterData(PrinterData printerData) {
				context.setAttribute(ATTRIBUTE_DATA, printerData);
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
	
	protected TGRectangle getPrinterArea(Printer printer, float margin) {
		Rectangle clientArea = printer.getClientArea();
		Rectangle trim = printer.computeTrim(0, 0, 0, 0);
		Point dpi = printer.getDPI();
		
		float x = (trim.x + (dpi.x * margin));
		float y = (trim.y + (dpi.y * margin));
		float width = ((clientArea.width + trim.width) - (dpi.x * margin));
		float height = ((clientArea.height + trim.height) - (dpi.y * margin));
		
		return new TGRectangle(x, y, width, height);
	}
	
	protected float getPrinterScale(Printer printer) {
		Point dpi = printer.getDPI();
		if( dpi != null ){
			return ( dpi.x / 100.0f );
		}
		return 1.0f;
	}
	
	private class PrintDocumentImpl implements PrintDocument{
		
		private Printer printer;
		private PrinterData printerData;
		private PrintLayout layout;
		private TGPainterImpl painter;
		private TGRectangle bounds;
		private boolean started;
		
		public PrintDocumentImpl(PrintLayout layout, Printer printer,PrinterData printerData, TGRectangle bounds){
			this.layout = layout;
			this.printer = printer;
			this.printerData = printerData;
			this.bounds = bounds;
			this.painter = new TGPainterImpl();
		}
		
		public TGPainter getPainter() {
			return this.painter;
		}
		
		public TGRectangle getBounds(){
			return this.bounds;
		}
		
		public void pageStart() {
			if(this.started){
				this.printer.startPage();
				this.painter.init(new GC(this.printer));
			}
		}
		
		public void pageFinish() {
			if(this.started){
				this.painter.dispose();
				this.printer.endPage();
			}
		}
		
		public void start() {
			this.started = this.printer.startJob(getJobName());
		}
		
		public void finish() {
			if(this.started){
				this.printer.endJob();
				this.started = false;
				TGSynchronizer.getInstance(getContext()).executeLater(new Runnable(){
					public void run() {
						dispose();
					}
				});
			}
		}
		
		public boolean isPaintable(int page){
			if(this.printerData.scope == PrinterData.PAGE_RANGE){
				if(this.printerData.startPage > 0 && this.printerData.startPage > page){
					return false;
				}
				if(this.printerData.endPage > 0 && this.printerData.endPage < page){
					return false;
				}
			}
			return true;
		}
		
		public String getJobName(){
			String prefix = TuxGuitar.APPLICATION_NAME;
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