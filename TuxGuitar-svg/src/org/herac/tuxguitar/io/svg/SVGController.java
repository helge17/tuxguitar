package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGFactoryImpl;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;
import org.herac.tuxguitar.graphics.control.TGLayoutVertical;
import org.herac.tuxguitar.graphics.control.TGResourceBuffer;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

public class SVGController implements TGController {
	
	private SVGStyles tgStyles;
	
	private TGSong tgSong;
	private TGSongManager tgSongManager;
	private TGResourceBuffer tgResourceBuffer;
	private TGResourceFactory tgResourceFactory;
	private TGLayoutVertical tgLayout;
	
	public SVGController(SVGStyles tgStyles){
		this.tgStyles = tgStyles;
		this.tgSongManager = new TGSongManager();
		this.tgSongManager.setFactory(new TGFactoryImpl());
		this.tgResourceFactory = new SVGResourceFactory();
		this.tgResourceBuffer = new TGResourceBuffer();
		this.tgLayout = new TGLayoutVertical(this, this.tgStyles.getFlags() );
	}
	
	public TGSongManager getSongManager() {
		return this.tgSongManager;
	}
	
	public TGSong getSong() {
		return this.tgSong;
	}
	
	public TGResourceFactory getResourceFactory() {
		return this.tgResourceFactory;
	}
	
	public TGResourceBuffer getResourceBuffer() {
		return this.tgResourceBuffer;
	}
	
	public void load(TGSong song) throws TGFileFormatException {
		this.tgSong = song;
		this.tgLayout.loadStyles();
		this.tgLayout.updateSong();
	}
	
	public void write(StringBuffer svgBuffer) throws Throwable {		
		if( this.tgSong != null ){
			// Do a paint to calculate the document height.
			TGRectangle svgBounds = new TGRectangle(0, 0, 960, 0 );
			TGPainter svgPainter = new SVGPainter(new StringBuffer());
			this.tgLayout.paint(svgPainter, svgBounds, 0, 0);
			svgBounds.setHeight(this.tgLayout.getHeight());
			
			// Start of SVG document
			svgBuffer.append("<svg width=\"" + svgBounds.getWidth() + "px\" height=\"" + svgBounds.getHeight() + "px\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">");
			svgBuffer.append("\r\n");
			
			// Open the painter
			svgPainter = new SVGPainter(svgBuffer);
			
			// Fill the background.
			TGColor svgBackground = svgPainter.createColor(this.tgStyles.getStyles().getBackgroundColor());
			svgPainter.setBackground(svgBackground);
			svgPainter.initPath(TGPainter.PATH_FILL);
			svgPainter.addRectangle(0, 0, svgBounds.getWidth(), svgBounds.getHeight());
			svgPainter.closePath();
			svgBackground.dispose();
			
			// Paint the TGSong
			this.tgLayout.paint(svgPainter, svgBounds, 0, 0);
			
			// Closes the painter
			svgPainter.dispose();
			
			// End of SVG document
			svgBuffer.append("\r\n");
			svgBuffer.append("</svg>");
		}
	}
	
	public TGLayoutStyles getStyles() {
		return this.tgStyles.getStyles();
	}
	
	public int getTrackSelection() {
		return this.tgStyles.getTrack();
	}
	
	public boolean isRunning(TGBeat beat) {
		return false;
	}
	
	public boolean isRunning(TGMeasure measure) {
		return false;
	}
	
	public boolean isLoopSHeader(TGMeasureHeader measureHeader) {
		return false;
	}
	
	public boolean isLoopEHeader(TGMeasureHeader measureHeader) {
		return false;
	}
}
