package org.herac.tuxguitar.app.printer;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLyricImpl;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.graphics.control.TGTrackSpacing;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGTrack;

public class PrintLayout extends TGLayout{
	
	private PrintStyles styles;
	private PrintDocument document;
	private int page;
	
	private TGFont songNameFont;
	private TGFont trackNameFont;
	private TGFont songAuthorFont;
	
	public PrintLayout(TGController controller,PrintStyles styles){
		super(controller,( styles.getStyle() | DISPLAY_COMPACT ) );
		this.styles = styles;
	}
	
	public int getMode(){
		return 0;
	}
	
	public void makeDocument(PrintDocument document){
		this.page = 0;
		this.document = document;
		this.makeDocument();
	}
	
	private void makeDocument(){
		this.document.start();
		
		this.openPage();
		this.paintHeader(this.document.getPainter());
		this.paintSong(this.document.getPainter(), null, this.document.getBounds().getX(), ( this.document.getBounds().getY() + (80.0f * getScale() ) ) );
		this.paintFooter(this.document.getPainter());
		this.closePage();
		
		this.document.finish();
	}
	
	public void paintSong(TGPainter painter,TGRectangle clientArea, float fromX, float fromY) {
		this.setWidth(0);
		this.setHeight(0);
		
		int style = getStyle();
		float posY = Math.round(fromY + getFirstTrackSpacing());
		float height = getFirstTrackSpacing();
		float lineHeight = 0;
		
		TGTrackImpl track = (TGTrackImpl)getSongManager().getTrack(getSong(), this.styles.getTrackNumber());
		((TGLyricImpl)track.getLyrics()).start(getSkippedBeats(track));
		
		TGTrackSpacing ts = new TGTrackSpacing(this) ;
		TempLine line = getTempLines(track,( this.styles.getFromMeasure() - 1 ),ts);
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
			if((posY + lineHeight + getTrackSpacing()) > (this.document.getBounds().getY() + getMaxHeight())){
				this.paintFooter(painter);
				this.closePage();
				this.openPage();
				posY = Math.round(this.document.getBounds().getY() + getFirstTrackSpacing());
			}
			
			//pinto la linea
			this.paintLine(track, line, painter, fromX, posY, ts);
			
			float lineHeightWithSpacing = Math.round(lineHeight + getTrackSpacing() + 0.5f);
			
			posY += lineHeightWithSpacing;
			height += lineHeightWithSpacing;
			
			ts = new TGTrackSpacing(this) ;
			line = getTempLines(track,( line.lastIndex + 1 ),ts);
		}
		this.setHeight(height);
	}
	
	public void paintHeader(TGPainter painter){
		if(this.document.isPaintable(this.page) ){
			float x = this.document.getBounds().getX();
			float y = this.document.getBounds().getY();
			String songName = getSong().getName();
			String songAuthor = getSong().getAuthor();
			String trackName = "(" + getSongManager().getTrack(getSong(), this.styles.getTrackNumber()).getName() + ")";
			
			if( songName == null || songName.length() == 0 ){
				songName = TuxGuitar.getProperty("print-header.default-song-name");
			}
			if( songAuthor == null || songAuthor.length() == 0 ){
				songAuthor = TuxGuitar.getProperty("print-header.default-song-author");
			}
			painter.setFont(getSongNameFont(painter));
			painter.drawString(songName,(x + getCenter(painter,songName)),y);
			painter.setFont(getTrackNameFont(painter));
			painter.drawString(trackName,(x + getCenter(painter,trackName)),(y + Math.round(30.0f * getScale())));
			painter.setFont(getSongAuthorFont(painter));
			painter.drawString(songAuthor,(x + getRight(painter,songAuthor)),(y + Math.round(50.0f * getScale())));
		}
	}
	
	private void paintFooter(TGPainter painter){
		if(this.document.isPaintable(this.page) ){
			float x = this.document.getBounds().getX();
			float y = this.document.getBounds().getY();
			String pageNumber = Integer.toString(this.page);
			
			painter.setBackground(getResources().getColorWhite());
			painter.setForeground(getResources().getColorBlack());
			painter.drawString(pageNumber, (x + getRight(painter, pageNumber)),(y + getBottom(painter, pageNumber)));
		}
	}
	
	public void paintLine(TGTrackImpl track, TempLine line, TGPainter painter, float fromX, float fromY, TGTrackSpacing ts) {
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
	
	private void openPage(){
		this.page ++;
		if( this.document.isPaintable(this.page) ){
			this.document.pageStart();
		}
	}
	
	private void closePage(){
		if( this.document.isPaintable(this.page) ){
			this.document.pageFinish();
		}
	}
	
	private float getCenter(TGPainter painter,String text){
		return ((getMaxWidth() - painter.getFMWidth(text)) / 2);
	}
	
	private float getRight(TGPainter painter,String text){
		return ((getMaxWidth() - painter.getFMWidth(text)));
	}
	
	private float getBottom(TGPainter painter,String text){
		return ((getMaxHeight() - painter.getFMHeight()));
	}
	
	private TempLine getTempLines(TGTrack track,int fromIndex,TGTrackSpacing ts) {
		TempLine line = new TempLine();
		int measureCount = track.countMeasures();
		for (int measureIdx = fromIndex; measureIdx < measureCount; measureIdx++) {
			TGMeasureImpl measure= (TGMeasureImpl) track.getMeasure(measureIdx);
			if( measure.getNumber() >= this.styles.getFromMeasure() && measure.getNumber() <= this.styles.getToMeasure()){
				
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
		
		for (int i = 0; i < (this.styles.getFromMeasure() - 1); i++) {
			TGMeasureImpl measure = (TGMeasureImpl) track.getMeasure(i);
			beats += measure.getNotEmptyBeats();
		}
		return beats;
	}
	
	public boolean isPlayModeEnabled(){
		return false;
	}
	
	public float getMaxWidth(){
		return (this.document.getBounds().getWidth() - this.document.getBounds().getX() - 10);
	}
	
	public float getMaxHeight(){
		return (this.document.getBounds().getHeight() - this.document.getBounds().getY() - 10);
	}
	
	public boolean isFirstMeasure(TGMeasureHeader mh){
		return (mh.getNumber() == this.styles.getFromMeasure());
	}
	
	public boolean isLastMeasure(TGMeasureHeader mh){
		return (mh.getNumber() == this.styles.getToMeasure());
	}
	
	public TGFont getSongNameFont( TGResourceFactory factory ){
		if( factory != null && ( this.songNameFont == null || this.songNameFont.isDisposed() ) ){
			this.songNameFont = factory.createFont(this.getResources().getDefaultFont().getName(), (16.0f * getFontScale()), true, false);
		}
		return this.songNameFont;
	}
	
	public TGFont getSongAuthorFont( TGResourceFactory factory ){
		if( factory != null && ( this.songAuthorFont == null || this.songAuthorFont.isDisposed() ) ){
			this.songAuthorFont = factory.createFont(this.getResources().getDefaultFont().getName(), (8.0f * getFontScale()), true, false);
		}
		return this.songAuthorFont;
	}
	
	public TGFont getTrackNameFont( TGResourceFactory factory ){
		if( factory != null && ( this.trackNameFont == null || this.trackNameFont.isDisposed() ) ){
			this.trackNameFont = factory.createFont(this.getResources().getDefaultFont().getName(), (8.0f * getFontScale()), true, false);
		}
		return this.trackNameFont;
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
	}
	
	private class TempLine{
		protected float tempWith;
		protected int lastIndex;
		protected boolean fullLine;
		protected float maxY = 0;
		protected float minY = 50;
		protected List<Integer> measures;
		
		public TempLine(){
			this.measures = new ArrayList<Integer>();
		}
		
		protected void addMeasure(int index){
			this.measures.add(new Integer(index));
			this.lastIndex = index;
		}
	}
}
