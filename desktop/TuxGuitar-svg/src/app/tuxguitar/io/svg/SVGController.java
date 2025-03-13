package app.tuxguitar.io.svg;

import app.tuxguitar.graphics.control.TGController;
import app.tuxguitar.graphics.control.TGFactoryImpl;
import app.tuxguitar.graphics.control.TGLayoutStyles;
import app.tuxguitar.graphics.control.TGLayoutVertical;
import app.tuxguitar.graphics.control.TGResourceBuffer;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UIResourceFactory;
import app.tuxguitar.util.TGMessagesManager;

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

			((SVGPainter) svgPainter).drawString(
				songName,
				widthMinusMargin / 2,
				fmTopLine + startY + Math.round(headerOffset),
				"middle"
			);

			headerOffset += 20.0f * tgLayout.getScale();
		}

		if( artistName != null && artistName.length() > 0 ){
			svgPainter.setFont(getArtistFont());

			((SVGPainter) svgPainter).drawString(
				artistName,
				widthMinusMargin / 2,
				fmTopLine + startY + Math.round(headerOffset),
				"middle"
			);

			headerOffset += 20.0f * tgLayout.getScale();
		}

		if( (albumName != null && albumName.length() > 0) ||
			(releaseYear != null && releaseYear.length() > 0) ){

			String albumNameReleaseYear = "";
			if( albumName != null && albumName.length() > 0 )
				albumNameReleaseYear += TGMessagesManager.getProperty("composition.album") + " " + albumName + " ";
			if( releaseYear != null && releaseYear.length() > 0 )
				albumNameReleaseYear += "(" + releaseYear + ")";

			svgPainter.setFont(getAlbumNameYearFont());

			((SVGPainter) svgPainter).drawString(
				albumNameReleaseYear,
				widthMinusMargin / 2,
				startY + fmTopLine + Math.round(headerOffset),
				"middle"
			);

			headerOffset += 20.0f * tgLayout.getScale();
		}

		if( songAuthor != null && songAuthor.length() > 0 ){

			songAuthor = TGMessagesManager.getProperty("composition.author") + " " + songAuthor;
			svgPainter.setFont(getSongAuthorFont());

			((SVGPainter) svgPainter).drawString(
				songAuthor,
				widthMinusMargin,
				fmTopLine + startY + Math.round(headerOffset),
				"end"
			);

			headerOffset += 10.0f * tgLayout.getScale();
		}

		if( copyright != null && copyright.length() > 0 ){

			copyright = TGMessagesManager.getProperty("composition.copyright") + " " + copyright;
			svgPainter.setFont(getSongAuthorFont());

			((SVGPainter) svgPainter).drawString(
				copyright,
				widthMinusMargin,
				fmTopLine + startY + Math.round(headerOffset),
				"end"
			);

			headerOffset += 10.0f * tgLayout.getScale();
		}

		if( transcriber != null && transcriber.length() > 0 ){

			transcriber = TGMessagesManager.getProperty("composition.transcriber") + " " + transcriber;
			svgPainter.setFont(getSongAuthorFont());

			((SVGPainter) svgPainter).drawString(
				transcriber,
				widthMinusMargin,
				fmTopLine + startY + Math.round(headerOffset),
				"end"
			);

			headerOffset += 10.0f * tgLayout.getScale();
		}

		if( tabCreator != null && tabCreator.length() > 0 ){

			tabCreator = TGMessagesManager.getProperty("composition.writer") + " " + tabCreator;
			svgPainter.setFont(getSongAuthorFont());

			((SVGPainter) svgPainter).drawString(
				tabCreator,
				widthMinusMargin,
				fmTopLine + startY + Math.round(headerOffset),
				"end"
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
