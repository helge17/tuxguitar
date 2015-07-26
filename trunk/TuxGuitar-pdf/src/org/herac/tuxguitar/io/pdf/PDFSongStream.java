package org.herac.tuxguitar.io.pdf;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.app.graphics.TGResourceFactoryImpl;
import org.herac.tuxguitar.app.printer.PrintController;
import org.herac.tuxguitar.app.printer.PrintDocument;
import org.herac.tuxguitar.app.printer.PrintLayout;
import org.herac.tuxguitar.app.printer.PrintStyles;
import org.herac.tuxguitar.app.util.MessageDialog;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGFactoryImpl;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;

public class PDFSongStream implements TGSongStream {
	
	private static final int PAGE_WIDTH = 550;
	private static final int PAGE_HEIGHT = 800;
	
	private TGSongStreamContext context;
	
	public PDFSongStream(TGSongStreamContext context) {
		this.context = context;
	}
	
	public PrintStyles getDefaultStyles(TGSong song){
		PrintStyles styles = new PrintStyles();
		styles.setStyle(TGLayout.DISPLAY_TABLATURE);
		styles.setFromMeasure(1);
		styles.setToMeasure(song.countMeasureHeaders());
		styles.setTrackNumber(1);
		styles.setBlackAndWhite(false);
		return styles;
	}
	
	public void process() throws TGFileFormatException {
		try{
			OutputStream stream = this.context.getAttribute(OutputStream.class.getName());
			TGSong song = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			PrintStyles styles = this.context.getAttribute(PrintStyles.class.getName());
			if( styles == null ) {
				styles = getDefaultStyles(song);
			}
			
			TGSongManager manager = new TGSongManager(new TGFactoryImpl());
			TGSong clonedSong = song.clone(manager.getFactory());
			
			TGResourceFactory factory = new TGResourceFactoryImpl(TuxGuitar.getInstance().getDisplay());
			
			PrintController controller = new PrintController(clonedSong, manager, factory);
			PrintLayout layout = new PrintLayout(controller, styles);
			
			layout.loadStyles(1f);
			layout.updateSong();
			layout.makeDocument(new PrintDocumentImpl(new TGRectangle(0, 0, PAGE_WIDTH,PAGE_HEIGHT), stream));
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
	
	private class PrintDocumentImpl implements PrintDocument{
		
		private TGPainterImpl painter;
		private TGRectangle bounds;
		private OutputStream stream;
		private Image buffer;
		private List<ImageData> pages;
		
		public PrintDocumentImpl(TGRectangle bounds, OutputStream stream){
			this.bounds = bounds;
			this.stream = stream;
			this.painter = new TGPainterImpl();
			this.pages = new ArrayList<ImageData>();
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
			this.buffer = new Image(TuxGuitar.getInstance().getDisplay(), width, height);
			this.painter.init( this.buffer );
		}
		
		public void pageFinish() {
			this.pages.add( this.buffer.getImageData() );
			this.painter.dispose();
			this.buffer.dispose();
		}
		
		public void start() {
			// Not implemented
		}
		
		public void finish() {
			try{
				PDFWriter.write(this.stream, this.pages);
			}catch(Throwable throwable){
				MessageDialog.errorMessage(throwable);
			}
		}
		
		public boolean isPaintable(int page) {
			return true;
		}
	}
}
