package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.graphics.TGDimension;
import org.herac.tuxguitar.graphics.TGMargins;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGFactoryImpl;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;
import org.herac.tuxguitar.graphics.control.print.TGPrintController;
import org.herac.tuxguitar.graphics.control.print.TGPrintLayout;
import org.herac.tuxguitar.graphics.control.print.TGPrintSettings;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.base.TGSongWriterHandle;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

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
			TGPrintSettings settings = handle.getContext().getAttribute(TGPrintSettings.class.getName());
			if( settings == null ) {
				settings = getDefaultStyles(handle.getSong());
			}
			
			TGLayoutStyles styles = handle.getContext().getAttribute(TGLayoutStyles.class.getName());
			if( styles == null ) {
				styles = new PDFLayoutStyles();
			}
			
			TGSongManager manager = new TGSongManager(new TGFactoryImpl());
			TGSong clonedSong = handle.getSong().clone(manager.getFactory());
			
			TGResourceFactory factory = new PDFResourceFactory();
			TGController controller = new TGPrintController(clonedSong, manager, factory, styles);
			
			TGDimension pageSize = new TGDimension(PAGE_WIDTH, PAGE_HEIGHT);
			TGMargins pageMargins = new TGMargins(MARGIN_TOP, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_BOTTOM);
			
			TGPrintLayout layout = new TGPrintLayout(controller, settings);
			
			layout.loadStyles(1f);
			layout.updateSong();
			layout.makeDocument(new PDFDocument(this.context, pageSize, pageMargins, handle.getOutputStream()));
			
			controller.getResourceBuffer().disposeAllResources();
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
}
