package org.herac.tuxguitar.io.image;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.TGFactoryImpl;
import org.herac.tuxguitar.gui.editors.tab.Tablature;
import org.herac.tuxguitar.gui.editors.tab.layout.PrinterViewLayout;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.printer.PrintDocument;
import org.herac.tuxguitar.gui.printer.PrintStyles;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGSynchronizer;

public class ImageExporter implements TGRawExporter{
	
	private static final int PAGE_WIDTH = 550;
	private static final int PAGE_HEIGHT = 800;
	
	private PrintStyles styles;
	private ImageFormat format;
	private String path;
	
	public String getExportName() {
		return TuxGuitar.getProperty("tuxguitar-image.export-label");
	}
	
	public PrintStyles getStyles() {
		return this.styles;
	}
	
	public void setStyles(PrintStyles styles) {
		this.styles = styles;
	}
	
	public ImageFormat getFormat() {
		return this.format;
	}
	
	public void setFormat(ImageFormat format) {
		this.format = format;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public PrintStyles getDefaultStyles(TGSong song){
		PrintStyles styles = new PrintStyles();
		styles.setStyle(ViewLayout.DISPLAY_TABLATURE);
		styles.setFromMeasure(1);
		styles.setToMeasure(song.countMeasureHeaders());
		styles.setTrackNumber(1);
		styles.setBlackAndWhite(false);
		return styles;
	}
	
	public void exportSong(final TGSong song) {
		if( this.path != null ){
			if( this.styles == null ){
				this.styles = getDefaultStyles(song);
			}
			if( this.format == null ){
				this.format = ImageFormat.IMAGE_FORMATS[0];
			}
			export(song);
		}
	}
	
	public void export(final TGSong song){
		new Thread(new Runnable() {
			public void run() {
				try{
					TGSongManager manager = new TGSongManager();
					manager.setFactory(new TGFactoryImpl());
					manager.setSong(song.clone(manager.getFactory()));
					
					export(manager);
				}catch(Throwable throwable){
					MessageDialog.errorMessage(throwable);
				}
			}
		}).start();
	}
	
	public void export(final TGSongManager manager){
		new SyncThread(new Runnable() {
			public void run() {
				try{
					Tablature tablature = new Tablature(TuxGuitar.instance().getShell());
					tablature.setSongManager(manager);
					
					PrinterViewLayout layout = new PrinterViewLayout(tablature, getStyles(), 1f);
					
					export(layout);
				}catch(Throwable throwable){
					MessageDialog.errorMessage(throwable);
				}
			}
		}).start();
	}
	
	public void export(final PrinterViewLayout layout){
		new Thread(new Runnable() {
			public void run() {
				try{
					layout.getTablature().updateTablature();
					layout.makeDocument(new PrintDocumentImpl(layout,new Rectangle(25,25,PAGE_WIDTH,PAGE_HEIGHT), getFormat(), getPath() ));
				}catch(Throwable throwable){
					MessageDialog.errorMessage(throwable);
				}
			}
		}).start();
	}
	
	private class PrintDocumentImpl implements PrintDocument{
		
		private PrinterViewLayout layout;
		private TGPainter painter;
		private Rectangle bounds;
		private String path;
		private Image buffer;
		private List pages;
		private ImageFormat format;
		
		public PrintDocumentImpl(PrinterViewLayout layout, Rectangle bounds, ImageFormat format, String path){
			this.layout = layout;
			this.bounds = bounds;
			this.path = path;
			this.painter = new TGPainter();
			this.pages = new ArrayList();
			this.format = format;
		}
		
		public TGPainter getPainter() {
			return this.painter;
		}
		
		public Rectangle getBounds(){
			return this.bounds;
		}
		
		public void pageStart() {
			this.buffer = new Image(this.layout.getTablature().getDisplay(),this.bounds.width + (this.bounds.x * 2), this.bounds.height + (this.bounds.y * 2) );
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
			try {
				TGSynchronizer.instance().addRunnable(new TGSynchronizer.TGRunnable() {
					public void run() {
						dispose();
					}
				});
				
				ImageWriter.write(this.format, this.path, this.pages);
			} catch (Throwable throwable) {
				MessageDialog.errorMessage(throwable);
			}
		}
		
		protected void dispose(){
			this.layout.getTablature().dispose();
		}
		
		public boolean isPaintable(int page) {
			return true;
		}
	}
}
