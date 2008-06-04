/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.file;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.TGFactoryImpl;
import org.herac.tuxguitar.gui.editors.tab.Tablature;
import org.herac.tuxguitar.gui.editors.tab.layout.PrinterViewLayout;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.printer.PrintDocument;
import org.herac.tuxguitar.gui.printer.PrintPreview;
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
public class PrintPreviewAction extends Action{
	public static final String NAME = "action.file.print-preview";
	
	public PrintPreviewAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		try{
			final PrintStyles data = PrintStylesDialog.open(TuxGuitar.instance().getShell());
			if(data != null){
				TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
				
				this.printPreview(data);
			}
		}catch(Throwable throwable){
			MessageDialog.errorMessage(throwable);
		}
		return 0;
	}
	
	public void printPreview(final PrintStyles data){
		new Thread(new Runnable() {
			public void run() {
				try{
					final TGSongManager manager = new TGSongManager();
					manager.setFactory(new TGFactoryImpl());
					manager.setSong(getSongManager().getSong().clone(manager.getFactory()));
				
					printPreview(manager,data);
				}catch(Throwable throwable){
					MessageDialog.errorMessage(throwable);
				}
			}
		}).start();
	}
	
	public void printPreview(final TGSongManager manager, final PrintStyles data){
		new SyncThread(new Runnable() {
			public void run() {
				try{
					Tablature tablature = new Tablature(TuxGuitar.instance().getShell());
					tablature.setSongManager(manager);
					
					PrinterViewLayout layout = new PrinterViewLayout(tablature,data, 1f);
					
					printPreview( layout );
				}catch(Throwable throwable){
					MessageDialog.errorMessage(throwable);
				}
			}
		}).start();
	}
	
	public void printPreview(final PrinterViewLayout layout){
		new Thread(new Runnable() {
			public void run() {
				try{
					layout.getTablature().updateTablature();
					layout.makeDocument(new PrintDocumentImpl(layout, new Rectangle(0,0,850,1050)));
					//new SyncThread(new Runnable() {
					//	public void run() {
					//		layout.makeDocument(new PrintDocumentImpl(layout, new Rectangle(0,0,850,1050)));
					//	}
					//}).start();
				}catch(Throwable throwable){
					MessageDialog.errorMessage(throwable);
				}
			}
		}).start();
	}
	
	private class PrintDocumentImpl implements PrintDocument{
		
		private PrinterViewLayout layout;
		private TGPainter painter;
		private Rectangle bounds;
		private List pages;
		
		public PrintDocumentImpl(PrinterViewLayout layout, Rectangle bounds){
			this.layout = layout;
			this.bounds = bounds;
			this.painter = new TGPainter();
			this.pages = new ArrayList();
		}
		
		public TGPainter getPainter() {
			return this.painter;
		}
		
		public Rectangle getBounds(){
			return this.bounds;
		}
		
		public void pageStart() {
			Image page = new Image(this.layout.getTablature().getDisplay(),this.bounds.width - this.bounds.x, this.bounds.height - this.bounds.y);
			this.painter.init( page );
			this.pages.add( page );
		}
		
		public void pageFinish() {
			this.painter.dispose();
		}
		
		public void start() {
			// Not implemented
		}
		
		public void finish() {
			final Tablature tablature = this.layout.getTablature();
			final Rectangle bounds = this.bounds;
			final List pages = this.pages;
			try {
				TGSynchronizer.instance().addRunnable(new TGSynchronizer.TGRunnable(){
					public void run() {
						tablature.dispose();
						PrintPreview preview = new PrintPreview(pages,bounds);
						preview.showPreview(getEditor().getTablature().getShell());
						Iterator it = pages.iterator();
						while(it.hasNext()){
							Image image = (Image)it.next();
							image.dispose();
						}
					}
				});
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		public boolean isPaintable(int page) {
			return true;
		}
	}
}
