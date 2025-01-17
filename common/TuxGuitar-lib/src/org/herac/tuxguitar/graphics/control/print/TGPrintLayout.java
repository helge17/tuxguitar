package org.herac.tuxguitar.graphics.control.print;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLyricImpl;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.graphics.control.TGTrackSpacing;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;
import org.herac.tuxguitar.util.TGMessagesManager;

public class TGPrintLayout extends TGLayout {
	
	private TGPrintSettings settings;
	private TGPrintDocument document;
	private int page;
	
	private UIFont songNameFont;
	private UIFont trackNameFont;
	private UIFont songAuthorFont;
	private UIFont artistFont;
	private UIFont albumNameYearFont;
	
	public TGPrintLayout(TGController controller, TGPrintSettings settings){
		super(controller, settings.getStyle());
		this.settings = settings;
	}
	
	public int getMode(){
		return 0;
	}
	
	public void makeDocument(TGPrintDocument document){
		this.page = 0;
		this.document = document;
		this.makeDocument();
	}
	
	private void makeDocument(){
		this.document.start();
		this.paint(this.document.getPainter(), null, this.document.getMargins().getLeft(), this.document.getMargins().getTop());
		this.document.finish();
	}
	
	public void paintSong(UIPainter painter, UIRectangle clientArea, float fromX, float fromY) {
		this.setWidth(0);
		this.setHeight(0);
		
		int style = getStyle();
		float posY = 0f;
		float height = getFirstTrackSpacing();
		float lineHeight = 0;
		
		Iterator<TGTrack> tracks = getSong().getTracks();
		while(tracks.hasNext()) {
			TGTrackImpl track = (TGTrackImpl) tracks.next();
			if( this.settings.getTrackNumber() < 0 || track.getNumber() == this.settings.getTrackNumber() ) {
				this.openPage();
				
				posY = Math.round(fromY + getFirstTrackSpacing());
				posY += this.paintHeader(painter, track);
				
				((TGLyricImpl)track.getLyrics()).start(getSkippedBeats(track));
				
				TGTrackSpacing ts = new TGTrackSpacing(this) ;
				TempLine line = getTempLines(track,( this.settings.getFromMeasure() - 1 ),ts);
				while(!line.measures.isEmpty()){
					
					ts.setSize(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES, ((style & DISPLAY_SCORE) != 0 ? ((getScoreLineSpacing() * 5) ) : 0));
					if((style & DISPLAY_SCORE) != 0){
						ts.setSize(TGTrackSpacing.POSITION_SCORE_UP_LINES, Math.abs(line.minY));
						if(line.maxY > track.getScoreHeight()){
							ts.setSize(TGTrackSpacing.POSITION_SCORE_DOWN_LINES, (line.maxY - track.getScoreHeight()) );
						}
					}
					if((style & DISPLAY_TABLATURE) != 0){
						ts.setSize(TGTrackSpacing.POSITION_TABLATURE_TOP_SEPARATOR, ((style & DISPLAY_SCORE) != 0 ? getMinScoreTabSpacing() : Math.max(Math.abs(line.minY), getStringSpacing()) ));
						ts.setSize(TGTrackSpacing.POSITION_TABLATURE, ((style & DISPLAY_SCORE) != 0 ?  track.getTabHeight() + getStringSpacing() + 1 : Math.max( line.maxY, track.getTabHeight() + getStringSpacing() + 1) ));
					}
					ts.setSize(TGTrackSpacing.POSITION_LYRIC,10);
					checkDefaultSpacing(ts);
					
					lineHeight = ts.getSize();
					//Verifico si entra en la pagina actual
					if((posY + lineHeight + getTrackSpacing()) > (this.document.getMargins().getTop() + getMaxHeight())){
						this.paintFooter(painter);
						this.closePage();
						this.openPage();
						posY = Math.round(this.document.getMargins().getTop() + getFirstTrackSpacing());
					}
					
					//pinto la linea
					this.paintLine(track, line, painter, fromX, posY, ts);
					
					float lineHeightWithSpacing = Math.round(lineHeight + getTrackSpacing() + 0.5f);
					
					posY += lineHeightWithSpacing;
					height += lineHeightWithSpacing;
					
					ts = new TGTrackSpacing(this) ;
					line = getTempLines(track,( line.lastIndex + 1 ),ts);
				}
				
				this.paintFooter(painter);
				this.closePage();
			}
		}
		this.setHeight(height);
	}
	
