package app.tuxguitar.io.pdf;

import app.tuxguitar.graphics.control.TGController;
import app.tuxguitar.graphics.control.TGFactoryImpl;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.graphics.control.TGLayoutStyles;
import app.tuxguitar.graphics.control.print.TGPrintController;
import app.tuxguitar.graphics.control.print.TGPrintLayout;
import app.tuxguitar.graphics.control.print.TGPrintSettings;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.base.TGSongWriterHandle;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.resource.UIInset;
import app.tuxguitar.ui.resource.UIResourceFactory;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.util.TGContext;

public class PDFSongWriter implements TGSongWriter {

	public static final TGFileFormat FILE_FORMAT = new TGFileFormat("PDF", "application/pdf", new String[]{"pdf"});

	private static final int PAGE_WIDTH = 550;
	private static final int PAGE_HEIGHT = 800;
	private static final int MARGIN_TOP = 20;
	private static final int MARGIN_BOTTOM = 20;
	private static final int MARGIN_LEFT = 20;
	private static final int MARGIN_RIGHT = 20;

	private TGContext context;

	public PDFSongWriter(TGContext context) {
		this.context = context;
	}

	public TGFileFormat getFileFormat() {
		return FILE_FORMAT;
	}

	public TGPrintSettings getDefaultStyles(TGSong song){
		TGPrintSettings styles = new TGPrintSettings();
		styles.setStyle(TGLayout.DISPLAY_TABLATURE | TGLayout.DISPLAY_SCORE | TGLayout.DISPLAY_CHORD_DIAGRAM | TGLayout.DISPLAY_CHORD_NAME | TGLayout.DISPLAY_COMPACT | TGLayout.DISPLAY_MODE_BLACK_WHITE);
		styles.setFromMeasure(1);
		styles.setToMeasure(song.countMeasureHeaders());
		styles.setTrackNumber(TGPrintSettings.ALL_TRACKS);
		return styles;
	}

	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try{
			TGPrintSettings settings = handle.getContext().getAttribute(TGPrintSettings.ATTRIBUTE_PRINT_STYLES);
			if( settings == null ) {
				settings = getDefaultStyles(handle.getSong());
			}
			Integer zoomValue = handle.getContext().getAttribute(TGPrintSettings.ATTRIBUTE_PRINT_ZOOM);
			if (zoomValue == null) {
				zoomValue = 100;
			}

			TGLayoutStyles styles = handle.getContext().getAttribute(TGLayoutStyles.class.getName());
			if( styles == null ) {
				styles = new PDFLayoutStyles(this.context);
			}

			TGSongManager manager = new TGSongManager(new TGFactoryImpl());
			TGSong clonedSong = handle.getSong().clone(manager.getFactory());

			UIResourceFactory factory = new PDFResourceFactory();
			TGController controller = new TGPrintController(clonedSong, manager, factory, styles);

			UISize pageSize = new UISize(PAGE_WIDTH, PAGE_HEIGHT);
			UIInset pageMargins = new UIInset(MARGIN_TOP, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_BOTTOM);

			TGPrintLayout layout = new TGPrintLayout(controller, settings);

			layout.loadStyles(((float) zoomValue)/100f);
			layout.updateSong();
			layout.makeDocument(new PDFDocument(this.context, pageSize, pageMargins, handle.getOutputStream()));

			controller.getResourceBuffer().disposeAllResources();
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
}
