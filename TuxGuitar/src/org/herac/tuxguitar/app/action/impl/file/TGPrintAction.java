package org.herac.tuxguitar.app.action.impl.file;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.app.graphics.TGResourceFactoryImpl;
import org.herac.tuxguitar.app.printer.PrintController;
import org.herac.tuxguitar.app.printer.PrintDocument;
import org.herac.tuxguitar.app.printer.PrintLayout;
import org.herac.tuxguitar.app.printer.PrintStyles;
import org.herac.tuxguitar.app.util.MessageDialog;
import org.herac.tuxguitar.app.view.dialog.printer.PrintStylesDialog;
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
	
	public TGPrintAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		try{
			final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			final PrintStyles data = PrintStylesDialog.open(TuxGuitar.getInstance().getShell(), song);
			if(data != null){
				PrintDialog dialog = new PrintDialog(TuxGuitar.getInstance().getShell(), SWT.NULL);
				PrinterData printerData = dialog.open();
				
				if (printerData != null) {
//					TuxGuitar.getInstance().loadCursor(SWT.CURSOR_WAIT);
					
					this.print(song, printerData, data);
				}
			}
		}catch(Throwable throwable ){
			MessageDialog.errorMessage(throwable);
		}
	}
	
	public void print(final TGSong srcSong, final PrinterData printerData ,final PrintStyles data){
		try{
			new Thread(new Runnable() {
				public void run() {
					try{
						final TGSongManager manager = new TGSongManager(new TGFactoryImpl());
						final TGSong song = srcSong.clone(manager.getFactory());
						
						TGSynchronizer.getInstance(getContext()).executeLater(new Runnable() {
							public void run() {
								try{
									Printer printer = new Printer(printerData);
									PrintController controller = new PrintController(song, manager, new TGResourceFactoryImpl(printer));
									PrintLayout layout = new PrintLayout(controller,data);
									
									print(printer, printerData, layout , getPrinterArea(printer,0.5f), getPrinterScale(printer) );
								}catch(Throwable throwable ){
									MessageDialog.errorMessage(throwable);
								}
							}
						});
					}catch(Throwable throwable ){
						MessageDialog.errorMessage(throwable);
					}
				}
			}).start();
		}catch(Throwable throwable ){
			MessageDialog.errorMessage(throwable);
		}
	}
	
	protected void print(final Printer printer, final PrinterData printerData ,final PrintLayout layout, final TGRectangle bounds, final float scale){
		new Thread(new Runnable() {
			public void run() {
				try{
					layout.loadStyles(scale, 1f);
					layout.updateSong();
					layout.makeDocument(new PrintDocumentImpl(layout,printer, printerData, bounds));
					layout.getResourceBuffer().disposeAllResources();
				}catch(Throwable throwable ){
					MessageDialog.errorMessage(throwable);
				}
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