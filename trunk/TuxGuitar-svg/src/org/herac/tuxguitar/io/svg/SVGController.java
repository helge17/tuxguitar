package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGFactoryImpl;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;
import org.herac.tuxguitar.graphics.control.TGLayoutVertical;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

public class SVGController implements TGController {
	
	private SVGExporterStyles tgStyles;
	
	private TGSongManager tgSongManager;
	private TGResourceFactory tgResourceFactory;
	private TGLayoutVertical tgLayout;
	
	public SVGController(SVGExporterStyles tgStyles){
		this.tgStyles = tgStyles;
		this.tgSongManager = new TGSongManager();
		this.tgSongManager.setFactory( new TGFactoryImpl() );
		this.tgResourceFactory = new SVGResourceFactory();
		this.tgLayout = new TGLayoutVertical(this, this.tgStyles.getLayoutFlags() );
	}
	
	public TGSongManager getSongManager() {
		return this.tgSongManager;
	}
	
	public TGResourceFactory getResourceFactory() {
		return this.tgResourceFactory;
	}
	
	public void load(TGSong song) throws TGFileFormatException {
		this.tgSongManager.setSong(song);
		this.tgLayout.loadStyles();
		this.tgLayout.updateSong();
	}
	
	public void write(StringBuffer svgBuffer) throws Throwable {		
		if( this.tgSongManager.getSong() != null ){
			// Do a paint to calculate the document height.
			TGRectangle svgBounds = new TGRectangle(0, 0, 960, 0 );
			TGPainter svgPainter = new SVGPainter(new StringBuffer());
			this.tgLayout.paint(svgPainter, svgBounds, 0, 0);
			svgBounds.setHeight(this.tgLayout.getHeight());
			
			// Start of SVG document
			svgBuffer.append("<svg width=\"" + svgBounds.getWidth() + "px\" height=\"" + svgBounds.getHeight() + "px\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">");
			svgBuffer.append("\r\n");
			
			// Paint the TGSong
			svgPainter = new SVGPainter(svgBuffer);
			this.tgLayout.paint(svgPainter, svgBounds, 0, 0);
			svgPainter.dispose();
			
			// End of SVG document
			svgBuffer.append("\r\n");
			svgBuffer.append("</svg>");
		}
	}
	
	public void configureStyles(TGLayoutStyles styles) {
		styles.setBufferEnabled( false );
		styles.setStringSpacing( this.tgStyles.getLayoutStyles().getStringSpacing() );
		styles.setScoreLineSpacing( this.tgStyles.getLayoutStyles().getScoreLineSpacing() );
		styles.setFirstMeasureSpacing( this.tgStyles.getLayoutStyles().getFirstMeasureSpacing() );
		styles.setMinBufferSeparator( this.tgStyles.getLayoutStyles().getMinBufferSeparator() );
		styles.setMinTopSpacing( this.tgStyles.getLayoutStyles().getMinTopSpacing() );
		styles.setMinScoreTabSpacing( this.tgStyles.getLayoutStyles().getMinScoreTabSpacing() );
		styles.setFirstTrackSpacing( this.tgStyles.getLayoutStyles().getFirstTrackSpacing() );
		styles.setTrackSpacing( this.tgStyles.getLayoutStyles().getTrackSpacing() );
		styles.setChordFretIndexSpacing( this.tgStyles.getLayoutStyles().getChordFretIndexSpacing() );
		styles.setChordStringSpacing( this.tgStyles.getLayoutStyles().getChordStringSpacing() );
		styles.setChordFretSpacing( this.tgStyles.getLayoutStyles().getChordFretSpacing() );
		styles.setChordNoteSize( this.tgStyles.getLayoutStyles().getChordNoteSize() );
		styles.setRepeatEndingSpacing( this.tgStyles.getLayoutStyles().getRepeatEndingSpacing() );
		styles.setTextSpacing( this.tgStyles.getLayoutStyles().getTextSpacing() );
		styles.setMarkerSpacing( this.tgStyles.getLayoutStyles().getMarkerSpacing() );
		styles.setLoopMarkerSpacing( this.tgStyles.getLayoutStyles().getLoopMarkerSpacing() );
		styles.setDivisionTypeSpacing( this.tgStyles.getLayoutStyles().getDivisionTypeSpacing() );
		styles.setEffectSpacing( this.tgStyles.getLayoutStyles().getEffectSpacing() );
		styles.setDefaultFont( this.tgStyles.getLayoutStyles().getDefaultFont());
		styles.setNoteFont( this.tgStyles.getLayoutStyles().getNoteFont());
		styles.setTimeSignatureFont( this.tgStyles.getLayoutStyles().getTimeSignatureFont());
		styles.setLyricFont( this.tgStyles.getLayoutStyles().getLyricFont() );
		styles.setTextFont( this.tgStyles.getLayoutStyles().getTextFont() );
		styles.setMarkerFont( this.tgStyles.getLayoutStyles().getMarkerFont() );
		styles.setGraceFont( this.tgStyles.getLayoutStyles().getGraceFont());
		styles.setChordFont( this.tgStyles.getLayoutStyles().getChordFont() );
		styles.setChordFretFont( this.tgStyles.getLayoutStyles().getChordFretFont());
		styles.setBackgroundColor( this.tgStyles.getLayoutStyles().getBackgroundColor());
		styles.setLineColor( this.tgStyles.getLayoutStyles().getLineColor());
		styles.setScoreNoteColor( this.tgStyles.getLayoutStyles().getScoreNoteColor());
		styles.setTabNoteColor( this.tgStyles.getLayoutStyles().getTabNoteColor());
		styles.setPlayNoteColor( this.tgStyles.getLayoutStyles().getPlayNoteColor());
		styles.setLoopSMarkerColor( this.tgStyles.getLayoutStyles().getLoopSMarkerColor());
		styles.setLoopEMarkerColor( this.tgStyles.getLayoutStyles().getLoopEMarkerColor());
	}
	
	public int getTrackSelection() {
		return -1;
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
