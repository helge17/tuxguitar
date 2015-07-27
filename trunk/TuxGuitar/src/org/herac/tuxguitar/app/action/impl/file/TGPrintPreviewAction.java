package org.herac.tuxguitar.app.action.impl.file;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.app.graphics.TGResourceFactoryImpl;
import org.herac.tuxguitar.app.printer.PrintController;
import org.herac.tuxguitar.app.printer.PrintDocument;
import org.herac.tuxguitar.app.printer.PrintLayout;
import org.herac.tuxguitar.app.printer.PrintStyles;
import org.herac.tuxguitar.app.util.MessageDialog;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.dialog.printer.PrintPreview;
import org.herac.tuxguitar.app.view.dialog.printer.PrintStylesDialog;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGFactoryImpl;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGPrintPreviewAction extends TGActionBase{
	
	public static final String NAME = "action.file.print-preview";
	
	public TGPrintPreviewAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		try{
			final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			final PrintStyles data = PrintStylesDialog.open(TuxGuitar.getInstance().getShell(), song);
			if(data != null){
//				TuxGuitar.getInstance().loadCursor(SWT.CURSOR_WAIT);
				
				this.printPreview(song, data);
			}
		}catch(Throwable throwable){
			MessageDialog.errorMessage(throwable);
		}
	}
	
	public void printPreview(final TGSong srcSong, final PrintStyles data){
		new Thread(new Runnable() {
			public void run() {
				try{
					final TGSongManager manager = new TGSongManager(new TGFactoryImpl(getContext()));
					final TGSong song = srcSong.clone(manager.getFactory());
				
					printPreview(song, manager, data);
				}catch(Throwable throwable){
					MessageDialog.errorMessage(throwable);
				}
			}
		}).start();
	}
	
	public void printPreview(final TGSong song, final TGSongManager manager, final PrintStyles data){
		TGSynchronizer.getInstance(getContext()).executeLater(new Runnable() {
			public void run() {
				try{
					TGResourceFactory factory = new TGResourceFactoryImpl(TuxGuitar.getInstance().getDisplay());
					PrintController controller = new PrintController(song, manager, factory);
					PrintLayout layout = new PrintLayout(controller,data);
					
					printPreview( layout );
				}catch(Throwable throwable){
					MessageDialog.errorMessage(throwable);
				}
			}
		});
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
		private List<Image> pages;
		
		public PrintDocumentImpl(TGRectangle bounds){
			this.bounds = bounds;
			this.painter = new TGPainterImpl();
			this.pages = new ArrayList<Image>();
		}
		
		public TGPainter getPainter() {
			return this.painter;
		}
		
		public TGRectangle getBounds(){
			return this.bounds;
		}
		
		public void pageStart() {
			int width = Math.round(this.bounds.getWidth() - this.bounds.getX());
			int height = Math.round(this.bounds.getHeight() - this.bounds.getY());
			Image page = new Image(TuxGuitar.getInstance().getDisplay(), width, height);
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
			final List<Image> pages = this.pages;
			
			TGSynchronizer.getInstance(getContext()).executeLater(new Runnable(){
				public void run() throws TGException {
					PrintPreview preview = new PrintPreview(pages,bounds);
					preview.showPreview(TablatureEditor.getInstance(getContext()).getTablature().getShell());
					Iterator<Image> it = pages.iterator();
					while(it.hasNext()){
						Image image = (Image)it.next();
						image.dispose();
					}
				}
			});
		}
		
		public boolean isPaintable(int page) {
			return true;
		}
	}
}
