package org.herac.tuxguitar.app.action.impl.file;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.app.graphics.TGResourceFactoryImpl;
import org.herac.tuxguitar.app.printer.PrintController;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintPreviewDialog;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintPreviewDialogController;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintSettingsDialog;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintSettingsDialogController;
import org.herac.tuxguitar.app.view.dialog.printer.TGPrintSettingsHandler;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.graphics.TGDimension;
import org.herac.tuxguitar.graphics.TGMargins;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGFactoryImpl;
import org.herac.tuxguitar.graphics.control.print.TGPrintController;
import org.herac.tuxguitar.graphics.control.print.TGPrintDocument;
import org.herac.tuxguitar.graphics.control.print.TGPrintLayout;
import org.herac.tuxguitar.graphics.control.print.TGPrintSettings;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.util.TGContext;

public class TGPrintPreviewAction extends TGActionBase{
	
	public static final String NAME = "action.file.print-preview";
	
	public static final String ATTRIBUTE_STYLES = TGPrintSettings.class.getName();
	
	private static final int PAGE_WIDTH = 850;
	private static final int PAGE_HEIGHT = 1050;
	private static final int MARGIN_TOP = 20;
	private static final int MARGIN_BOTTOM = 40;
	private static final int MARGIN_LEFT = 50;
	private static final int MARGIN_RIGHT = 20;
	
	public TGPrintPreviewAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		TGPrintSettings styles = context.getAttribute(ATTRIBUTE_STYLES);
		if( styles == null ) {
			this.configureStyles(context);
			
			return;
		}
		
		TGSongManager manager = new TGSongManager(new TGFactoryImpl());
		TGSong sourceSong = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGSong targetSong = sourceSong.clone(manager.getFactory());
		TGDimension pageSize = new TGDimension(PAGE_WIDTH, PAGE_HEIGHT);
		TGMargins pageMargins = new TGMargins(MARGIN_TOP, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_BOTTOM);
		
		TGResourceFactory factory = new TGResourceFactoryImpl(getUIFactory());
		TGPrintController controller = new PrintController(this.getContext(), targetSong, manager, factory);
		TGPrintLayout printLayout = new TGPrintLayout(controller, styles);
		printLayout.loadStyles(1f);
		printLayout.updateSong();
		printLayout.makeDocument(new PrintDocumentImpl(pageSize, pageMargins));
		printLayout.getResourceBuffer().disposeAllResources();
	}
	
	public void configureStyles(final TGActionContext context) {
		context.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPrintSettingsDialogController());
		context.setAttribute(TGPrintSettingsDialog.ATTRIBUTE_HANDLER, new TGPrintSettingsHandler() {
			public void updatePrintSettings(TGPrintSettings styles) {
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
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.getContext()).getFactory();
	}
	
	private class PrintDocumentImpl implements TGPrintDocument {
		
		private TGPainterImpl painter;
		private TGDimension size;
		private TGMargins margins;
		private List<UIImage> pages;
		
		public PrintDocumentImpl(TGDimension size, TGMargins margins){
			this.size = size;
			this.margins = margins;
			this.painter = new TGPainterImpl(getUIFactory());
			this.pages = new ArrayList<UIImage>();
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
			UIFactory factory = getUIFactory();
			UIImage page = factory.createImage(this.size.getWidth(), this.size.getHeight());
			this.painter.setHandle(page.createPainter());
			this.pages.add( page );
		}
		
		public void pageFinish() {
			this.painter.dispose();
		}
		
		public void start() {
			// nothing to do
		}
		
		public void finish() {
			final TGDimension size = this.size;
			final List<UIImage> pages = this.pages;
			
			TGActionProcessor tgActionProcessor = new TGActionProcessor(getContext(), TGOpenViewAction.NAME);
			tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPrintPreviewDialogController());
			tgActionProcessor.setAttribute(TGPrintPreviewDialog.ATTRIBUTE_PAGES, pages);
			tgActionProcessor.setAttribute(TGPrintPreviewDialog.ATTRIBUTE_SIZE, size);
			tgActionProcessor.setAttribute(TGViewContext.ATTRIBUTE_DISPOSE_LISTENER, new UIDisposeListener() {
				public void onDispose(UIDisposeEvent event) {
					for(UIImage uiImage : pages) {
						uiImage.dispose();
					}
				}
			});
			
			tgActionProcessor.process();
		}
		
		public boolean isTransparentBackground() {
			return false;
		}
		
		public boolean isPaintable(int page) {
			return true;
		}
	}
}
