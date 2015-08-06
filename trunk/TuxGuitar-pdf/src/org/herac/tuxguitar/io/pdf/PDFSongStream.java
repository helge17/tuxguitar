package org.herac.tuxguitar.io.pdf;

import java.io.OutputStream;

import org.herac.tuxguitar.app.printer.PrintLayout;
import org.herac.tuxguitar.app.printer.PrintStyles;
import org.herac.tuxguitar.awt.graphics.TGResourceFactoryImpl;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGFactoryImpl;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class PDFSongStream implements TGSongStream {
	
	private static final int PAGE_X = 20;
	private static final int PAGE_Y = 20;
	private static final int PAGE_WIDTH = 550;
	private static final int PAGE_HEIGHT = 800;
	
	private TGContext context;
	private TGSongStreamContext streamContext;
	
	public PDFSongStream(TGContext context, TGSongStreamContext streamContext) {
		this.context = context;
		this.streamContext = streamContext;
	}
	
	public PrintStyles getDefaultStyles(TGSong song){
		PrintStyles styles = new PrintStyles();
		styles.setStyle(TGLayout.DISPLAY_TABLATURE | TGLayout.DISPLAY_MODE_BLACK_WHITE);
		styles.setFromMeasure(1);
		styles.setToMeasure(song.countMeasureHeaders());
		styles.setTrackNumber(1);
		return styles;
	}
	
	public void process() throws TGFileFormatException {
		try{
			OutputStream stream = this.streamContext.getAttribute(OutputStream.class.getName());
			TGSong song = this.streamContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			PrintStyles styles = this.streamContext.getAttribute(PrintStyles.class.getName());
			if( styles == null ) {
				styles = getDefaultStyles(song);
			}
			
			TGSongManager manager = new TGSongManager(new TGFactoryImpl());
			TGSong clonedSong = song.clone(manager.getFactory());
			
			TGResourceFactory factory = new TGResourceFactoryImpl();
			TGController controller = new PDFController(clonedSong, manager, factory);
			
			PrintLayout layout = new PrintLayout(controller, styles);
			
			layout.loadStyles(1f);
			layout.updateSong();
			layout.makeDocument(new PDFDocument(this.context, new TGRectangle(PAGE_X, PAGE_Y, PAGE_WIDTH, PAGE_HEIGHT), stream));
			
			controller.getResourceBuffer().disposeAllResources();
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
}