	public float paintHeader(UIPainter painter, TGTrack track){
		float headerOffset = 0f;
		if( this.document.isPaintable(this.page) ){
			float x = this.document.getMargins().getLeft();
			float y = this.document.getMargins().getTop();
			float fmTopLine = painter.getFMTopLine();
			String songName = getSong().getName();
			String songAuthor = getSong().getAuthor();
			String artistName = getSong().getArtist();
			String albumName = getSong().getAlbum();
			String releaseYear = getSong().getDate();
			String copyright = getSong().getCopyright();
			String transcriber = getSong().getTranscriber();
			String tabCreator = getSong().getWriter();
			String trackName = track.getName();
			
			if( songName != null && songName.length() > 0 ){
				painter.setFont(getSongNameFont());
				painter.drawString(songName,(x + getCenter(painter,songName)), (fmTopLine + y));
				headerOffset += (20.0f * getScale());
			}
			
			if (artistName != null && artistName.length() > 0) {

				painter.setFont(getArtistFont());
				painter.drawString(
					artistName,
					x + getCenter(painter, artistName),
					fmTopLine + y + Math.round(headerOffset)
				);
				headerOffset += 20.0f * getScale();
			}

			if ((albumName != null && albumName.length() > 0) || 
				(releaseYear != null && releaseYear.length() > 0)) {

				String albumNameReleaseYear = "";
				if( albumName != null && albumName.length() > 0 )
					albumNameReleaseYear += TGMessagesManager.getProperty("composition.album") + " " + albumName + " ";
				if( releaseYear != null && releaseYear.length() > 0 )
					albumNameReleaseYear += "(" + releaseYear + ")";

				painter.setFont(getAlbumNameYearFont());
				painter.drawString(
					albumNameReleaseYear,
					x + getCenter(painter, albumNameReleaseYear),
					fmTopLine + y + Math.round(headerOffset)
				);
				headerOffset += 20.0f * getScale();
			}
			
			if (trackName != null && trackName.length() > 0) {

				trackName = TGMessagesManager.getProperty("track") + " " + trackName;
				painter.setFont(getTrackNameFont());
				painter.drawString(
					trackName,
					x + getCenter(painter,trackName),
					fmTopLine + y + Math.round(headerOffset)
				);
				headerOffset += 20.0f * getScale();
			}
			
			if (songAuthor != null && songAuthor.length() > 0) {

				songAuthor = TGMessagesManager.getProperty("composition.author") + " " + songAuthor;
				painter.setFont(getSongAuthorFont());
				painter.drawString(
					songAuthor,
					x + getRight(painter, songAuthor),
					fmTopLine + y + Math.round(headerOffset)
				);
				headerOffset += 10.0f * getScale();
			}

			if (copyright != null && copyright.length() > 0) {

				copyright = TGMessagesManager.getProperty("composition.copyright") + " " + copyright;
				painter.setFont(getSongAuthorFont());
				painter.drawString(
					copyright,
					x + getRight(painter, copyright),
					fmTopLine + y + Math.round(headerOffset)
				);
				headerOffset += 10.0f * getScale();
			}
			
			if (transcriber != null && transcriber.length() > 0) {

				transcriber = TGMessagesManager.getProperty("composition.transcriber") + " " + transcriber;
				painter.setFont(getSongAuthorFont());
				painter.drawString(
					transcriber,
					x + getRight(painter, transcriber),
					fmTopLine + y + Math.round(headerOffset)
				);
				headerOffset += 10.0f * getScale();
			}

			if( tabCreator != null && tabCreator.length() > 0 ){

				tabCreator = TGMessagesManager.getProperty("composition.writer") + " " + tabCreator;
				painter.setFont(getSongAuthorFont());
				painter.drawString(
					tabCreator,
					x + getRight(painter, tabCreator),
					fmTopLine + y + Math.round(headerOffset)
				);
				headerOffset += 20.0f * getScale();
			}
		}
		return headerOffset;
	}
	
	private void paintFooter(UIPainter painter){
		if(this.document.isPaintable(this.page) ){
			float x = this.document.getMargins().getLeft();
			float y = this.document.getMargins().getTop();
			float fmTopLine = painter.getFMTopLine();
			String pageNumber = Integer.toString(this.page);
			
			painter.setBackground(getLightColor(getResources().getBackgroundColor()));
			painter.setForeground(getDarkColor(getResources().getForegroundColor()));
			painter.drawString(pageNumber, (x + getRight(painter, pageNumber)),(fmTopLine + y + getBottom(painter, pageNumber)));
		}
	}
	
