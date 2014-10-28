/*
 * Created on 04-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.graphics.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGVoice;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGLayout {
	
	public static final int MODE_VERTICAL = 1;
	public static final int MODE_HORIZONTAL = 2;
	public static final int DEFAULT_MODE = MODE_HORIZONTAL;
	
	public static final int DISPLAY_COMPACT = 0x01;
	public static final int DISPLAY_MULTITRACK = 0x02;
	public static final int DISPLAY_SCORE = 0x04;
	public static final int DISPLAY_TABLATURE = 0x08;
	public static final int DISPLAY_CHORD_NAME = 0x10;
	public static final int DISPLAY_CHORD_DIAGRAM = 0x20;
	
	private float scale;
	private float fontScale;
	private int style;
	private int width;
	private int height;
	
	private int minBufferSeparator;
	private int minTopSpacing;
	private int minScoreTabSpacing;
	private int stringSpacing;
	private int scoreLineSpacing;
	private int trackSpacing;
	private int firstTrackSpacing;
	private int firstMeasureSpacing;
	private int chordFretIndexSpacing;
	private int chordStringSpacing;
	private int chordFretSpacing;
	private int chordNoteSize;
	private int repeatEndingSpacing;
	private int effectSpacing;
	private int divisionTypeSpacing;
	private int textSpacing;
	private int markerSpacing;
	private int loopMarkerSpacing;
	private boolean bufferEnabled;
	private boolean playModeEnabled;
	
	private List trackPositions;
	
	private TGController controller;
	private TGResources resources;
	private TGLayoutStyles styles;
	
	public TGLayout(TGController controller,int style){
		this.controller = controller;
		this.trackPositions = new ArrayList();
		this.playModeEnabled = false;
		this.resources = new TGResources(this);
		this.styles = new TGLayoutStyles();
		this.style = style;
		if((this.style & DISPLAY_TABLATURE) == 0 && (this.style & DISPLAY_SCORE) == 0 ){
			this.style |= DISPLAY_TABLATURE;
		}
	}
	
	public void loadStyles(){
		this.loadStyles(1f);
	}
	
	public void loadStyles(float scale){
		this.setScale(scale);
		this.setFontScale(scale);
		this.getComponent().configureStyles(this.styles);
		
		this.setBufferEnabled( this.styles.isBufferEnabled() );
		this.setStringSpacing( (int)(this.styles.getStringSpacing() * getScale() ) );
		this.setScoreLineSpacing( (int)(this.styles.getScoreLineSpacing() * getScale() ) );
		this.setFirstMeasureSpacing( Math.round( this.styles.getFirstMeasureSpacing() * getScale() ) );
		this.setMinBufferSeparator( Math.round( this.styles.getMinBufferSeparator() * getScale() ) );
		this.setMinTopSpacing( Math.round( this.styles.getMinTopSpacing() * getScale() ) );
		this.setMinScoreTabSpacing( Math.round( this.styles.getMinScoreTabSpacing() * getScale() ) );
		this.setFirstTrackSpacing( Math.round(this.styles.getFirstTrackSpacing() * getScale() ) );
		this.setTrackSpacing( Math.round(this.styles.getTrackSpacing() * getScale() ) );
		this.setChordFretIndexSpacing( Math.round( this.styles.getChordFretIndexSpacing() * getScale() ) );
		this.setChordStringSpacing( Math.round( this.styles.getChordStringSpacing() * getScale() ) );
		this.setChordFretSpacing( Math.round( this.styles.getChordFretSpacing() * getScale() ) );
		this.setChordNoteSize( Math.round( this.styles.getChordNoteSize() * getScale() ) );
		this.setRepeatEndingSpacing( Math.round( this.styles.getRepeatEndingSpacing() * getScale() ) );
		this.setTextSpacing( Math.round( this.styles.getTextSpacing() * getScale() ) );
		this.setMarkerSpacing( Math.round( this.styles.getMarkerSpacing() * getScale() ) );
		this.setLoopMarkerSpacing( Math.round( this.styles.getLoopMarkerSpacing() * getScale() ) );
		this.setDivisionTypeSpacing( Math.round( this.styles.getDivisionTypeSpacing() * getScale() ) );
		this.setEffectSpacing( Math.round( this.styles.getEffectSpacing() * getScale() ) );
		
		this.getResources().load(this.styles);
	}
	
	public abstract void paintSong(TGPainter painter,TGRectangle clientArea,int fromX,int fromY);
	
	public abstract int getMode();
	
	public void paint(TGPainter painter,TGRectangle clientArea,int fromX,int fromY){
		this.playModeEnabled = false;
		paintSong(painter,clientArea,fromX,fromY);
	}
	
	public void paintMeasure(TGMeasureImpl measure,TGPainter painter,int spacing) {
		measure.setSpacing(spacing);
		measure.paintMeasure(this,painter);
	}
	
	public void updateSong(){
		updateMeasures();
	}
	
	public void updateMeasures() {
		int measureCount = getSong().countMeasureHeaders();
		for (int measureIdx = 0; measureIdx < measureCount; measureIdx++) {
			this.updateMeasureIndex( measureIdx );
		}
	}
	
	private void updateMeasureIndex(int index) {
		if( index >= 0 && index < getSong().countMeasureHeaders() ){
			((TGMeasureHeaderImpl) getSong().getMeasureHeader( index )).update(this, index);
			
			int trackCount = getSong().countTracks();
			for (int trackIdx = 0; trackIdx < trackCount; trackIdx++) {
				TGTrackImpl track = (TGTrackImpl) getSong().getTrack(trackIdx);
				TGMeasureImpl measure = (TGMeasureImpl) track.getMeasure( index );
				measure.create(this);
			}
			for (int trackIdx = 0; trackIdx < trackCount; trackIdx++) {
				TGTrackImpl track = (TGTrackImpl) getSong().getTrack(trackIdx);
				TGMeasureImpl measure = (TGMeasureImpl)track.getMeasure( index );
				track.update(this);
				measure.update(this);
			}
		}
	}
	
	public void updateMeasureNumber(int number) {
		TGMeasureHeader header = getSongManager().getMeasureHeader(getSong(), number);
		if( header != null ){
			int index = getSongManager().getMeasureHeaderIndex(getSong(), header);
			if( index >= 0 ){
				updateMeasureIndex(index);
			}
		}
	}
	
	/**
	 * Pinta las lineas
	 */
	public void paintLines(TGTrackImpl track,TGTrackSpacing ts,TGPainter painter,int x,int y,int width) {
		if(width > 0){
			setLineStyle(painter);
			int tempX = ((x < 0)?0:x);
			int tempY = y;
			
			//partitura
			if( (this.style & DISPLAY_SCORE) != 0 ){
				int posY = tempY + ts.getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES);
				
				painter.initPath();
				painter.setAntialias(false);
				for(int i = 1;i <= 5;i ++){
					painter.moveTo(tempX, posY);
					painter.lineTo(tempX + width,posY);
					posY += getScoreLineSpacing();
				}
				painter.closePath();
			}
			//tablatura
			if((this.style & DISPLAY_TABLATURE) != 0){
				tempY += ts.getPosition(TGTrackSpacing.POSITION_TABLATURE);
				
				painter.initPath();
				painter.setAntialias(false);
				for(int i = 0; i < track.stringCount();i++){
					painter.moveTo(tempX,tempY);
					painter.lineTo(tempX + width,tempY);
					tempY += getStringSpacing();
				}
				painter.closePath();
			}
		}
	}
	
	/**
	 * Pinta el compas y las notas que estan sonando
	 */
	public void paintPlayMode(TGPainter painter,TGMeasureImpl measure,TGBeatImpl beat,boolean paintMeasure){
		this.playModeEnabled = true;
		
		//pinto el compas
		if(paintMeasure){
			measure.paintMeasure(this,painter);
		}
		//pinto el pulso
		if( beat != null ){
			beat.paint(this,painter,measure.getPosX()  + measure.getHeaderImpl().getLeftSpacing(this), measure.getPosY());
		}
		
		//pinto los lyrics
		((TGLyricImpl)measure.getTrackImpl().getLyrics()).paintCurrentNoteBeats(painter,this,measure,measure.getPosX(), measure.getPosY());
		
		this.playModeEnabled = false;
	}
	
	protected float checkScale(){
		float v1 = ((this.style & DISPLAY_SCORE) != 0 ? (getScoreLineSpacing() * 1.25f ) : 0 );
		float v2 = ((this.style & DISPLAY_TABLATURE) != 0 ? getStringSpacing() : 0 );
		float scale = (Math.max(v1,v2) / 10.0f);
		return scale;
	}
	
	protected void checkDefaultSpacing(TGTrackSpacing ts){
		int checkPosition = -1;
		int minBufferSeparator = getMinBufferSeparator();
		if( (this.style & DISPLAY_SCORE) != 0 ){
			int bufferSeparator = (ts.getPosition(TGTrackSpacing.POSITION_SCORE_UP_LINES) - ts.getPosition(TGTrackSpacing.POSITION_BUFFER_SEPARATOR));
			if(bufferSeparator < minBufferSeparator ) {
				ts.setSize(TGTrackSpacing.POSITION_BUFFER_SEPARATOR,minBufferSeparator - bufferSeparator);
			}
			checkPosition = ts.getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES);
		}
		else if((this.style & DISPLAY_TABLATURE) != 0){
			int bufferSeparator = (ts.getPosition(TGTrackSpacing.POSITION_TABLATURE) - ts.getPosition(TGTrackSpacing.POSITION_BUFFER_SEPARATOR));
			if(bufferSeparator < minBufferSeparator ) {
				ts.setSize(TGTrackSpacing.POSITION_BUFFER_SEPARATOR,minBufferSeparator - bufferSeparator);
			}
			checkPosition = ts.getPosition(TGTrackSpacing.POSITION_TABLATURE);
		}
		
		if(checkPosition >= 0 && checkPosition < getMinTopSpacing()){
			ts.setSize(TGTrackSpacing.POSITION_TOP, (getMinTopSpacing() - checkPosition));
		}
	}
	
	/**
	 * Calcula el espacio minimo entre negras, dependiendo de la duracion de la nota 
	 */
	public int getSpacingForQuarter(TGDuration duration){
		double spacing = (((double)TGDuration.QUARTER_TIME / (double)duration.getTime()) * getMinSpacing(duration));
		return  (int)spacing;
	}
	
	/**
	 * Calcula el Espacio minimo que quedara entre nota y nota
	 */
	protected float getMinSpacing(TGDuration duration){
		float scale = getScale();
		switch(duration.getValue()){
			case TGDuration.WHOLE:
				return (50.0f * scale);
			case TGDuration.HALF:
				return (30.0f * scale);
			case TGDuration.QUARTER:
				return (25.0f * scale);
			case TGDuration.EIGHTH:
				return (20.0f * scale);
			default:
				return (18.0f * scale);
		}
	}
	
	/**
	 * Calcula el Espacio que ocupara el pulso
	 */
	public float getBeatWidth(TGVoice voice){
		float scale = getScale();
		TGDuration duration = voice.getDuration();
		if(duration != null){
			switch(duration.getValue()){
				case TGDuration.WHOLE:
					return (30.0f * scale);
				case TGDuration.HALF:
					return (25.0f * scale);
				case TGDuration.QUARTER:
					return (21.0f * scale);
				case TGDuration.EIGHTH:
					return (20.0f * scale);
				case TGDuration.SIXTEENTH:
					return (19.0f * scale);
				case TGDuration.THIRTY_SECOND:
					return (18.0f * scale);
				default:
					return (17.0f * scale);
			}
		}
		return (20.0f * scale);
	}
	
	/**
	 * Calcula el Espacio que ocupara el pulso
	 */
	public float getVoiceWidth(TGVoiceImpl voice){
		float scale = getScale();
		TGDuration duration = voice.getDuration();
		if(duration != null){
			switch(duration.getValue()){
				case TGDuration.WHOLE:
					return (30.0f * scale);
				case TGDuration.HALF:
					return (25.0f * scale);
				case TGDuration.QUARTER:
					return (21.0f * scale);
				case TGDuration.EIGHTH:
					return (20.0f * scale);
				case TGDuration.SIXTEENTH:
					return (19.0f * scale);
				case TGDuration.THIRTY_SECOND:
					return (18.0f * scale);
				default:
					return (17.0f * scale);
			}
		}
		return (20.0f * scale);
	}
	
	public boolean isPlayModeEnabled(){
		return this.playModeEnabled;
	}
	
	public void setMeasureNumberStyle(TGPainter painter){
		painter.setFont(getResources().getDefaultFont());
		painter.setBackground(getResources().getBackgroundColor());
		painter.setForeground(getResources().getColorRed());
	}
	
	public void setDivisionsStyle(TGPainter painter, boolean fill){
		painter.setFont(getResources().getDefaultFont());
		painter.setBackground( (fill ? getResources().getColorBlack() :getResources().getBackgroundColor() ));
		painter.setForeground(getResources().getColorBlack());
	}
	
	public void setTempoStyle(TGPainter painter, boolean fontStyle){
		painter.setFont(getResources().getDefaultFont());
		painter.setForeground(getResources().getColorBlack());
		painter.setBackground( ( fontStyle ? getResources().getBackgroundColor() : getResources().getColorBlack() ));
	}
	
	public void setTripletFeelStyle(TGPainter painter, boolean fontStyle){
		painter.setFont(getResources().getDefaultFont());
		painter.setForeground(getResources().getColorBlack());
		painter.setBackground( ( fontStyle ? getResources().getBackgroundColor() : getResources().getColorBlack() ));
	}
	
	public void setMeasurePlayingStyle(TGPainter painter){
		painter.setBackground(getResources().getBackgroundColor());
		painter.setForeground(getResources().getColorBlack());
	}
	
	public void setLyricStyle(TGPainter painter,boolean playMode){
		painter.setFont(getResources().getLyricFont());
		painter.setBackground(getResources().getBackgroundColor());
		painter.setForeground( (playMode ? getResources().getPlayNoteColor() : getResources().getColorBlack()) );
	}
	
	public void setMarkerStyle(TGPainter painter, TGColor color){
		painter.setFont(getResources().getMarkerFont());
		painter.setBackground(getResources().getBackgroundColor());
		painter.setForeground(color);
	}
	
	public void setTextStyle(TGPainter painter){
		painter.setFont(getResources().getTextFont());
		painter.setBackground(getResources().getBackgroundColor());
		painter.setForeground(getResources().getColorBlack());
	}
	
	public void setTimeSignatureStyle(TGPainter painter){
		painter.setFont(getResources().getTimeSignatureFont());
		painter.setForeground(getResources().getColorBlack());
		painter.setBackground(getResources().getBackgroundColor());
	}
	
	public void setKeySignatureStyle(TGPainter painter){
		painter.setBackground(getResources().getColorBlack());
	}
	
	public void setClefStyle(TGPainter painter){
		painter.setBackground(getResources().getColorBlack());
	}
	
	public void setLineStyle(TGPainter painter){
		painter.setLineWidth(1);
		painter.setForeground(getResources().getLineColor());
	}
	
	public void setScoreSilenceStyle(TGPainter painter,boolean playMode){
		painter.setForeground( (playMode ? getResources().getPlayNoteColor() : getResources().getScoreNoteColor() ));
		painter.setBackground( (playMode ? getResources().getPlayNoteColor() : getResources().getScoreNoteColor() ));
	}
	
	public void setTabSilenceStyle(TGPainter painter,boolean playMode){
		painter.setForeground( (playMode ? getResources().getPlayNoteColor() : getResources().getTabNoteColor() ));
		painter.setBackground( (playMode ? getResources().getPlayNoteColor() : getResources().getTabNoteColor() ));
	}
	
	public void setScoreNoteStyle(TGPainter painter,boolean playing){
		painter.setForeground( (playing ? getResources().getPlayNoteColor() : getResources().getScoreNoteColor() ));
		painter.setBackground( (playing ? getResources().getPlayNoteColor() : getResources().getScoreNoteColor() ));
	}
	
	public void setScoreNoteFooterStyle(TGPainter painter){
		painter.setForeground( getResources().getScoreNoteColor());
		painter.setBackground( getResources().getScoreNoteColor());
	}
	
	public void setScoreEffectStyle(TGPainter painter){
		painter.setForeground( getResources().getScoreNoteColor());
		painter.setBackground( getResources().getScoreNoteColor());
	}
	
	public void setTabNoteStyle(TGPainter painter,boolean playMode){
		painter.setForeground( (playMode ? getResources().getPlayNoteColor() : getResources().getTabNoteColor() ));
		painter.setBackground( getResources().getBackgroundColor() );
		painter.setFont(getResources().getNoteFont());
	}
	
	public void setTabNoteFooterStyle(TGPainter painter){
		painter.setForeground( getResources().getTabNoteColor());
		painter.setBackground( getResources().getTabNoteColor());
	}
	
	public void setTabEffectStyle(TGPainter painter){
		painter.setForeground( getResources().getTabNoteColor());
		painter.setBackground( getResources().getTabNoteColor());
	}
	
	public void setTabGraceStyle(TGPainter painter){
		painter.setFont(getResources().getGraceFont());
		painter.setForeground(getResources().getTabNoteColor());
		painter.setBackground(getResources().getBackgroundColor());
	}
	
	public void setPlayNoteColor(TGPainter painter){
		painter.setForeground(getResources().getPlayNoteColor());
		painter.setBackground(getResources().getPlayNoteColor());
	}
	
	public void setOfflineEffectStyle(TGPainter painter){
		painter.setForeground(getResources().getColorBlack());
		painter.setBackground(getResources().getBackgroundColor());
		painter.setFont(getResources().getDefaultFont());
	}
	
	public void setDotStyle(TGPainter painter){
		painter.setForeground(getResources().getColorBlack());
		painter.setBackground(getResources().getColorBlack());
	}
	
	public void setDivisionTypeStyle(TGPainter painter){
		painter.setForeground(getResources().getColorBlack());
		painter.setBackground(getResources().getBackgroundColor());
		painter.setFont(getResources().getDefaultFont());
	}
	
	public void setRepeatEndingStyle(TGPainter painter){
		painter.setForeground(getResources().getColorBlack());
		painter.setBackground(getResources().getBackgroundColor());
		painter.setFont(getResources().getDefaultFont());
	}
	
	public void setChordStyle(TGChordImpl chord){
		chord.setFont(getResources().getChordFont());
		chord.setForegroundColor(getResources().getColorBlack());
		chord.setBackgroundColor(getResources().getBackgroundColor());
		chord.setColor(getResources().getLineColor());
		chord.setNoteColor(getResources().getTabNoteColor());
		chord.setTonicColor(getResources().getTabNoteColor());
		chord.setStyle(this.style);
		chord.setFretSpacing(getChordFretSpacing());
		chord.setStringSpacing(getChordStringSpacing());
		chord.setNoteSize(getChordNoteSize());
		chord.setFirstFretSpacing(getChordFretIndexSpacing());
		chord.setFirstFretFont(getResources().getChordFretFont());
	}
	
	public void setLoopSMarkerStyle(TGPainter painter){
		painter.setBackground(getResources().getLoopSMarkerColor());
	}
	
	public void setLoopEMarkerStyle(TGPainter painter){
		painter.setBackground(getResources().getLoopEMarkerColor());
	}
	
	public TGRectangle getNoteOrientation(TGPainter painter,int x,int y,TGNote note){
		String noteAsString = null;
		if (note.isTiedNote()){
			noteAsString = "L";
			noteAsString = (note.getEffect().isGhostNote())?"(" + noteAsString + ")":noteAsString;
		}else if(note.getEffect().isDeadNote()){
			noteAsString = "X";
			noteAsString = (note.getEffect().isGhostNote())?"(" + noteAsString + ")":noteAsString;
		}else{
			noteAsString = Integer.toString(note.getValue());
			noteAsString = (note.getEffect().isGhostNote())?"(" + noteAsString + ")":noteAsString;
		}
		return getOrientation(painter,x,y,noteAsString);
	}
	
	public TGRectangle getOrientation(TGPainter painter,int x,int y,String s){
		int fmWidth = painter.getFMWidth(s);
		int fmAscent = painter.getFMAscent();
		int fSize = painter.getFontSize();
		return new TGRectangle((x - (fmWidth / 2)),(y - (fmAscent - fSize) - (fSize / 2)),fmWidth, fSize );
		//TGDimension size = painter.getStringExtent(s);
		//return new TGRectangle((x - (size.getWidth() / 2)),(y - (size.getHeight() / 2)),size.getWidth(), size.getHeight() );
	}
	
	public TGSongManager getSongManager() {
		return getComponent().getSongManager();
	}
	
	public TGSong getSong() {
		return getComponent().getSong();
	}
	
	public void setComponent(TGController controller){
		this.controller = controller;
	}
	
	public TGController getComponent(){
		return this.controller;
	}
	
	public TGResources getResources(){
		return this.resources;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getStyle(){
		return this.style;
	}
	
	public void setStyle(int style){
		this.style = style;
	}
	
	public float getScale() {
		return this.scale;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public float getFontScale() {
		return this.fontScale;
	}
	
	public void setFontScale(float fontScale) {
		this.fontScale = fontScale;
	}
	
	public boolean isBufferEnabled() {
		return this.bufferEnabled;
	}
	
	public void setBufferEnabled(boolean bufferEnabled) {
		this.bufferEnabled = bufferEnabled;
	}
	
	public int getFirstMeasureSpacing() {
		return this.firstMeasureSpacing;
	}
	
	public void setFirstMeasureSpacing(int firstMeasureSpacing) {
		this.firstMeasureSpacing = firstMeasureSpacing;
	}
	
	public int getMinBufferSeparator() {
		return this.minBufferSeparator;
	}
	
	public void setMinBufferSeparator(int minBufferSeparator) {
		this.minBufferSeparator = minBufferSeparator;
	}
	
	public int getMinTopSpacing() {
		return this.minTopSpacing;
	}
	
	public void setMinTopSpacing(int minTopSpacing) {
		this.minTopSpacing = minTopSpacing;
	}
	
	public int getMinScoreTabSpacing() {
		return this.minScoreTabSpacing;
	}
	
	public void setMinScoreTabSpacing(int minScoreTabSpacing) {
		this.minScoreTabSpacing = minScoreTabSpacing;
	}
	
	public int getScoreLineSpacing() {
		return this.scoreLineSpacing;
	}
	
	public void setScoreLineSpacing(int scoreLineSpacing) {
		this.scoreLineSpacing = scoreLineSpacing;
	}
	
	public int getFirstTrackSpacing() {
		return this.firstTrackSpacing;
	}
	
	public void setFirstTrackSpacing(int firstTrackSpacing) {
		this.firstTrackSpacing = firstTrackSpacing;
	}
	
	public int getTrackSpacing() {
		return this.trackSpacing;
	}
	
	public void setTrackSpacing(int trackSpacing) {
		this.trackSpacing = trackSpacing;
	}
	
	public int getStringSpacing() {
		return this.stringSpacing;
	}
	
	public void setStringSpacing(int stringSpacing) {
		this.stringSpacing = stringSpacing;
	}
	
	public int getChordFretIndexSpacing() {
		return this.chordFretIndexSpacing;
	}
	
	public void setChordFretIndexSpacing(int chordFretIndexSpacing) {
		this.chordFretIndexSpacing = chordFretIndexSpacing;
	}
	
	public int getChordStringSpacing() {
		return this.chordStringSpacing;
	}
	
	public void setChordStringSpacing(int chordStringSpacing) {
		this.chordStringSpacing = chordStringSpacing;
	}
	
	public int getChordFretSpacing() {
		return this.chordFretSpacing;
	}
	
	public void setChordFretSpacing(int chordFretSpacing) {
		this.chordFretSpacing = chordFretSpacing;
	}
	
	public int getChordNoteSize() {
		return this.chordNoteSize;
	}
	
	public void setChordNoteSize(int chordNoteSize) {
		this.chordNoteSize = chordNoteSize;
	}
	
	public int getRepeatEndingSpacing() {
		return this.repeatEndingSpacing;
	}
	
	public void setRepeatEndingSpacing(int repeatEndingSpacing) {
		this.repeatEndingSpacing = repeatEndingSpacing;
	}
	
	public int getDivisionTypeSpacing() {
		return this.divisionTypeSpacing;
	}
	
	public void setDivisionTypeSpacing(int divisionTypeSpacing) {
		this.divisionTypeSpacing = divisionTypeSpacing;
	}
	
	public int getTextSpacing() {
		return this.textSpacing;
	}
	
	public void setTextSpacing(int textSpacing) {
		this.textSpacing = textSpacing;
	}
	
	public int getMarkerSpacing() {
		return this.markerSpacing;
	}
	
	public void setMarkerSpacing(int markerSpacing) {
		this.markerSpacing = markerSpacing;
	}
	
	public int getLoopMarkerSpacing() {
		return this.loopMarkerSpacing;
	}
	
	public void setLoopMarkerSpacing(int loopMarkerSpacing) {
		this.loopMarkerSpacing = loopMarkerSpacing;
	}
	
	public int getEffectSpacing() {
		return this.effectSpacing;
	}
	
	public void setEffectSpacing(int effectSpacing) {
		this.effectSpacing = effectSpacing;
	}
	
	public int getDefaultChordSpacing(){
		int spacing = 0;
		if( (this.style & DISPLAY_CHORD_DIAGRAM) != 0 ){
			spacing += ( (TGChordImpl.MAX_FRETS * getChordFretSpacing()) + getChordFretSpacing());
		}
		if( (this.style & DISPLAY_CHORD_NAME) != 0 ){
			spacing += Math.round( (15f * getScale()) );
		}
		return spacing;
	}
	
	public boolean isFirstMeasure(TGMeasureHeader mh){
		return (mh.getNumber() == 1);
	}
	
	public boolean isFirstMeasure(TGMeasure measure){
		return (isFirstMeasure(measure.getHeader()));
	}
	
	public boolean isLastMeasure(TGMeasureHeader mh){
		return (mh.getNumber() == getSong().countMeasureHeaders());
	}
	
	public boolean isLastMeasure(TGMeasure measure){
		return (isLastMeasure(measure.getHeader()));
	}
	
	public boolean hasLoopMarker(TGMeasureHeader mh){
		return (getComponent().isLoopSHeader(mh) || getComponent().isLoopEHeader(mh));
	}
	
	public boolean hasLoopMarker(TGMeasure measure){
		return (hasLoopMarker(measure.getHeader()));
	}
	
	protected void clearTrackPositions(){
		this.trackPositions.clear();
	}
	
	protected void addTrackPosition(int track,int posY,int height){
		this.trackPositions.add(new TrackPosition(track,posY,height));
	}
	
	public int getTrackNumberAt(int y){
		TrackPosition trackPos = getTrackPositionAt(y);
		return ((trackPos != null)?trackPos.getTrack():-1);
	}
	
	public TrackPosition getTrackPositionAt(int y){
		TrackPosition trackPos = null;
		int minorDistance = 0;
		
		Iterator it = this.trackPositions.iterator();
		while(it.hasNext()){
			TrackPosition pos = (TrackPosition)it.next();
			int distanceY = Math.min(Math.abs(y - (pos.getPosY())), Math.abs(y - (pos.getPosY() + pos.getHeight() - 10)));
			if(trackPos == null || distanceY < minorDistance){
				trackPos = pos;
				minorDistance = distanceY;
			}
		}
		return trackPos;
	}
	
	public void disposeLayout(){
		this.getResources().dispose();
	}
	
	public class TrackPosition{
		private int track;
		private int posY;
		private int height;
		
		public TrackPosition(int track,int posY,int height){
			this.track = track;
			this.posY = posY;
			this.height = height;
		}
		
		public int getPosY() {
			return this.posY;
		}
		
		public int getHeight() {
			return this.height;
		}
		
		public int getTrack() {
			return this.track;
		}
	}
}
