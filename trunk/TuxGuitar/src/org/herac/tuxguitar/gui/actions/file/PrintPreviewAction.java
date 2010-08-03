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
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGFactoryImpl;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.TGPainterImpl;
import org.herac.tuxguitar.gui.editors.TGResourceFactoryImpl;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.printer.PrintController;
import org.herac.tuxguitar.gui.printer.PrintDocument;
import org.herac.tuxguitar.gui.printer.PrintPreview;
import org.herac.tuxguitar.gui.printer.PrintStyles;
import org.herac.tuxguitar.gui.printer.PrintStylesDialog;
import org.herac.tuxguitar.gui.printer.PrintLayout;
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
					TGResourceFactory factory = new TGResourceFactoryImpl(TuxGuitar.instance().getDisplay());
					PrintController controller = new PrintController(manager, factory);
					PrintLayout layout = new PrintLayout(controller,data);
					
					printPreview( layout );
				}catch(Throwable throwable){
					MessageDialog.errorMessage(throwable);
				}
			}
		}).start();
	}
	
	public void printPreview(final PrintLayout layout){
		new Thread(new Runnable() {
			public void run() {
				try{
					layout.loadStyles(1f);
					layout.updateSong();
					layout.makeDocument(new PrintDocumentImpl(new TGRectangle(0,0,850,1050)));
				}catch(Throwable throwable){
					MessageDialog.errorMessage(throwable);
				}
			}
		}).start();
	}
	
	private class PrintDocumentImpl implements PrintDocument{
		
		private TGPainterImpl painter;
		private TGRectangle bounds;
		private List pages;
		
		public PrintDocumentImpl(TGRectangle bounds){
			this.bounds = bounds;
			this.painter = new TGPainterImpl();
			this.pages = new ArrayList();
		}
		
		public TGPainter getPainter() {
			return this.painter;
		}
		
		public TGRectangle getBounds(){
			return this.bounds;
		}
		
		public void pageStart() {
			Image page = new Image(TuxGuitar.instance().getDisplay(),this.bounds.getWidth() - this.bounds.getX(), this.bounds.getHeight() - this.bounds.getY());
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
			final TGRectangle bounds = this.bounds;
			final List pages = this.pages;
			try {
				TGSynchronizer.instance().addRunnable(new TGSynchronizer.TGRunnable(){
					public void run() {
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
