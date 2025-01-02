package org.herac.tuxguitar.io.svg;

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
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;

public class SVGController implements TGController {
	
	private SVGStyles tgStyles;
	
	private TGSong tgSong;
	private TGSongManager tgSongManager;
	private TGResourceBuffer tgResourceBuffer;
	private UIResourceFactory tgResourceFactory;
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
	
	public UIResourceFactory getResourceFactory() {
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
			UIRectangle svgBounds = new UIRectangle(0, 0, 960, 0 );
			UIPainter svgPainter = new SVGPainter(new StringBuffer());
			this.tgLayout.paint(svgPainter, svgBounds, 0, 0);
			svgBounds.getSize().setHeight(this.tgLayout.getHeight());
			
			// Start of SVG document
			svgBuffer.append("<svg width=\"" + svgBounds.getWidth() + "px\" height=\"" + svgBounds.getHeight() + "px\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">");
			svgBuffer.append("\r\n");
			
			// Open the painter
			svgPainter = new SVGPainter(svgBuffer);
			
			// Fill the background.
			UIColor svgBackground = this.tgResourceFactory.createColor(this.tgStyles.getStyles().getBackgroundColor());
			svgPainter.setBackground(svgBackground);
			svgPainter.initPath(UIPainter.PATH_FILL);
			svgPainter.addRectangle(0, 0, svgBounds.getWidth(), svgBounds.getHeight());
			svgPainter.closePath();
			svgBackground.dispose();
			
			// Paint header with song info first
			float headerOffset = 0f;
			headerOffset += this.paintHeader(svgPainter, svgBounds, 0f, 10f);
			
			// Paint the TGSong
			this.tgLayout.paint(svgPainter, svgBounds, 0, headerOffset);
			
			// Closes the painter
			svgPainter.dispose();
			
			// End of SVG document
			svgBuffer.append("\r\n");
			svgBuffer.append("</svg>");
		}
	}
	
