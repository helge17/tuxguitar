package app.tuxguitar.io.image;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.printer.PrintController;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.graphics.control.TGFactoryImpl;
import app.tuxguitar.graphics.control.print.TGPrintController;
import app.tuxguitar.graphics.control.print.TGPrintDocument;
import app.tuxguitar.graphics.control.print.TGPrintLayout;
import app.tuxguitar.graphics.control.print.TGPrintPainter;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongStream;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIInset;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIResourceFactory;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.error.TGErrorManager;

public class ImageExporterStream implements TGSongStream{

	private static final int PAGE_WIDTH = 550;
	private static final int PAGE_HEIGHT = 800;
	private static final int MARGIN_TOP = 20;
	private static final int MARGIN_BOTTOM = 20;
	private static final int MARGIN_LEFT = 20;
	private static final int MARGIN_RIGHT = 20;

	private TGContext context;
	private TGSongStreamContext streamContext;

	public ImageExporterStream(TGContext context, TGSongStreamContext streamContext) {
		this.context = context;
		this.streamContext = streamContext;
	}

	public void process() throws TGFileFormatException {
		ImageExporterSettings settings = this.streamContext.getAttribute(ImageExporterSettings.class.getName());
		if( settings != null ){
			TGSong song = this.streamContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			TGSongManager manager = new TGSongManager(new TGFactoryImpl());
			TGSong clonedSong = song.clone(manager.getFactory());
			UISize pageSize = new UISize(PAGE_WIDTH, PAGE_HEIGHT);
			UIInset pageMargins = new UIInset(MARGIN_TOP, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_BOTTOM);

			UIResourceFactory factory = getUIFactory();

			TGPrintController controller = new PrintController(this.context, clonedSong, manager, factory);
			TGPrintLayout layout = new TGPrintLayout(controller, settings.getStyles());

			layout.loadStyles(1f);
			layout.updateSong();
			layout.makeDocument(new PrintDocumentImpl(pageSize, pageMargins, settings.getFormat(), settings.getPath() ));
		}
	}

	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}

	private class PrintDocumentImpl implements TGPrintDocument{

		private TGPrintPainter painter;
		private UISize size;
		private UIInset margins;
		private String path;
		private List<UIImage> pages;
		private ImageFormat format;

		public PrintDocumentImpl(UISize size, UIInset margins, ImageFormat format, String path){
			this.size = size;
			this.margins = margins;
			this.path = path;
			this.painter = new TGPrintPainter();
			this.pages = new ArrayList<UIImage>();
			this.format = format;
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
			int width = Math.round(this.size.getWidth());
			int height = Math.round(this.size.getHeight());

			UIFactory factory = getUIFactory();
			UIImage page = factory.createImage(width, height);
			this.painter.setHandle(page.createPainter());
			this.pages.add( page );
		}

		public void pageFinish() {
			this.painter.dispose();
		}

		public void start() {
			// Not implemented
		}

		public void finish() {
			try {
				ImageWriter.write(this.format, this.path, this.pages);

				for(UIImage uiImage : this.pages) {
					uiImage.dispose();
				}
			} catch (Throwable throwable) {
				TGErrorManager.getInstance(ImageExporterStream.this.context).handleError(throwable);
			}
		}

		public boolean isTransparentBackground() {
			return true;
		}

		public boolean isPaintable(int page) {
			return true;
		}
	}
}
