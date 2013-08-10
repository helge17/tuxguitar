/*
 * Created on 04-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.printer;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGChordImpl;
import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLyricImpl;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.graphics.control.TGTrackSpacing;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGTrack;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
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
		paintHeader(this.document.getPainter());
		paintSong(this.document.getPainter(),null,this.document.getBounds().getX(), ( this.document.getBounds().getY() + Math.round(80.0f * getFontScale() ) ) );
		paintFooter(this.document.getPainter());
		this.closePage();
		
		this.document.finish();
	}
	
	public void paintSong(TGPainter painter,TGRectangle clientArea,int fromX,int fromY) {
		this.setWidth(0);
		this.setHeight(0);
		
		int style = getStyle();
		int posY = fromY + getFirstTrackSpacing();
		int height = getFirstTrackSpacing();
		int lineHeight = 0;
		
		TGTrackImpl track = (TGTrackImpl)getSongManager().getTrack(this.styles.getTrackNumber());
		((TGLyricImpl)track.getLyrics()).start(getSkippedBeats(track));
		
		TGTrackSpacing ts = new TGTrackSpacing(this) ;
		TempLine line = getTempLines(track,( this.styles.getFromMeasure() - 1 ),ts);
		while(!line.measures.isEmpty()){
			
			ts.setSize(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES, ((style & DISPLAY_SCORE) != 0?( (getScoreLineSpacing() * 5) ):0));
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
				posY = this.document.getBounds().getY() + getFirstTrackSpacing();
			}
			
			//pinto la linea
			paintLine(track,line,painter,fromX,posY,ts);
			
			posY += lineHeight + getTrackSpacing();
			height += lineHeight + getTrackSpacing();
			
			ts = new TGTrackSpacing(this) ;
			line = getTempLines(track,( line.lastIndex + 1 ),ts);
		}
		this.setHeight(height);
	}
	
	public void paintHeader(TGPainter painter){
		if(this.document.isPaintable(this.page) ){
			int x = this.document.getBounds().getX();
			int y = this.document.getBounds().getY();
			String songName = getSongManager().getSong().getName();
			String songAuthor = getSongManager().getSong().getAuthor();
			String trackName = "(" + getSongManager().getTrack(this.styles.getTrackNumber()).getName() + ")";
			
			if(songName == null || songName.length() == 0){
				songName = TuxGuitar.getProperty("print-header.default-song-name");
			}
			if(songAuthor == null || songAuthor.length() == 0){
				songAuthor = TuxGuitar.getProperty("print-header.default-song-author");
			}
			painter.setFont(getSongNameFont(painter));
			painter.drawString(songName,(x + getCenter(painter,songName)),y);
			painter.setFont(getTrackNameFont(painter));
			painter.drawString(trackName,(x + getCenter(painter,trackName)),(y + Math.round(30.0f * getFontScale())));
			painter.setFont(getSongAuthorFont(painter));
			painter.drawString(songAuthor,(x + getRight(painter,songAuthor)),(y + Math.round(50.0f * getFontScale())));
		}
	}
	
	private void paintFooter(TGPainter painter){
		if(this.document.isPaintable(this.page) ){
			int x = this.document.getBounds().getX();
			int y = this.document.getBounds().getY();
			String pageNumber = Integer.toString(this.page);
			
			painter.setBackground(getResources().getColorWhite());
			painter.setForeground(getResources().getColorBlack());
			painter.drawString(pageNumber,(x + getRight(painter,pageNumber)),(y + getBottom(painter,pageNumber)));
		}
	}
	
	public void paintLine(TGTrackImpl track,TempLine line,TGPainter painter,int fromX, int fromY,TGTrackSpacing ts) {
		if(this.document.isPaintable(this.page) ){
			int posX = fromX;
			int posY = fromY;
			int width = 0;
			
			int measureSpacing = 0;
			if(line.fullLine){
				int diff = ( getMaxWidth() - line.tempWith);
				if(diff != 0 && line.measures.size() > 0){
					measureSpacing = diff / line.measures.size();
				}
			}
			
			for(int i = 0;i < line.measures.size();i ++){
				int index = ((Integer)line.measures.get(i)).intValue();
				TGMeasureImpl currMeasure = (TGMeasureImpl)track.getMeasure(index);
				
				//asigno la posicion dentro del compas
				currMeasure.setPosX(posX);
				currMeasure.setPosY(posY);
				currMeasure.setTs(ts);
				
				((TGLyricImpl)track.getLyrics()).setCurrentMeasure(currMeasure);
				
				currMeasure.setFirstOfLine(i == 0);
				
				paintMeasure(currMeasure,painter,measureSpacing);
				((TGLyricImpl)track.getLyrics()).paintCurrentNoteBeats(painter,this,currMeasure,posX, posY);
				
				int measureWidth = ( currMeasure.getWidth(this) + currMeasure.getSpacing() );
				posX += measureWidth;
				width += measureWidth;
			}
			this.setWidth(Math.max(getWidth(),width));
		}
	}
	
	private void openPage(){
		this.page ++;
		if(this.document.isPaintable(this.page)){
			this.document.pageStart();
		}
	}
	
	private void closePage(){
		if(this.document.isPaintable(this.page)){
			this.document.pageFinish();
		}
	}
	
	private int getCenter(TGPainter painter,String text){
		return ((getMaxWidth() - painter.getFMWidth(text)) / 2);
	}
	
	private int getRight(TGPainter painter,String text){
		return ((getMaxWidth() - painter.getFMWidth(text)));
	}
	
	private int getBottom(TGPainter painter,String text){
		return ((getMaxHeight() - painter.getFMHeight()));
	}
	
	private TempLine getTempLines(TGTrack track,int fromIndex,TGTrackSpacing ts) {
		TempLine line = new TempLine();
		int measureCount = track.countMeasures();
		for (int measureIdx = fromIndex; measureIdx < measureCount; measureIdx++) {
			TGMeasureImpl measure= (TGMeasureImpl) track.getMeasure(measureIdx);
			if( measure.getNumber() >= this.styles.getFromMeasure() && measure.getNumber() <= this.styles.getToMeasure()){
				
				//verifico si tengo que bajar de linea
				if((line.tempWith + measure.getWidth(this)) >=  getMaxWidth() && !line.measures.isEmpty()){
					line.fullLine = true;
					return line;
				}
				line.tempWith +=  measure.getWidth(this);
				line.maxY = (measure.getMaxY() > line.maxY)?measure.getMaxY():line.maxY;
				line.minY = (measure.getMinY() < line.minY)?measure.getMinY():line.minY;
				
				line.addMeasure(measureIdx);
				measure.registerSpacing(this,ts);
			}
		}
		
		return line;
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
	
	public void setLineStyle(TGPainter painter){
		painter.setLineWidth(1);
		painter.setForeground( getDarkColor( getResources().getLineColor() ) );
	}
	
	public void setMeasureNumberStyle(TGPainter painter){
		painter.setFont(getResources().getDefaultFont());
		painter.setBackground(getResources().getColorWhite());
		painter.setForeground(getDarkColor(getResources().getColorRed()));
	}
	
	public void setDivisionsStyle(TGPainter painter, boolean fill){
		painter.setFont(getResources().getDefaultFont());
		painter.setBackground( (fill ? getResources().getColorBlack() :getResources().getColorWhite() ));
		painter.setForeground(getResources().getColorBlack());
	}
	
	public void setTempoStyle(TGPainter painter, boolean fontStyle){
		painter.setFont(getResources().getDefaultFont());
		painter.setForeground(getResources().getColorBlack());
		painter.setBackground( ( fontStyle ? getResources().getColorWhite() : getResources().getColorBlack() ));
	}
	
	public void setTripletFeelStyle(TGPainter painter, boolean fontStyle){
		painter.setFont(getResources().getDefaultFont());
		painter.setForeground(getResources().getColorBlack());
		painter.setBackground( ( fontStyle ? getResources().getColorWhite() : getResources().getColorBlack() ));
	}
	
	public void setTabNoteStyle(TGPainter painter,boolean playMode){
		painter.setBackground( getResources().getColorWhite() );
		painter.setForeground( getDarkColor(getResources().getTabNoteColor()) );
		painter.setFont(getResources().getNoteFont());
	}
	
	public void setTabNoteFooterStyle(TGPainter painter){
		painter.setForeground( getDarkColor(getResources().getTabNoteColor()));
		painter.setBackground( getDarkColor(getResources().getTabNoteColor()));
	}
	
	public void setTabEffectStyle(TGPainter painter){
		painter.setForeground( getDarkColor(getResources().getTabNoteColor()));
		painter.setBackground( getDarkColor(getResources().getTabNoteColor()));
	}
	
	public void setScoreNoteStyle(TGPainter painter,boolean playing){
		painter.setBackground( getDarkColor(getResources().getScoreNoteColor()) );
		painter.setForeground( getDarkColor(getResources().getScoreNoteColor()) );
	}
	
	public void setScoreNoteFooterStyle(TGPainter painter){
		painter.setForeground( getDarkColor(getResources().getScoreNoteColor()) );
		painter.setBackground( getDarkColor(getResources().getScoreNoteColor()) );
	}
	
	public void setScoreEffectStyle(TGPainter painter){
		painter.setForeground( getDarkColor(getResources().getScoreNoteColor()) );
		painter.setBackground( getDarkColor(getResources().getScoreNoteColor()) );
	}
	
	public void setTimeSignatureStyle(TGPainter painter){
		painter.setFont(getResources().getTimeSignatureFont());
		painter.setBackground( getResources().getColorWhite() );
		painter.setForeground( getResources().getColorBlack() );
	}
	
	public void setTabGraceStyle(TGPainter painter){
		painter.setFont(getResources().getGraceFont());
		painter.setBackground( getResources().getColorWhite() );
		painter.setForeground( getDarkColor(getResources().getTabNoteColor()) );
	}
	
	public void setLyricStyle(TGPainter painter,boolean playMode){
		painter.setFont(getResources().getLyricFont());
		painter.setBackground( getResources().getColorWhite() );
		painter.setForeground(getResources().getColorBlack());
	}
	
	public void setMarkerStyle(TGPainter painter, TGColor color){
		painter.setFont(getResources().getMarkerFont());
		painter.setBackground(getResources().getColorWhite());
		painter.setForeground(getDarkColor(color));
	}
	
	public void setTextStyle(TGPainter painter){
		painter.setFont(getResources().getTextFont());
		painter.setBackground( getResources().getColorWhite() );
		painter.setForeground( getResources().getColorBlack() );
	}
	
	public void setOfflineEffectStyle(TGPainter painter){
		painter.setFont(getResources().getDefaultFont());
		painter.setBackground( getResources().getColorWhite() );
		painter.setForeground( getResources().getColorBlack() );
	}
	
	public void setDivisionTypeStyle(TGPainter painter){
		painter.setFont(getResources().getDefaultFont());
		painter.setBackground( getResources().getColorWhite() );
		painter.setForeground( getResources().getColorBlack() );
	}
	
	public void setRepeatEndingStyle(TGPainter painter){
		painter.setFont(getResources().getDefaultFont());
		painter.setBackground( getResources().getColorWhite() );
		painter.setForeground( getResources().getColorBlack() );
	}
	
	public void setChordStyle(TGChordImpl chord){
		chord.setStyle(getStyle());
		chord.setFont(getResources().getChordFont());
		chord.setForegroundColor(getResources().getColorBlack());
		chord.setBackgroundColor(getResources().getColorWhite());
		chord.setColor(getDarkColor(getResources().getLineColor()));
		chord.setNoteColor(getDarkColor(getResources().getTabNoteColor()));
		chord.setTonicColor(getDarkColor(getResources().getTabNoteColor()));
		chord.setFretSpacing(getChordFretSpacing());
		chord.setStringSpacing(getChordStringSpacing());
		chord.setNoteSize(getChordNoteSize());
		chord.setFirstFretSpacing(getChordFretIndexSpacing());
		chord.setFirstFretFont(getResources().getChordFretFont());
	}
	
	public TGColor getDarkColor( TGColor color ) {
		return ( this.styles.isBlackAndWhite() ? getResources().getColorBlack() : color );
	}
	
	public int getMaxWidth(){
		return (this.document.getBounds().getWidth() - this.document.getBounds().getX() - 10);
	}
	
	public int getMaxHeight(){
		return (this.document.getBounds().getHeight() - this.document.getBounds().getY() - 10);
	}
	
	public boolean isFirstMeasure(TGMeasureHeader mh){
		return (mh.getNumber() == this.styles.getFromMeasure());
	}
	
	public boolean isLastMeasure(TGMeasureHeader mh){
		return (mh.getNumber() == this.styles.getToMeasure());
	}
	
	/*
	private int getScaledValue(float scale, int value){
		return getScaledValue(scale, value,1);
	}
	
	private int getScaledValue(float scale, int value, int minimum){
		float scaledValue = ( value * scale );
		return Math.max( Math.round( scaledValue ) , minimum );
	}
	*/
	public TGFont getSongNameFont( TGResourceFactory factory ){
		if( factory != null && ( this.songNameFont == null || this.songNameFont.isDisposed() ) ){
			this.songNameFont = factory.createFont(this.getResources().getDefaultFont().getName(),Math.round(16.0f * getFontScale()), true, false);
		}
		return this.songNameFont;
	}
	
	public TGFont getSongAuthorFont( TGResourceFactory factory ){
		if( factory != null && ( this.songAuthorFont == null || this.songAuthorFont.isDisposed() ) ){
			this.songAuthorFont = factory.createFont(this.getResources().getDefaultFont().getName(),Math.round(8.0f * getFontScale()), true, false);
		}
		return this.songAuthorFont;
	}
	
	public TGFont getTrackNameFont( TGResourceFactory factory ){
		if( factory != null && ( this.trackNameFont == null || this.trackNameFont.isDisposed() ) ){
			this.trackNameFont = factory.createFont(this.getResources().getDefaultFont().getName(),Math.round(8.0f * getFontScale()), true, false);
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
		protected int tempWith;
		protected int lastIndex;
		protected boolean fullLine;
		protected int maxY = 0;
		protected int minY = 50;
		protected List measures;
		
		public TempLine(){
			this.measures = new ArrayList();
		}
		
		protected void addMeasure(int index){
			this.measures.add(new Integer(index));
			this.lastIndex = index;
		}
	}
}
