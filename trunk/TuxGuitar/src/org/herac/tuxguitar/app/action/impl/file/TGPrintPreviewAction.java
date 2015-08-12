package org.herac.tuxguitar.app.action.impl.file;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
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
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintPreviewDialog;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintPreviewDialogController;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintStylesDialog;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintStylesDialogController;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintStylesHandler;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGFactoryImpl;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGPrintPreviewAction extends TGActionBase{
	
	public static final String NAME = "action.file.print-preview";
	
	public static final String ATTRIBUTE_STYLES = PrintStyles.class.getName();
	
	public TGPrintPreviewAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		PrintStyles styles = context.getAttribute(ATTRIBUTE_STYLES);
		if( styles == null ) {
			this.configureStyles(context);
			
			return;
		}
		
		TGSongManager manager = new TGSongManager(new TGFactoryImpl());
		TGSong sourceSong = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGSong targetSong = sourceSong.clone(manager.getFactory());
		
		TGResourceFactory factory = new TGResourceFactoryImpl(TuxGuitar.getInstance().getDisplay());
		PrintController controller = new PrintController(targetSong, manager, factory);
		PrintLayout printLayout = new PrintLayout(controller, styles);
		printLayout.loadStyles(1f);
		printLayout.updateSong();
		printLayout.makeDocument(new PrintDocumentImpl(new TGRectangle(0, 0, 850, 1050)));
		printLayout.getResourceBuffer().disposeAllResources();
	}
	
	public void configureStyles(final TGActionContext context) {
		context.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPrintStylesDialogController());
		context.setAttribute(TGPrintStylesDialog.ATTRIBUTE_HANDLER, new TGPrintStylesHandler() {
			public void updatePrintStyles(final PrintStyles styles) {
				context.setAttribute(ATTRIBUTE_STYLES, styles);
				executeActionInNewThread(TGPrintPreviewAction.NAME, context);
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
			// nothing to do
		}
		
		public void finish() {
			final TGRectangle bounds = this.bounds;
			final List<Image> pages = this.pages;
			
			TGActionProcessor tgActionProcessor = new TGActionProcessor(getContext(), TGOpenViewAction.NAME);
			tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPrintPreviewDialogController());
			tgActionProcessor.setAttribute(TGPrintPreviewDialog.ATTRIBUTE_PAGES, pages);
			tgActionProcessor.setAttribute(TGPrintPreviewDialog.ATTRIBUTE_BOUNDS, bounds);
			tgActionProcessor.setAttribute(TGViewContext.ATTRIBUTE_DISPOSE_LISTENER, new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					Iterator<Image> it = pages.iterator();
					while(it.hasNext()){
						Image image = (Image)it.next();
						image.dispose();
					}
				}
			});
			
			tgActionProcessor.process();
		}
		
		public boolean isPaintable(int page) {
			return true;
		}
	}
}
