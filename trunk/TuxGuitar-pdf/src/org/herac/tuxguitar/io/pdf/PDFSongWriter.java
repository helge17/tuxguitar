package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.app.printer.PrintLayout;
import org.herac.tuxguitar.app.printer.PrintStyles;
import org.herac.tuxguitar.awt.graphics.TGResourceFactoryImpl;
import org.herac.tuxguitar.graphics.TGDimension;
import org.herac.tuxguitar.graphics.TGMargins;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGFactoryImpl;
import org.herac.tuxguitar.graphics.control.TGLayout;
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
	
	public PrintStyles getDefaultStyles(TGSong song){
		PrintStyles styles = new PrintStyles();
		styles.setStyle(TGLayout.DISPLAY_TABLATURE | TGLayout.DISPLAY_MODE_BLACK_WHITE);
		styles.setFromMeasure(1);
		styles.setToMeasure(song.countMeasureHeaders());
		styles.setTrackNumber(1);
		return styles;
	}
	
	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try{
			PrintStyles styles = handle.getContext().getAttribute(PrintStyles.class.getName());
			if( styles == null ) {
				styles = getDefaultStyles(handle.getSong());
			}
			
			TGSongManager manager = new TGSongManager(new TGFactoryImpl());
			TGSong clonedSong = handle.getSong().clone(manager.getFactory());
			
			TGResourceFactory factory = new TGResourceFactoryImpl();
			TGController controller = new PDFController(clonedSong, manager, factory);
			
			TGDimension pageSize = new TGDimension(PAGE_WIDTH, PAGE_HEIGHT);
			TGMargins pageMargins = new TGMargins(MARGIN_TOP, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_BOTTOM);
			
			PrintLayout layout = new PrintLayout(controller, styles);
			
			layout.loadStyles(1f);
			layout.updateSong();
			layout.makeDocument(new PDFDocument(this.context, pageSize, pageMargins, handle.getOutputStream()));
			
			controller.getResourceBuffer().disposeAllResources();
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
}