	public void paintLine(TGTrackImpl track, TempLine line, UIPainter painter, float fromX, float fromY, TGTrackSpacing ts) {
		if(this.document.isPaintable(this.page) ){
			float posX = fromX;
			float posY = fromY;
			float width = 0;
			
			float defaultMeasureSpacing = 0;
			if( line.fullLine ){
				float diff = ( getMaxWidth() - line.tempWith);
				if( diff != 0 && line.measures.size() > 0 ){
					defaultMeasureSpacing = diff / line.measures.size();
				}
			}
			
			float measureSpacing = defaultMeasureSpacing;
			
			for(int i = 0;i < line.measures.size();i ++){
				int index = ((Integer)line.measures.get(i)).intValue();
				TGMeasureImpl currMeasure = (TGMeasureImpl)track.getMeasure(index);
				
				//asigno la posicion dentro del compas
				currMeasure.setPosX(posX);
				currMeasure.setPosY(posY);
				currMeasure.setTs(ts);
				
				((TGLyricImpl)track.getLyrics()).setCurrentMeasure(currMeasure);
				
				currMeasure.setFirstOfLine(i == 0);
				
				float measureWidth = currMeasure.getWidth(this);
				float measureWidthWithSpacing = (this.isBufferEnabled() ? Math.round(measureWidth + measureSpacing) : (measureWidth + measureSpacing));
				float measureSpacingAfterRound = (measureWidthWithSpacing - measureWidth);
				
				this.paintMeasure(currMeasure, painter, measureSpacingAfterRound);
				((TGLyricImpl)track.getLyrics()).paintCurrentNoteBeats(painter, this, currMeasure, posX, posY);
				
				posX += measureWidthWithSpacing;
				width += measureWidthWithSpacing;
				measureSpacing = (defaultMeasureSpacing + (measureSpacing - measureSpacingAfterRound));
			}
			this.setWidth(Math.max(getWidth(),width));
		}
	}
	
	public void fillBackground(UIPainter painter, UIRectangle area) {
		if(!this.document.isTransparentBackground()) {
			super.fillBackground(painter, area);
		}
	}
	
	public void fillBackground(UIPainter painter) {
		if(!this.document.isTransparentBackground()) {
			this.fillBackground(painter, new UIRectangle(this.document.getSize()));
		}
	}
	
	private void openPage(){
		this.page ++;
		if( this.document.isPaintable(this.page) ){
			this.document.pageStart();
			
			this.fillBackground(this.document.getPainter());
		}
	}
	
	private void closePage(){
		if( this.document.isPaintable(this.page) ){
			this.document.pageFinish();
		}
	}
	
	private float getCenter(UIPainter painter,String text){
		return ((getMaxWidth() - painter.getFMWidth(text)) / 2);
	}
	
	private float getRight(UIPainter painter,String text){
		return ((getMaxWidth() - painter.getFMWidth(text)));
	}
	
	private float getBottom(UIPainter painter,String text){
		return ((getMaxHeight() - painter.getFMHeight()));
	}
	
	private TempLine getTempLines(TGTrack track,int fromIndex,TGTrackSpacing ts) {
		TempLine line = new TempLine();
		int measureCount = track.countMeasures();
		for (int measureIdx = fromIndex; measureIdx < measureCount; measureIdx++) {
			TGMeasureImpl measure= (TGMeasureImpl) track.getMeasure(measureIdx);
			if( measure.getNumber() >= this.settings.getFromMeasure() && measure.getNumber() <= this.settings.getToMeasure()){
				
				//verifico si tengo que bajar de linea
				if((line.tempWith + measure.getWidth(this)) >= getMaxWidth() ){
					if( line.measures.isEmpty() ) {
						this.addToTempLine(line, ts, measure, measureIdx);
					}
					line.fullLine = true;
					return line;
				}
				
				this.addToTempLine(line, ts, measure, measureIdx);
			}
		}
		
		return line;
	}
	
	private void addToTempLine(TempLine line, TGTrackSpacing ts, TGMeasureImpl measure, int measureIdx) {
		line.tempWith +=  measure.getWidth(this);
		line.maxY = (measure.getMaxY() > line.maxY)?measure.getMaxY():line.maxY;
		line.minY = (measure.getMinY() < line.minY)?measure.getMinY():line.minY;
		
		line.addMeasure(measureIdx);
		measure.registerSpacing(this,ts);
	}
	