	public float paintHeader(
		UIPainter svgPainter, 
		UIRectangle svgBounds,
		float startX, float startY
	) {
		float headerOffset = 0f;
		float marginLeft = this.tgLayout.getFirstMeasureSpacing();
		float marginRight = 5f;
		float widthMinusMargin = 
				svgBounds.getWidth() - (marginLeft + marginRight);
		float fmTopLine = Math.round(svgPainter.getFMTopLine());

		String songName = getSong().getName();
		String songAuthor = getSong().getAuthor();
		String artistName = getSong().getArtist();
		String albumName = getSong().getAlbum();
		String releaseYear = getSong().getDate();
		String copyright = getSong().getCopyright();
		String transcriber = getSong().getTranscriber();
		String tabCreator = getSong().getWriter();
		
		if( songName != null && songName.length() > 0 ){

			svgPainter.setFont(getSongNameFont());
			svgPainter.drawString(
				songName, 
				startX + alignCenter(svgPainter, widthMinusMargin, songName),
				fmTopLine + startY + Math.round(headerOffset)
			);
			headerOffset += 20.0f * tgLayout.getScale();
		}

		if( artistName != null && artistName.length() > 0 ){

			svgPainter.setFont(getArtistFont());
			svgPainter.drawString(
				artistName, 
				startX + alignCenter(svgPainter, widthMinusMargin, artistName),
				fmTopLine + startY + Math.round(headerOffset)
			);
			headerOffset += 10.0f * tgLayout.getScale();
		}
		
		if( (albumName != null && albumName.length() > 0) ||
			(releaseYear != null && releaseYear.length() > 0) ){
			
			String albumNameReleaseYear = "";
			if( albumName != null && albumName.length() > 0 )
				albumNameReleaseYear += "Recorded on " + albumName + " ";
			if( releaseYear != null && releaseYear.length() > 0 )
				albumNameReleaseYear += "(" + releaseYear + ")";
		
			svgPainter.setFont(getAlbumNameYearFont());
			svgPainter.drawString(
				albumNameReleaseYear,
				startX + alignCenter(svgPainter, widthMinusMargin, albumNameReleaseYear),
				startY + fmTopLine + Math.round(headerOffset)
			);
			headerOffset += 20.0f * tgLayout.getScale();
		}
		
		if( songAuthor != null && songAuthor.length() > 0 ){

			svgPainter.setFont(getSongAuthorFont());
			svgPainter.drawString(
				songAuthor,
				startX + alignRight(svgPainter, widthMinusMargin, songAuthor),
				fmTopLine + startY + Math.round(headerOffset)
			);
			headerOffset += 10.0f * tgLayout.getScale();
		}

		if( copyright != null && copyright.length() > 0 ){

			copyright = "Copyrighted by " + copyright;
			svgPainter.setFont(getSongAuthorFont());
			svgPainter.drawString(
				copyright,
				startX + alignRight(svgPainter, widthMinusMargin, copyright),
				fmTopLine + startY + Math.round(headerOffset)
			);
			headerOffset += 10.0f * tgLayout.getScale();
		}
		
		if( transcriber != null && transcriber.length() > 0 ){

			transcriber = "Transcribed by " + transcriber;
			svgPainter.setFont(getSongAuthorFont());
			svgPainter.drawString(
				transcriber,
				startX + alignRight(svgPainter, widthMinusMargin, transcriber),
				fmTopLine + startY + Math.round(headerOffset)
			);
			headerOffset += 10.0f * tgLayout.getScale();
		}

		if( tabCreator != null && tabCreator.length() > 0 ){

			tabCreator = "Created by " + tabCreator;
			svgPainter.setFont(getSongAuthorFont());
			svgPainter.drawString(
				tabCreator,
				startX + alignRight(svgPainter, widthMinusMargin, tabCreator),
				fmTopLine + startY + Math.round(headerOffset)
			);
			headerOffset += 20.0f * tgLayout.getScale();
		}

		return headerOffset;
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

	private float alignCenter(
		UIPainter svgPainter, 
		float widthMinusMargin, 
		String text
	) {
		
		// For some reason, getFMWidth(text) outputs a value that has 1.5 times
		// more pixels than the actual SVG rendering.
		// 0.65f is a temporary fix to calibrate it back to the correct value.
		return (( widthMinusMargin - 0.65f * svgPainter.getFMWidth(text) ) / 2 );
	}
	
	private float alignRight(
		UIPainter svgPainter,
		float widthMinusMargin,
		String text
	) {
		return widthMinusMargin - 0.65f * svgPainter.getFMWidth(text);
	}
	
	private UIFont getSongNameFont() {
		String textFont = this.tgStyles.getStyles().getTextFont().getName();

		UIFont songNameFont = this.tgResourceFactory.createFont(
			textFont,
			16.0f * this.tgLayout.getFontScale(), 
			true, false
		);

		return songNameFont;
	}

	private UIFont getArtistFont() {
		String textFont = this.tgStyles.getStyles().getTextFont().getName();

		UIFont artistFont = this.tgResourceFactory.createFont(
			textFont,
			12.0f * this.tgLayout.getFontScale(), 
			true, false
		);

		return artistFont;
	}

	private UIFont getAlbumNameYearFont() {
		String textFont = this.tgStyles.getStyles().getTextFont().getName();
		
		UIFont albumNameYearFont = this.tgResourceFactory.createFont(
			textFont,
			10.0f * this.tgLayout.getFontScale(),
			true, false
		);

		return albumNameYearFont;
	}

	private UIFont getSongAuthorFont() {
		String textFont = this.tgStyles.getStyles().getTextFont().getName();
		
		UIFont songAuthorFont = this.tgResourceFactory.createFont(
			textFont,
			8.0f * this.tgLayout.getFontScale(),
			true, false
		);

		return songAuthorFont;
	}

}
