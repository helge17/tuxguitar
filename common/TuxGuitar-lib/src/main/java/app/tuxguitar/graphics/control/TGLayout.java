package app.tuxguitar.graphics.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIRectangle;

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
	public static final int HIGHLIGHT_PLAYED_BEAT = 0x80;

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
	private float firstNoteSpacing;
	private float measureLeftSpacing;
	private float measureRightSpacing;
	private float clefSpacing;
	private float keySignatureSpacing;
	private float timeSignatureSpacing;
	private float chordFretIndexSpacing;
	private float chordStringSpacing;
	private float chordFretSpacing;
	private float chordNoteSize;
	private float chordLineWidth;
	private float repeatEndingSpacing;
	private float effectSpacing;
	private float divisionTypeSpacing;
	private float pickStrokeSpacing;
	private float bendSpacing;
	private float textSpacing;
	private float markerSpacing;
	private float loopMarkerSpacing;
	private float[] lineWidths;
	private float[] durationWidths;
	private boolean bufferEnabled;
	private boolean playModeEnabled;
	private boolean tabNotePathRendererEnabled;

	private List<TrackPosition> trackPositions;

	private TGController controller;
	private TGResources resources;
	private TGDrumMap drumMap;
	private Map<TGMeasureHeader, List<TGMeasureHeader>> updateDependants;

	public void addUpdateDependant(TGMeasureHeader header, TGMeasureHeader dependant) {
		if(!this.updateDependants.containsKey(header)) {
			this.updateDependants.put(header, new ArrayList<TGMeasureHeader>());
		}
		List<TGMeasureHeader> updateDependants = this.updateDependants.get(header);
		if(!updateDependants.contains(dependant)) {
			updateDependants.add(dependant);
		}
	}

	public TGLayout(TGController controller,int style) {
		this.controller = controller;
		this.trackPositions = new ArrayList<TrackPosition>();
		this.playModeEnabled = false;
		this.resources = new TGResources(this);
		this.drumMap = new TGDrumMap();
		this.updateDependants = new HashMap<TGMeasureHeader, List<TGMeasureHeader>>();
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
		TGLayoutStyles styles = this.getComponent().getStyles();

		this.scale = scale;
		this.fontScale = fontScale;

		this.bufferEnabled = styles.isBufferEnabled();
		this.tabNotePathRendererEnabled = styles.isTabNotePathRendererEnabled();
		this.stringSpacing = (styles.getStringSpacing() * getScale());
		this.scoreLineSpacing = (styles.getScoreLineSpacing() * getScale());
		this.minBufferSeparator = (styles.getMinBufferSeparator() * getScale());
		this.minTopSpacing = (styles.getMinTopSpacing() * getScale());
		this.minScoreTabSpacing = (styles.getMinScoreTabSpacing() * getScale());
		this.firstTrackSpacing = (styles.getFirstTrackSpacing() * getScale());
		this.firstMeasureSpacing = (styles.getFirstMeasureSpacing() * getScale());
		this.firstNoteSpacing = (styles.getFirstNoteSpacing() * getScale());
		this.measureLeftSpacing = (styles.getMeasureLeftSpacing() * getScale());
		this.measureRightSpacing = (styles.getMeasureRightSpacing() * getScale());
		this.clefSpacing = (styles.getClefSpacing() * getScale());
		this.keySignatureSpacing = (styles.getKeySignatureSpacing() * getScale());
		this.timeSignatureSpacing = (styles.getTimeSignatureSpacing() * getScale());
		this.trackSpacing = (styles.getTrackSpacing() * getScale());
		this.chordFretIndexSpacing = (styles.getChordFretIndexSpacing() * getScale());
		this.chordStringSpacing = (styles.getChordStringSpacing() * getScale());
		this.chordFretSpacing = (styles.getChordFretSpacing() * getScale());
		this.chordNoteSize = (styles.getChordNoteSize() * getScale());
		this.chordLineWidth = (styles.getChordLineWidth() * getScale());
		this.repeatEndingSpacing = (styles.getRepeatEndingSpacing() * getScale());
		this.textSpacing = (styles.getTextSpacing() * getScale());
		this.markerSpacing = (styles.getMarkerSpacing() * getScale());
		this.loopMarkerSpacing = (styles.getLoopMarkerSpacing() * getScale());
		this.divisionTypeSpacing = (styles.getDivisionTypeSpacing() * getScale());
		this.pickStrokeSpacing = (styles.getPickStrokeSpacing() * getScale());
		this.bendSpacing = (styles.getBendSpacing() * getScale());
		this.effectSpacing = (styles.getEffectSpacing() * getScale());

		this.lineWidths = new float[styles.getLineWidths() != null ? styles.getLineWidths().length : 0];
		for(int i = 0 ; i < this.lineWidths.length; i ++) {
			this.lineWidths[i] = (styles.getLineWidths()[i] * getScale());
		}

		this.durationWidths = new float[styles.getDurationWidths() != null ? styles.getDurationWidths().length : 0];
		for(int i = 0 ; i < this.durationWidths.length; i ++) {
			this.durationWidths[i] = (styles.getDurationWidths()[i] * getScale());
		}

		this.getResources().load(styles);
	}

	public abstract void paintSong(UIPainter painter,UIRectangle clientArea,float fromX,float fromY);

	public abstract int getMode();

	public void paint(UIPainter painter,UIRectangle clientArea,float fromX,float fromY){
		this.playModeEnabled = false;
		paintSong(painter,clientArea,fromX,fromY);
	}

	public void paintMeasure(TGMeasureImpl measure,UIPainter painter,float spacing) {
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

	public void updateMeasureNumber(Integer number) {
		this.updateMeasureNumbers(Collections.singletonList(number));
	}

	public void updateMeasureNumbers(List<Integer> numbers) {
		List<TGMeasureHeader> headersToUpdate = new ArrayList<TGMeasureHeader>();
		for(Integer number : numbers) {
			TGMeasureHeader header = getSongManager().getMeasureHeader(getSong(), number);
			if( header != null && !headersToUpdate.contains(header)) {
				headersToUpdate.add(header);
			}
			if( this.updateDependants.containsKey(header)) {
				List<TGMeasureHeader> dependants = this.updateDependants.get(header);
				for(TGMeasureHeader dependant : dependants) {
					if(!headersToUpdate.contains(dependant)) {
						headersToUpdate.add(dependant);
					}
				}
				this.updateDependants.remove(header);
			}
		}

		Collections.sort(headersToUpdate, new Comparator<TGMeasureHeader>() {
			public int compare(TGMeasureHeader o1, TGMeasureHeader o2) {
				return Integer.valueOf(o1.getNumber()).compareTo(Integer.valueOf(o2.getNumber()));
			}
		});

		for(TGMeasureHeader header : headersToUpdate) {
			int index = getSongManager().getMeasureHeaderIndex(getSong(), header);
			if( index >= 0 ) {
				updateMeasureIndex(index);
			}
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

	/**
	 * Pinta las lineas
	 */
	public void paintLines(TGTrackImpl track,TGTrackSpacing ts,UIPainter painter,float x,float y,float width) {
		this.paintLines(track, ts, painter, x, y, width, true);
	}
	public void paintLines(TGTrackImpl track,TGTrackSpacing ts,UIPainter painter,float x,float y,float width, boolean measureIsValid) {
		if(width > 0){
			setLineStyle(painter, measureIsValid);
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
	public void paintPlayMode(UIPainter painter, TGMeasureImpl measure, TGBeatImpl beat){
		this.playModeEnabled = true;

		measure.paintMeasure(this, painter);

		//pinto el pulso
		if( beat != null ){
			beat.paint(this,painter,measure.getPosX()  + measure.getHeaderImpl().getLeftSpacing(this),
					measure.getPosY(), (this.style & HIGHLIGHT_PLAYED_BEAT)!=0);
		}

		//pinto los lyrics
		((TGLyricImpl)measure.getTrackImpl().getLyrics()).paintCurrentNoteBeats(painter,this,measure,measure.getPosX(), measure.getPosY());

		this.playModeEnabled = false;
	}

	public void fillBackground(UIPainter painter, UIRectangle area) {
		fillBackground(painter, area, false);
	}

	public void fillBackground(UIPainter painter, UIRectangle area, boolean isPlaying) {
		UIColor background = isPlaying ? this.getResources().getBackgroundColorPlaying() : this.getResources().getBackgroundColor();
		painter.setBackground(this.getLightColor(background));
		painter.initPath(UIPainter.PATH_FILL);
		painter.addRectangle(area.getX(), area.getY(), area.getWidth(), area.getHeight());
		painter.closePath();
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
		return (((float)TGDuration.QUARTER_TIME / (float)duration.getTime()) * getDurationWidth(duration));
	}

	public float getDurationWidth(TGDuration duration){
		float durationWidth = 0f;

		if( this.durationWidths != null ) {
			int index = 0;
			for (int value = TGDuration.WHOLE; value <= duration.getValue(); value *= 2) {
				if (this.durationWidths.length > index) {
					durationWidth = this.durationWidths[index++];
				}
			}
		}
		return durationWidth;
	}

	public float getMinimumDurationWidth() {
		Float minimumWidth = null;
		if( this.durationWidths != null ) {
			for (int i = 0; i < this.durationWidths.length; i++) {
				if (minimumWidth == null || minimumWidth > this.durationWidths[i]) {
					minimumWidth = this.durationWidths[i];
				}
			}
		}
		return (minimumWidth != null ? minimumWidth : 0f);
	}

	public float getLineWidth(int level) {
		if( this.lineWidths != null ) {
			return this.lineWidths[this.lineWidths.length > level ? level : this.lineWidths.length - 1];
		}
		return UIPainter.THINNEST_LINE_WIDTH;
	}

	public float getScoreNoteWidth() {
		return (this.getScoreLineSpacing() * 1.085f);
	}

	public boolean isPlayModeEnabled(){
		return this.playModeEnabled;
	}

	public void setMeasureNumberStyle(UIPainter painter){
		painter.setFont(getResources().getDefaultFont());
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
		painter.setForeground(getDarkColor(getResources().getMeasureNumberColor()));
	}

	public void setDivisionsStyle(UIPainter painter, boolean fill){
		painter.setFont(getResources().getDefaultFont());
		painter.setBackground( (fill ? getDarkColor(getResources().getForegroundColor()) : getLightColor(getResources().getBackgroundColor())));
		painter.setForeground(getDarkColor(getResources().getForegroundColor()));
	}

	public void setTempoStyle(UIPainter painter, boolean fontStyle){
		painter.setFont(getResources().getDefaultFont());
		painter.setForeground(getDarkColor(getResources().getForegroundColor()));
		painter.setBackground( ( fontStyle ? getLightColor(getResources().getBackgroundColor()) : getDarkColor(getResources().getForegroundColor()) ));
	}

	public void setTripletFeelStyle(UIPainter painter, boolean fontStyle){
		painter.setFont(getResources().getDefaultFont());
		painter.setForeground(getDarkColor(getResources().getForegroundColor()));
		painter.setBackground( ( fontStyle ? getLightColor(getResources().getBackgroundColor()) : getDarkColor(getResources().getForegroundColor()) ));
	}

	public void setLyricStyle(UIPainter painter,boolean playMode){
		painter.setFont(getResources().getLyricFont());
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
		painter.setForeground( (playMode ? getResources().getPlayNoteColor() : getDarkColor(getResources().getForegroundColor())) );
	}

	public void setMarkerStyle(UIPainter painter, UIColor color){
		painter.setFont(getResources().getMarkerFont());
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
		painter.setForeground(getDarkColor(color));
	}

	public void setTextStyle(UIPainter painter){
		painter.setFont(getResources().getTextFont());
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
		painter.setForeground(getDarkColor(getResources().getForegroundColor()));
	}

	public void setTimeSignatureStyle(UIPainter painter){
		painter.setForeground(getDarkColor(getResources().getForegroundColor()));
		painter.setBackground(getDarkColor(getResources().getForegroundColor()));
	}

	public void setKeySignatureStyle(UIPainter painter){
		painter.setBackground(getDarkColor(getResources().getForegroundColor()));
	}

	public void setClefStyle(UIPainter painter){
		painter.setBackground(getDarkColor(getResources().getForegroundColor()));
	}

	public void setLineStyle(UIPainter painter){
		setLineStyle(painter, true);
	}
	public void setLineStyle(UIPainter painter, boolean isValid){
		painter.setLineWidth(getLineWidth(0));
		if (isValid) {
			painter.setForeground(getDarkColor( getResources().getLineColor()));
		} else {
			painter.setForeground(getDarkColor( getResources().getLineColorInvalid()));
		}
	}

	public void setScoreSilenceStyle(UIPainter painter,boolean playMode){
		painter.setForeground( (playMode ? getResources().getPlayNoteColor() : getDarkColor(getResources().getScoreNoteColor())));
		painter.setBackground( (playMode ? getResources().getPlayNoteColor() : getDarkColor(getResources().getScoreNoteColor())));
	}

	public void setTabSilenceStyle(UIPainter painter,boolean playMode){
		painter.setForeground( (playMode ? getResources().getPlayNoteColor() : getDarkColor(getResources().getTabNoteColor()) ));
		painter.setBackground( (playMode ? getResources().getPlayNoteColor() : getDarkColor(getResources().getTabNoteColor()) ));
	}

	public void setScoreNoteStyle(UIPainter painter,boolean playing){
		painter.setForeground( (playing ? getResources().getPlayNoteColor() : getDarkColor(getResources().getScoreNoteColor())));
		painter.setBackground( (playing ? getResources().getPlayNoteColor() : getDarkColor(getResources().getScoreNoteColor())));
	}

	public void setScoreNoteFooterStyle(UIPainter painter){
		painter.setForeground( getDarkColor(getResources().getScoreNoteColor()) );
		painter.setBackground( getDarkColor(getResources().getScoreNoteColor()) );
	}

	public void setScoreEffectStyle(UIPainter painter){
		painter.setForeground( getDarkColor(getResources().getScoreNoteColor()) );
		painter.setBackground( getDarkColor(getResources().getScoreNoteColor()) );
	}

	public void setTabNotePathStyle(UIPainter painter,boolean playMode){
		painter.setForeground((playMode ? getResources().getPlayNoteColor() : getDarkColor(getResources().getTabNoteColor())));
		painter.setBackground((playMode ? getResources().getPlayNoteColor() : getDarkColor(getResources().getTabNoteColor())));
	}

	public void setTabNoteFontStyle(UIPainter painter,boolean playMode){
		painter.setForeground( (playMode ? getResources().getPlayNoteColor() : getDarkColor(getResources().getTabNoteColor())));
		painter.setBackground( getLightColor(getResources().getBackgroundColor()) );
		painter.setFont(getResources().getNoteFont());
	}

	public void setTabNoteFooterStyle(UIPainter painter){
		painter.setForeground( getDarkColor(getResources().getTabNoteColor()));
		painter.setBackground( getDarkColor(getResources().getTabNoteColor()));
	}


	public void setTiedStyle(UIPainter painter, boolean playing){
		painter.setForeground( (playing ? getResources().getPlayNoteColor() : getDarkColor(getResources().getScoreNoteColor())));
		painter.setBackground( (playing ? getResources().getPlayNoteColor() : getDarkColor(getResources().getScoreNoteColor())));
	}

	public void setTabTiedStyle(UIPainter painter, boolean playing){
		painter.setForeground( (playing ? getResources().getPlayNoteColor() : getDarkColor(getResources().getTabNoteColor())));
		painter.setBackground( (playing ? getResources().getPlayNoteColor() : getDarkColor(getResources().getTabNoteColor())));
	}

	public void setTabEffectStyle(UIPainter painter){
		painter.setForeground( getDarkColor(getResources().getTabNoteColor()));
		painter.setBackground( getDarkColor(getResources().getTabNoteColor()));
	}

	public void setTabGraceStyle(UIPainter painter){
		painter.setFont(getResources().getGraceFont());
		painter.setForeground(getDarkColor(getResources().getTabNoteColor()));
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
	}

	public void setPlayNoteColor(UIPainter painter){
		painter.setForeground(getResources().getPlayNoteColor());
		painter.setBackground(getResources().getPlayNoteColor());
	}

	public void setOfflineEffectStyle(UIPainter painter){
		painter.setForeground(getDarkColor(((getStyle() & TGLayout.DISPLAY_SCORE) != 0 ? getResources().getScoreNoteColor() : getResources().getTabNoteColor())));
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
		painter.setFont(getResources().getDefaultFont());
	}

	public void setDotStyle(UIPainter painter){
		painter.setForeground(getDarkColor(((getStyle() & TGLayout.DISPLAY_SCORE) != 0 ? getResources().getScoreNoteColor() : getResources().getTabNoteColor())));
		painter.setBackground(getDarkColor(((getStyle() & TGLayout.DISPLAY_SCORE) != 0 ? getResources().getScoreNoteColor() : getResources().getTabNoteColor())));
	}

	public void setDivisionTypeStyle(UIPainter painter){
		painter.setForeground(getDarkColor(((getStyle() & TGLayout.DISPLAY_SCORE) != 0 ? getResources().getScoreNoteColor() : getResources().getTabNoteColor())));
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
		painter.setFont(getResources().getDefaultFont());
	}

	public void setRepeatEndingStyle(UIPainter painter){
		painter.setForeground(getDarkColor(getResources().getForegroundColor()));
		painter.setBackground(getLightColor(getResources().getBackgroundColor()));
		painter.setFont(getResources().getDefaultFont());
	}

	public void setChordStyle(TGChordImpl chord, boolean isPlaying){
		chord.setFont(getResources().getChordFont());
		chord.setForegroundColor(getDarkColor(getResources().getForegroundColor()));
		if (isPlaying) {
			chord.setBackgroundColor(getResources().getBackgroundColorPlaying());
		} else {
			chord.setBackgroundColor(getLightColor(getResources().getBackgroundColor()));
		}
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

	public void setLoopSMarkerStyle(UIPainter painter){
		painter.setBackground(getResources().getLoopSMarkerColor());
	}

	public void setLoopEMarkerStyle(UIPainter painter){
		painter.setBackground(getResources().getLoopEMarkerColor());
	}

	public UIColor getDarkColor(UIColor color) {
		return ((this.getStyle() & TGLayout.DISPLAY_MODE_BLACK_WHITE) != 0 ? getResources().getColorBlack() : color );
	}

	public UIColor getLightColor(UIColor color) {
		return ((this.getStyle() & TGLayout.DISPLAY_MODE_BLACK_WHITE) != 0 ? getResources().getColorWhite() : color );
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

	public TGController getComponent(){
		return this.controller;
	}

	public TGResources getResources(){
		return this.resources;
	}

	public TGDrumMap getDrumMap() {
		return this.drumMap;
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

	public void setStyle(int style) {
		this.style = style;
	}

	public float getScale() {
		return this.scale;
	}

	public float getFontScale() {
		return this.fontScale;
	}

	public boolean isBufferEnabled() {
		return this.bufferEnabled;
	}

	public boolean isTabNotePathRendererEnabled() {
		return tabNotePathRendererEnabled;
	}

	public float getFirstMeasureSpacing() {
		return this.firstMeasureSpacing;
	}

	public float getFirstNoteSpacing() {
		return firstNoteSpacing;
	}

	public float getMeasureLeftSpacing() {
		return measureLeftSpacing;
	}

	public float getMeasureRightSpacing() {
		return measureRightSpacing;
	}

	public float getClefSpacing() {
		return clefSpacing;
	}

	public float getKeySignatureSpacing() {
		return keySignatureSpacing;
	}

	public float getTimeSignatureSpacing() {
		return timeSignatureSpacing;
	}

	public float getMinBufferSeparator() {
		return this.minBufferSeparator;
	}

	public float getMinTopSpacing() {
		return this.minTopSpacing;
	}

	public float getMinScoreTabSpacing() {
		return this.minScoreTabSpacing;
	}

	public float getScoreLineSpacing() {
		return this.scoreLineSpacing;
	}

	public float getFirstTrackSpacing() {
		return this.firstTrackSpacing;
	}

	public float getTrackSpacing() {
		return this.trackSpacing;
	}

	public float getStringSpacing() {
		return this.stringSpacing;
	}

	public float getChordFretIndexSpacing() {
		return this.chordFretIndexSpacing;
	}

	public float getChordStringSpacing() {
		return this.chordStringSpacing;
	}

	public float getChordFretSpacing() {
		return this.chordFretSpacing;
	}

	public float getChordNoteSize() {
		return this.chordNoteSize;
	}

	public float getChordLineWidth() {
		return chordLineWidth;
	}

	public float getRepeatEndingSpacing() {
		return this.repeatEndingSpacing;
	}

	public float getDivisionTypeSpacing() {
		return this.divisionTypeSpacing;
	}

	public float getPickStrokeSpacing() {
		return this.pickStrokeSpacing;
	}

	public float getBendSpacing() {
		return this.bendSpacing;
	}

	public float getTextSpacing() {
		return this.textSpacing;
	}

	public float getMarkerSpacing() {
		return this.markerSpacing;
	}

	public float getLoopMarkerSpacing() {
		return this.loopMarkerSpacing;
	}

	public float getEffectSpacing() {
		return this.effectSpacing;
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
