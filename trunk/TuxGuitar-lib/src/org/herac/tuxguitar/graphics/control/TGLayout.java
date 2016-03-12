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
	public static final int DISPLAY_MODE_BLACK_WHITE = 0x40;
	
	private int style;
	private float scale;
	private float fontScale;
	private float width;
	private float height;
	
	private float minBufferSeparator;
	private float minTopSpacing;
	private float minScoreTabSpacing;
	private float stringSpacing;
	private float scoreLineSpacing;
	private float trackSpacing;
	private float firstTrackSpacing;
	private float firstMeasureSpacing;
	private float chordFretIndexSpacing;
	private float chordStringSpacing;
	private float chordFretSpacing;
	private float chordNoteSize;
	private float chordLineWidth;
	private float repeatEndingSpacing;
	private float effectSpacing;
	private float divisionTypeSpacing;
	private float textSpacing;
	private float markerSpacing;
	private float loopMarkerSpacing;
	private boolean bufferEnabled;
	private boolean playModeEnabled;
	
	private List<TrackPosition> trackPositions;
	
	private TGController controller;
	private TGResources resources;
	private TGLayoutStyles styles;
	
	public TGLayout(TGController controller,int style){
		this.controller = controller;
		this.trackPositions = new ArrayList<TrackPosition>();
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
		this.loadStyles(scale, scale);
	}
	
	public void loadStyles(float scale, float fontScale){
		this.setScale(scale);
		this.setFontScale(fontScale);
		this.getComponent().configureStyles(this.styles);
		
		this.setBufferEnabled( this.styles.isBufferEnabled() );
		this.setStringSpacing( (this.styles.getStringSpacing() * getScale() ) );
		this.setScoreLineSpacing( (this.styles.getScoreLineSpacing() * getScale() ) );
		this.setFirstMeasureSpacing( (this.styles.getFirstMeasureSpacing() * getScale() ) );
		this.setMinBufferSeparator( (this.styles.getMinBufferSeparator() * getScale() ) );
		this.setMinTopSpacing( (this.styles.getMinTopSpacing() * getScale() ) );
		this.setMinScoreTabSpacing( (this.styles.getMinScoreTabSpacing() * getScale() ) );
		this.setFirstTrackSpacing( (this.styles.getFirstTrackSpacing() * getScale() ) );
		this.setTrackSpacing( (this.styles.getTrackSpacing() * getScale() ) );
		this.setChordFretIndexSpacing( (this.styles.getChordFretIndexSpacing() * getScale() ) );
		this.setChordStringSpacing( (this.styles.getChordStringSpacing() * getScale() ) );
		this.setChordFretSpacing( (this.styles.getChordFretSpacing() * getScale() ) );
		this.setChordNoteSize( (this.styles.getChordNoteSize() * getScale() ) );
		this.setChordLineWidth( (this.styles.getChordLineWidth() * getScale() ) );
		this.setRepeatEndingSpacing( (this.styles.getRepeatEndingSpacing() * getScale() ) );
		this.setTextSpacing( (this.styles.getTextSpacing() * getScale() ) );
		this.setMarkerSpacing( (this.styles.getMarkerSpacing() * getScale() ) );
		this.setLoopMarkerSpacing( (this.styles.getLoopMarkerSpacing() * getScale() ) );
		this.setDivisionTypeSpacing( (this.styles.getDivisionTypeSpacing() * getScale() ) );
		this.setEffectSpacing( (this.styles.getEffectSpacing() * getScale() ) );
		this.getResources().load(this.styles);
	}
	
	public abstract void paintSong(TGPainter painter,TGRectangle clientArea,float fromX,float fromY);
	
	public abstract int getMode();
	
	public void paint(TGPainter painter,TGRectangle clientArea,float fromX,float fromY){
		this.playModeEnabled = false;
		paintSong(painter,clientArea,fromX,fromY);
	}
	
	public void paintMeasure(TGMeasureImpl measure,TGPainter painter,float spacing) {
		measure.setSpacing(spacing);
		measure.paintMeasure(this, painter);
	}
	
	public void updateSong(){
		this.getResourceBuffer().clearRegistry();
		this.updateMeasures();
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
	public void paintLines(TGTrackImpl track,TGTrackSpacing ts,TGPainter painter,float x,float y,float width) {
		if(width > 0){
			setLineStyle(painter);
			float tempX = ((x < 0)?0:x);
			float tempY = y;
			
			//partitura
			if( (this.style & DISPLAY_SCORE) != 0 ){
				float posY = tempY + ts.getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES);
				
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
	public void paintPlayMode(TGPainter painter, TGMeasureImpl measure, TGBeatImpl beat){
		this.playModeEnabled = true;
		
		//pinto el compas
		measure.paintPlayMode(this, painter);
		
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
		float checkPosition = -1;
		float minBufferSeparator = getMinBufferSeparator();
		if( (this.style & DISPLAY_SCORE) != 0 ){
			float bufferSeparator = (ts.getPosition(TGTrackSpacing.POSITION_SCORE_UP_LINES) - ts.getPosition(TGTrackSpacing.POSITION_BUFFER_SEPARATOR));
			if( bufferSeparator < minBufferSeparator ) {
				ts.setSize(TGTrackSpacing.POSITION_BUFFER_SEPARATOR,minBufferSeparator - bufferSeparator);
			}
			checkPosition = ts.getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES);
		}
		else if((this.style & DISPLAY_TABLATURE) != 0){
			float bufferSeparator = (ts.getPosition(TGTrackSpacing.POSITION_TABLATURE) - ts.getPosition(TGTrackSpacing.POSITION_BUFFER_SEPARATOR));
			if( bufferSeparator < minBufferSeparator ) {
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
	public float getSpacingForQuarter(TGDuration duration){
		return (((float)TGDuration.QUARTER_TIME / (float)duration.getTime()) * getMinSpacing(duration));
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
	
	public float getMinBeatWidth() {
		return (17.0f * getScale());
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
					return this.getMinBeatWidth();
			}
		}
		return (20.0f * scale);
	}
	
	public float getScoreNoteWidth() {
		return (this.getScoreLineSpacing() * 1.085f);
	}
	
	public boolean isPlayModeEnabled(){
		return this.playModeEnabled;
	}
	
	public void setMeasureNumberStyle(TGPainter painter){
		painter.setFont(getResources().getDefaultFont());
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
		painter.setForeground(getDarkColor(getResources().getColorRed()));
	}
	
	public void setDivisionsStyle(TGPainter painter, boolean fill){
		painter.setFont(getResources().getDefaultFont());
		painter.setBackground( (fill ? getResources().getColorBlack() : getLightColor(getResources().getBackgroundColor())));
		painter.setForeground(getResources().getColorBlack());
	}
	
	public void setTempoStyle(TGPainter painter, boolean fontStyle){
		painter.setFont(getResources().getDefaultFont());
		painter.setForeground(getResources().getColorBlack());
		painter.setBackground( ( fontStyle ? getLightColor(getResources().getBackgroundColor()) : getResources().getColorBlack() ));
	}
	
	public void setTripletFeelStyle(TGPainter painter, boolean fontStyle){
		painter.setFont(getResources().getDefaultFont());
		painter.setForeground(getResources().getColorBlack());
		painter.setBackground( ( fontStyle ? getLightColor(getResources().getBackgroundColor()) : getResources().getColorBlack() ));
	}
	
	public void setMeasurePlayingStyle(TGPainter painter){
		painter.setBackground(getResources().getBackgroundColor());
		painter.setForeground(getResources().getColorBlack());
	}
	
	public void setLyricStyle(TGPainter painter,boolean playMode){
		painter.setFont(getResources().getLyricFont());
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
		painter.setForeground( (playMode ? getResources().getPlayNoteColor() : getResources().getColorBlack()) );
	}
	
	public void setMarkerStyle(TGPainter painter, TGColor color){
		painter.setFont(getResources().getMarkerFont());
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
		painter.setForeground(getDarkColor(color));
	}
	
	public void setTextStyle(TGPainter painter){
		painter.setFont(getResources().getTextFont());
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
		painter.setForeground(getResources().getColorBlack());
	}
	
	public void setTimeSignatureStyle(TGPainter painter){
		painter.setFont(getResources().getTimeSignatureFont());
		painter.setForeground(getResources().getColorBlack());
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
	}
	
	public void setKeySignatureStyle(TGPainter painter){
		painter.setBackground(getResources().getColorBlack());
	}
	
	public void setClefStyle(TGPainter painter){
		painter.setBackground(getResources().getColorBlack());
	}
	
	public void setLineStyle(TGPainter painter){
		painter.setLineWidth(TGPainter.THINNEST_LINE_WIDTH);
		painter.setForeground(getDarkColor( getResources().getLineColor()));
	}
	
	public void setScoreSilenceStyle(TGPainter painter,boolean playMode){
		painter.setForeground( (playMode ? getResources().getPlayNoteColor() : getDarkColor(getResources().getScoreNoteColor())));
		painter.setBackground( (playMode ? getResources().getPlayNoteColor() : getDarkColor(getResources().getScoreNoteColor())));
	}
	
	public void setTabSilenceStyle(TGPainter painter,boolean playMode){
		painter.setForeground( (playMode ? getResources().getPlayNoteColor() : getDarkColor(getResources().getTabNoteColor()) ));
		painter.setBackground( (playMode ? getResources().getPlayNoteColor() : getDarkColor(getResources().getTabNoteColor()) ));
	}
	
	public void setScoreNoteStyle(TGPainter painter,boolean playing){
		painter.setForeground( (playing ? getResources().getPlayNoteColor() : getDarkColor(getResources().getScoreNoteColor())));
		painter.setBackground( (playing ? getResources().getPlayNoteColor() : getDarkColor(getResources().getScoreNoteColor())));
	}
	
	public void setScoreNoteFooterStyle(TGPainter painter){
		painter.setForeground( getDarkColor(getResources().getScoreNoteColor()) );
		painter.setBackground( getDarkColor(getResources().getScoreNoteColor()) );
	}
	
	public void setScoreEffectStyle(TGPainter painter){
		painter.setForeground( getDarkColor(getResources().getScoreNoteColor()) );
		painter.setBackground( getDarkColor(getResources().getScoreNoteColor()) );
	}
	
	public void setTabNoteStyle(TGPainter painter,boolean playMode){
		painter.setForeground( (playMode ? getResources().getPlayNoteColor() : getDarkColor(getResources().getTabNoteColor())));
		painter.setBackground( getLightColor(getResources().getBackgroundColor()) );
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
	
	public void setTabGraceStyle(TGPainter painter){
		painter.setFont(getResources().getGraceFont());
		painter.setForeground(getDarkColor(getResources().getTabNoteColor()));
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
	}
	
	public void setPlayNoteColor(TGPainter painter){
		painter.setForeground(getResources().getPlayNoteColor());
		painter.setBackground(getResources().getPlayNoteColor());
	}

	public void setOfflineEffectStyle(TGPainter painter){
		painter.setForeground(getDarkColor(((getStyle() & TGLayout.DISPLAY_SCORE) != 0 ? getResources().getScoreNoteColor() : getResources().getTabNoteColor())));
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
		painter.setFont(getResources().getDefaultFont());
	}
	
	public void setDotStyle(TGPainter painter){
		painter.setForeground(((getStyle() & TGLayout.DISPLAY_SCORE) != 0 ? getResources().getScoreNoteColor() : getResources().getTabNoteColor()));
		painter.setBackground(((getStyle() & TGLayout.DISPLAY_SCORE) != 0 ? getResources().getScoreNoteColor() : getResources().getTabNoteColor()));
	}
	
	public void setDivisionTypeStyle(TGPainter painter){
		painter.setForeground(getResources().getColorBlack());
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
		painter.setFont(getResources().getDefaultFont());
	}
	
	public void setRepeatEndingStyle(TGPainter painter){
		painter.setForeground(getResources().getColorBlack());
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
		painter.setFont(getResources().getDefaultFont());
	}
	
	public void setChordStyle(TGChordImpl chord){
		chord.setFont(getResources().getChordFont());
		chord.setForegroundColor(getResources().getColorBlack());
		chord.setBackgroundColor(getResources().getBackgroundColor());
		chord.setColor(getDarkColor(getResources().getLineColor()));
		chord.setNoteColor(getDarkColor(getResources().getTabNoteColor()));
		chord.setTonicColor(getDarkColor(getResources().getTabNoteColor()));
		chord.setStyle(this.style);
		chord.setFretSpacing(getChordFretSpacing());
		chord.setStringSpacing(getChordStringSpacing());
		chord.setNoteSize(getChordNoteSize());
		chord.setLineWidth(getChordLineWidth());
		chord.setFirstFretSpacing(getChordFretIndexSpacing());
		chord.setFirstFretFont(getResources().getChordFretFont());
	}
	
	public void setLoopSMarkerStyle(TGPainter painter){
		painter.setBackground(getResources().getLoopSMarkerColor());
	}
	
	public void setLoopEMarkerStyle(TGPainter painter){
		painter.setBackground(getResources().getLoopEMarkerColor());
	}
	
	public TGColor getDarkColor(TGColor color) {
		return ((this.getStyle() & TGLayout.DISPLAY_MODE_BLACK_WHITE) != 0 ? getResources().getColorBlack() : color );
	}
	
	public TGColor getLightColor(TGColor color) {
		return ((this.getStyle() & TGLayout.DISPLAY_MODE_BLACK_WHITE) != 0 ? getResources().getColorWhite() : color );
	}
	
	public TGRectangle getNoteOrientation(TGPainter painter, float x, float y, TGNote note){
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
		return getOrientation(painter, x, y, noteAsString);
	}
	
	public TGRectangle getOrientation(TGPainter painter, float x, float y, String s){
		float fmWidth = painter.getFMWidth(s);
		float fmTopLine = painter.getFMTopLine();
		float fmMiddleLine = painter.getFMMiddleLine();
		float fmBaseLine = painter.getFMBaseLine();
		
		return new TGRectangle((x - (fmWidth / 2f)), (y + fmMiddleLine), fmWidth, (fmBaseLine - fmTopLine));
	}
	
	public TGSongManager getSongManager() {
		return getComponent().getSongManager();
	}
	
	public TGSong getSong() {
		return getComponent().getSong();
	}
	
	public TGResourceBuffer getResourceBuffer() {
		return getComponent().getResourceBuffer();
	}
	
	public TGLayoutStyles getStyles() {
		return this.styles;
	}
	
	public TGController getComponent(){
		return this.controller;
	}
	
	public TGResources getResources(){
		return this.resources;
	}
	
	public float getHeight() {
		return this.height;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public void setWidth(float width) {
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
	
	public float getFirstMeasureSpacing() {
		return this.firstMeasureSpacing;
	}
	
	public void setFirstMeasureSpacing(float firstMeasureSpacing) {
		this.firstMeasureSpacing = firstMeasureSpacing;
	}
	
	public float getMinBufferSeparator() {
		return this.minBufferSeparator;
	}
	
	public void setMinBufferSeparator(float minBufferSeparator) {
		this.minBufferSeparator = minBufferSeparator;
	}
	
	public float getMinTopSpacing() {
		return this.minTopSpacing;
	}
	
	public void setMinTopSpacing(float minTopSpacing) {
		this.minTopSpacing = minTopSpacing;
	}
	
	public float getMinScoreTabSpacing() {
		return this.minScoreTabSpacing;
	}
	
	public void setMinScoreTabSpacing(float minScoreTabSpacing) {
		this.minScoreTabSpacing = minScoreTabSpacing;
	}
	
	public float getScoreLineSpacing() {
		return this.scoreLineSpacing;
	}
	
	public void setScoreLineSpacing(float scoreLineSpacing) {
		this.scoreLineSpacing = scoreLineSpacing;
	}
	
	public float getFirstTrackSpacing() {
		return this.firstTrackSpacing;
	}
	
	public void setFirstTrackSpacing(float firstTrackSpacing) {
		this.firstTrackSpacing = firstTrackSpacing;
	}
	
	public float getTrackSpacing() {
		return this.trackSpacing;
	}
	
	public void setTrackSpacing(float trackSpacing) {
		this.trackSpacing = trackSpacing;
	}
	
	public float getStringSpacing() {
		return this.stringSpacing;
	}
	
	public void setStringSpacing(float stringSpacing) {
		this.stringSpacing = stringSpacing;
	}
	
	public float getChordFretIndexSpacing() {
		return this.chordFretIndexSpacing;
	}
	
	public void setChordFretIndexSpacing(float chordFretIndexSpacing) {
		this.chordFretIndexSpacing = chordFretIndexSpacing;
	}
	
	public float getChordStringSpacing() {
		return this.chordStringSpacing;
	}
	
	public void setChordStringSpacing(float chordStringSpacing) {
		this.chordStringSpacing = chordStringSpacing;
	}
	
	public float getChordFretSpacing() {
		return this.chordFretSpacing;
	}
	
	public void setChordFretSpacing(float chordFretSpacing) {
		this.chordFretSpacing = chordFretSpacing;
	}
	
	public float getChordNoteSize() {
		return this.chordNoteSize;
	}
	
	public void setChordNoteSize(float chordNoteSize) {
		this.chordNoteSize = chordNoteSize;
	}
	
	public float getChordLineWidth() {
		return chordLineWidth;
	}

	public void setChordLineWidth(float chordLineWidth) {
		this.chordLineWidth = chordLineWidth;
	}

	public float getRepeatEndingSpacing() {
		return this.repeatEndingSpacing;
	}
	
	public void setRepeatEndingSpacing(float repeatEndingSpacing) {
		this.repeatEndingSpacing = repeatEndingSpacing;
	}
	
	public float getDivisionTypeSpacing() {
		return this.divisionTypeSpacing;
	}
	
	public void setDivisionTypeSpacing(float divisionTypeSpacing) {
		this.divisionTypeSpacing = divisionTypeSpacing;
	}
	
	public float getTextSpacing() {
		return this.textSpacing;
	}
	
	public void setTextSpacing(float textSpacing) {
		this.textSpacing = textSpacing;
	}
	
	public float getMarkerSpacing() {
		return this.markerSpacing;
	}
	
	public void setMarkerSpacing(float markerSpacing) {
		this.markerSpacing = markerSpacing;
	}
	
	public float getLoopMarkerSpacing() {
		return this.loopMarkerSpacing;
	}
	
	public void setLoopMarkerSpacing(float loopMarkerSpacing) {
		this.loopMarkerSpacing = loopMarkerSpacing;
	}
	
	public float getEffectSpacing() {
		return this.effectSpacing;
	}
	
	public void setEffectSpacing(float effectSpacing) {
		this.effectSpacing = effectSpacing;
	}
	
	public float getDefaultChordSpacing(){
		float spacing = 0;
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
	
	protected void addTrackPosition(int track, float posY, float height){
		this.trackPositions.add(new TrackPosition(track,posY,height));
	}
	
	public int getTrackNumberAt(float y){
		TrackPosition trackPos = getTrackPositionAt(y);
		return ((trackPos != null)?trackPos.getTrack():-1);
	}
	
	public TrackPosition getTrackPositionAt(float y){
		TrackPosition trackPos = null;
		float minorDistance = 0;
		
		Iterator<TrackPosition> it = this.trackPositions.iterator();
		while(it.hasNext()){
			TrackPosition pos = (TrackPosition)it.next();
			float distanceY = Math.min(Math.abs(y - (pos.getPosY())), Math.abs(y - (pos.getPosY() + pos.getHeight() - 10)));
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
		private float posY;
		private float height;
		
		public TrackPosition(int track, float posY, float height){
			this.track = track;
			this.posY = posY;
			this.height = height;
		}
		
		public float getPosY() {
			return this.posY;
		}
		
		public float getHeight() {
			return this.height;
		}
		
		public int getTrack() {
			return this.track;
		}
	}
}