	private int getSkippedBeats(TGTrack track) {
		int beats = 0;
		
		for (int i = 0; i < (this.settings.getFromMeasure() - 1); i++) {
			TGMeasureImpl measure = (TGMeasureImpl) track.getMeasure(i);
			beats += measure.getNotEmptyBeats();
		}
		return beats;
	}
	
	public boolean isPlayModeEnabled(){
		return false;
	}
	
	public float getMaxWidth(){
		return (this.document.getSize().getWidth() - this.document.getMargins().getLeft() - this.document.getMargins().getRight() - getScale());
	}
	
	public float getMaxHeight(){
		return (this.document.getSize().getHeight() - this.document.getMargins().getTop() - this.document.getMargins().getBottom() - getScale());
	}
	
	public boolean isFirstMeasure(TGMeasureHeader mh){
		return (mh.getNumber() == this.settings.getFromMeasure());
	}
	
	public boolean isLastMeasure(TGMeasureHeader mh){
		return (mh.getNumber() == this.settings.getToMeasure());
	}
	
	public UIFont getSongNameFont() {
		UIResourceFactory factory = this.getComponent().getResourceFactory();
		if( factory != null && ( this.songNameFont == null || this.songNameFont.isDisposed() ) ){
			this.songNameFont = factory.createFont(this.getResources().getDefaultFont().getName(), (16.0f * getFontScale()), true, false);
		}
		return this.songNameFont;
	}
	
	public UIFont getSongAuthorFont() {
		UIResourceFactory factory = this.getComponent().getResourceFactory();

		if( factory != null && 
			(this.songAuthorFont == null || this.songAuthorFont.isDisposed()) ){

			this.songAuthorFont = factory.createFont(
				this.getResources().getDefaultFont().getName(), 
				8.0f * getFontScale(),
				true, false
			);
		}
		return this.songAuthorFont;
	}
	
	public UIFont getTrackNameFont() {
		UIResourceFactory factory = this.getComponent().getResourceFactory();
		if( factory != null && ( this.trackNameFont == null || this.trackNameFont.isDisposed() ) ){
			this.trackNameFont = factory.createFont(this.getResources().getDefaultFont().getName(), (8.0f * getFontScale()), true, false);
		}
		return this.trackNameFont;
	}
	
	public UIFont getArtistFont() {
		UIResourceFactory factory = this.getComponent().getResourceFactory();
		
		if( factory != null && 
			(this.artistFont == null || this.artistFont.isDisposed()) ){

			this.artistFont = factory.createFont(
				this.getResources().getDefaultFont().getName(),
				12.0f * getFontScale(),
				true, false
			);
		}
		
		return this.artistFont;
	}

	public UIFont getAlbumNameYearFont() {
		UIResourceFactory factory = this.getComponent().getResourceFactory();
		
		if( factory != null && 
			(this.albumNameYearFont == null || this.albumNameYearFont.isDisposed()) ){

			this.albumNameYearFont = factory.createFont(
				this.getResources().getDefaultFont().getName(),
				10.0f * getFontScale(),
				true, false
			);
		}
		
		return this.albumNameYearFont;
	}
	
	public void disposeLayout(){
		super.disposeLayout();
		if( this.songNameFont != null && !this.songNameFont.isDisposed() ){
			this.songNameFont.dispose();
		}
		if( this.songAuthorFont != null && !this.songAuthorFont.isDisposed() ){
			this.songAuthorFont.dispose();
		}
		if( this.trackNameFont != null && !this.trackNameFont.isDisposed() ){
			this.trackNameFont.dispose();
		}
		if( this.artistFont != null && !this.artistFont.isDisposed() ){
			this.artistFont.dispose();
		}
		if( this.albumNameYearFont != null && 
			!this.albumNameYearFont.isDisposed() ){

			this.albumNameYearFont.dispose();
		}
	}
	
	private class TempLine {
		
		protected float tempWith;
		protected int lastIndex;
		protected boolean fullLine;
		protected float maxY = 0;
		protected float minY = 50;
		protected List<Integer> measures;
		
		public TempLine(){
			this.measures = new ArrayList<Integer>();
		}
		
		public void addMeasure(int index){
			this.measures.add(Integer.valueOf(index));
			this.lastIndex = index;
		}
	}
}
