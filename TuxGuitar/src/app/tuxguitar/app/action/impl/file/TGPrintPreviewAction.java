package app.tuxguitar.app.action.impl.file;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.printer.PrintController;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.dialog.printer.TGPrintPreviewDialog;
import app.tuxguitar.app.view.dialog.printer.TGPrintPreviewDialogController;
import app.tuxguitar.app.view.dialog.printer.TGPrintSettingsDialog;
import app.tuxguitar.app.view.dialog.printer.TGPrintSettingsDialogController;
import app.tuxguitar.app.view.dialog.printer.TGPrintSettingsHandler;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.graphics.control.TGFactoryImpl;
import app.tuxguitar.graphics.control.print.TGPrintController;
import app.tuxguitar.graphics.control.print.TGPrintDocument;
import app.tuxguitar.graphics.control.print.TGPrintLayout;
import app.tuxguitar.graphics.control.print.TGPrintPainter;
import app.tuxguitar.graphics.control.print.TGPrintSettings;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIInset;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.util.TGContext;

public class TGPrintPreviewAction extends TGActionBase{

	public static final String NAME = "action.file.print-preview";

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
		TGPrintSettings styles = context.getAttribute(TGPrintSettings.ATTRIBUTE_PRINT_STYLES);
		if( styles == null ) {
			this.configureStyles(context);

			return;
		}
		Integer zoomValue = context.getAttribute(TGPrintSettings.ATTRIBUTE_PRINT_ZOOM);
		if (zoomValue == null) {
			zoomValue = 100;
		}

		TGSongManager manager = new TGSongManager(new TGFactoryImpl());
		TGSong sourceSong = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGSong targetSong = sourceSong.clone(manager.getFactory());
		UISize pageSize = new UISize(PAGE_WIDTH, PAGE_HEIGHT);
		UIInset pageMargins = new UIInset(MARGIN_TOP, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_BOTTOM);

		TGPrintController controller = new PrintController(this.getContext(), targetSong, manager, this.getUIFactory());
		TGPrintLayout printLayout = new TGPrintLayout(controller, styles);
		printLayout.loadStyles(((float)zoomValue)/100f);
		printLayout.updateSong();
		printLayout.makeDocument(new PrintDocumentImpl(pageSize, pageMargins));
		printLayout.getResourceBuffer().disposeAllResources();
	}

	public void configureStyles(final TGActionContext context) {
		context.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPrintSettingsDialogController());
		context.setAttribute(TGPrintSettingsDialog.ATTRIBUTE_HANDLER, new TGPrintSettingsHandler() {
			public void updatePrintSettings(TGPrintSettings styles, int zoomValue) {
				context.setAttribute(TGPrintSettings.ATTRIBUTE_PRINT_STYLES, styles);
				context.setAttribute(TGPrintSettings.ATTRIBUTE_PRINT_ZOOM, zoomValue);
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

		private TGPrintPainter painter;
		private UISize size;
		private UIInset margins;
		private List<UIImage> pages;

		public PrintDocumentImpl(UISize size, UIInset margins){
			this.size = size;
			this.margins = margins;
			this.pages = new ArrayList<UIImage>();
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
			final UISize size = this.size;
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
