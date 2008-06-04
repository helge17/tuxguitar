/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.file;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.TGFactoryImpl;
import org.herac.tuxguitar.gui.editors.tab.Tablature;
import org.herac.tuxguitar.gui.editors.tab.layout.PrinterViewLayout;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.printer.PrintDocument;
import org.herac.tuxguitar.gui.printer.PrintStyles;
import org.herac.tuxguitar.gui.printer.PrintStylesDialog;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrintAction extends Action{
	public static final String NAME = "action.file.print";
	
	public PrintAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		try{
			final PrintStyles data = PrintStylesDialog.open(TuxGuitar.instance().getShell());
			if(data != null){
				PrintDialog dialog = new PrintDialog(TuxGuitar.instance().getShell(), SWT.NULL);
				PrinterData printerData = dialog.open();
				
				if (printerData != null) {
					TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
					
					this.print(printerData, data);
				}
			}
		}catch(Throwable throwable ){
			MessageDialog.errorMessage(throwable);
		}
		return 0;
	}
	
	public void print(final PrinterData printerData ,final PrintStyles data){
		try{
			new Thread(new Runnable() {
				public void run() {
					try{
						final TGSongManager manager = new TGSongManager();
						manager.setFactory(new TGFactoryImpl());
						manager.setSong(getSongManager().getSong().clone(manager.getFactory()));
						
						new SyncThread(new Runnable() {
							public void run() {
								try{
									Shell shell = new Shell();
									Printer printer = new Printer(printerData);
									
									Tablature tablature = new Tablature(shell);
									tablature.setSongManager(manager);
									
									Rectangle bounds = getPrinterArea(printer,0.5);
									
									PrinterViewLayout layout = new PrinterViewLayout(tablature,data, getPrinterScale(printer));
									
									print(printer, printerData, layout , bounds);
								}catch(Throwable throwable ){
									MessageDialog.errorMessage(throwable);
								}
							}
						}).start();
					}catch(Throwable throwable ){
						MessageDialog.errorMessage(throwable);
					}
				}
			}).start();
		}catch(Throwable throwable ){
			MessageDialog.errorMessage(throwable);
		}
	}
	
	protected void print(final Printer printer,final PrinterData printerData ,final PrinterViewLayout layout, final Rectangle bounds){
		new Thread(new Runnable() {
			public void run() {
				try{
					layout.getTablature().updateTablature();
					layout.makeDocument(new PrintDocumentImpl(layout,printer, printerData, bounds));
					//new SyncThread(new Runnable() {
					//	public void run() {
					//		try{
					//			layout.makeDocument(new PrintDocumentImpl(layout,printer, bounds));
					//		}catch(Throwable throwable ){
					//			MessageDialog.errorMessage(throwable);
					//		}
					//	}
					//}).start();
				}catch(Throwable throwable ){
					MessageDialog.errorMessage(throwable);
				}
			}
		}).start();
	}
	
	protected Rectangle getPrinterArea(Printer printer,double margin) {
		Rectangle clientArea = printer.getClientArea();
		Rectangle trim = printer.computeTrim(0, 0, 0, 0);
		Point dpi = printer.getDPI();
		
		int x = (int) (margin * dpi.x) - trim.x;
		int y = (int) (margin * dpi.y) - trim.y;
		int width = clientArea.width + trim.width - (int) (margin * dpi.x) - trim.x;
		int height = clientArea.height + trim.height - (int) (margin * dpi.y) - trim.y;
		
		return new Rectangle(x,y,width,height);
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
		private PrinterViewLayout layout;
		private TGPainter painter;
		private Rectangle bounds;
		private boolean started;
		
		public PrintDocumentImpl(PrinterViewLayout layout, Printer printer,PrinterData printerData, Rectangle bounds){
			this.layout = layout;
			this.printer = printer;
			this.printerData = printerData;
			this.bounds = bounds;
			this.painter = new TGPainter();
		}
		
		public TGPainter getPainter() {
			return this.painter;
		}
		
		public Rectangle getBounds(){
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
				try {
					TGSynchronizer.instance().addRunnable(new TGSynchronizer.TGRunnable(){
						public void run() {
							dispose();
						}
					});
				} catch (Throwable e) {
					e.printStackTrace();
				}
				TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
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
			String song = this.layout.getSongManager().getSong().getName();
			return ( song != null && song.length() > 0 ? (prefix + "-" + song) : prefix );
		}
		
		public void dispose(){
			if(!this.printer.isDisposed()){
				this.printer.dispose();
			}
			if(!this.layout.getTablature().isDisposed() && !this.layout.getTablature().getShell().isDisposed()){
				this.layout.getTablature().getShell().dispose();
			}
			if(!this.layout.getTablature().isDisposed()){
				this.layout.getTablature().dispose();
			}
		}
	}
}