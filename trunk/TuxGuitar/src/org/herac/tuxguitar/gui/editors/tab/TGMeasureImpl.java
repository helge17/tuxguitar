/*
 * Created on 26-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.editors.tab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.layout.TrackSpacing;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.gui.editors.tab.painters.TGClefPainter;
import org.herac.tuxguitar.gui.editors.tab.painters.TGKeySignaturePainter;
import org.herac.tuxguitar.gui.editors.tab.painters.TGTempoPainter;
import org.herac.tuxguitar.gui.editors.tab.painters.TGTripletFeelPainter;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNoteEffect;
import org.herac.tuxguitar.song.models.TGTupleto;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TGMeasureImpl extends TGMeasure{
	
	public static final int NATURAL = 1;
	public static final int SHARP = 2;
	public static final int FLAT = 3;
	
	public static final int KEY_SIGNATURES[][] = new int[][]{
		//------------NATURAL------------------------------------
		{NATURAL,NATURAL,NATURAL,NATURAL,NATURAL,NATURAL,NATURAL}, // NATURAL
		//------------SHARPS------------------------------------
		{NATURAL,NATURAL,NATURAL,SHARP,NATURAL,NATURAL,NATURAL},   // 1 SHARP
		{SHARP,NATURAL,NATURAL,SHARP,NATURAL,NATURAL,NATURAL},     // 2 SHARPS
		{SHARP,NATURAL,NATURAL,SHARP,SHARP,NATURAL,NATURAL},       // 3 SHARPS
		{SHARP,SHARP,NATURAL,SHARP,SHARP,NATURAL,NATURAL},         // 4 SHARPS
		{SHARP,SHARP,NATURAL,SHARP,SHARP,SHARP,NATURAL},           // 5 SHARPS
		{SHARP,SHARP,SHARP,SHARP,SHARP,SHARP,NATURAL},             // 6 SHARPS
		{SHARP,SHARP,SHARP,SHARP,SHARP,SHARP,SHARP},               // 7 SHARPS
		//------------FLATS------------------------------------
		{NATURAL,NATURAL,NATURAL,NATURAL,NATURAL,NATURAL,FLAT},    // 1 FLAT
		{NATURAL,NATURAL,FLAT,NATURAL,NATURAL,NATURAL,FLAT},       // 2 FLATS
		{NATURAL,NATURAL,FLAT,NATURAL,NATURAL,FLAT,FLAT},          // 3 FLATS
		{NATURAL,FLAT,FLAT,NATURAL,NATURAL,FLAT,FLAT},             // 4 FLATS
		{NATURAL,FLAT,FLAT,NATURAL,FLAT,FLAT,FLAT},                // 5 FLATS
		{FLAT,FLAT,FLAT,NATURAL,FLAT,FLAT,FLAT},                   // 6 FLATS
		{FLAT,FLAT,FLAT,FLAT,FLAT,FLAT,FLAT},                      // 7 FLATS
	};
	
	public static final int ACCIDENTAL_SHARP_NOTES[] = new int[]{0,0,1,1,2,3,3,4,4,5,5,6};
	public static final int ACCIDENTAL_FLAT_NOTES [] = new int[]{0,1,1,2,2,3,4,4,5,5,6,6};
	public static final boolean ACCIDENTAL_NOTES[] = new boolean[]{false,true,false,true,false,false,true,false,true,false,true,false};
	
	public static final int SCORE_KEY_OFFSETS[] = new int[]{30,18,22,24};
	
	public static final int SCORE_KEY_SHARP_POSITIONS[] = new int[]{ 1 , 4, 0, 3, 6, 2 , 5 };
	
	public static final int SCORE_KEY_FLAT_POSITIONS[] = new int[]{ 5, 2, 6, 3, 0, 4, 1 };
	
	/**
	 * Espacio por defecto de la clave
	 */
	private static final int DEFAULT_CLEF_SPACING = 40;
	/**
	 * Espacio por defecto entre negra y negra
	 */
	private static final int DEFAULT_QUARTER_SPACING = 30;
	/**
	 * Posicion X
	 */
	private int posX;
	/** 
	 * Posicion Y
	 */
	private int posY;
	/**
	 * Espacio entre negras
	 */
	private int quarterSpacing;
	/**
	 * Espacio entre pulsos
	 */
	private long divisionLength;
	/**
	 * Boolean para saber si hay que pintar la clave
	 */
	private boolean paintClef = true;
	/**
	 * Boolean para saber si hay que pintar el KeySignature
	 */
	private boolean paintKeySignature = true;
	/**
	 * Compas anterior
	 */
	private TGMeasure prevMeasure;
	/**
	 * Boolean para saber si el compas esta en el area de pantalla
	 */
	private boolean outOfBounds;
	/**
	 * Boolean para saber si el compas es el primero de la linea
	 */
	private boolean firstOfLine;
	
	private boolean compactMode;
	
	private int spacing;
	
	private TrackSpacing ts;
	
	private int maxY;
	
	private int minY;
	
	private int notEmptyBeats;
	
	private int widthBeats = 0;
	
	private List beatGroups;
	
	private TGMeasureBuffer buffer;
	
	private boolean bufferCreated;
	
	private Color markerColor;
	
	private int lyricBeatIndex;
	private int width;
	
	private boolean text;
	private boolean chord;
	private boolean accentuated;
	private boolean harmonic;
	private boolean tapping;
	private boolean palmMute;
	private boolean vibrato;
	private boolean tupleto;
	private boolean fadeIn;
	
	private boolean[][] registeredAccidentals;
	
	public TGMeasureImpl(TGMeasureHeader header) {
		super(header);
		this.beatGroups = new ArrayList();
		this.registeredAccidentals = new boolean[11][7];
	}
	
	/**
	 * Crea los valores necesarios
	 */
	public void create(ViewLayout layout) {
		this.divisionLength = TGSongManager.getDivisionLength(getHeader());
		this.resetSpacing();
		this.autoCompleteSilences(layout.getSongManager());
		this.orderBeats(layout.getSongManager());
		this.checkCompactMode(layout);
		this.clearRegisteredAccidentals();
		this.calculateBeats(layout);
		this.calculateWidth(layout);
		this.setFirstOfLine(false);
	}
	
	/**
	 * Actualiza los valores para dibujar
	 */
	public void update(ViewLayout layout) {
		updateComponents(layout);
		setOutOfBounds(true);
		setBufferCreated(false);
	}
	
	private void checkCompactMode(ViewLayout layout){
		boolean compactMode = ( (layout.getStyle() & ViewLayout.DISPLAY_COMPACT) != 0 );
		if(compactMode && (layout.getStyle() & ViewLayout.DISPLAY_MULTITRACK) != 0){
			compactMode = (layout.getSongManager().getSong().countTracks() == 1);
		}
		this.compactMode = compactMode;
	}
	
	private void clearRegisteredAccidentals(){
		for( int i = 0 ; i < 11 ; i ++ ){
			for( int n = 0 ; n < 7 ; n ++ ){
				this.registeredAccidentals[i][n] = false;
			}
		}
	}
	
	public void calculateWidth(ViewLayout layout) {
		if(this.compactMode){
			this.width = this.widthBeats;
		}
		else{
			double quartersInSignature = ((1.00 / this.getTimeSignature().getDenominator().getValue()) * 4.00) * this.getTimeSignature().getNumerator();
			this.width = (int)(getQuarterSpacing() * quartersInSignature);
		}
		
		this.width += getFirstNoteSpacing(layout);
		this.width += (this.getRepeatClose() > 0)?20:0;
		this.width += getHeaderImpl().getLeftSpacing(layout);
		this.width += getHeaderImpl().getRightSpacing(layout);
		
		this.getHeaderImpl().notifyWidth(this.width);
	}
	
	private void calculateBeats(ViewLayout layout) {
		TGChord previousChord = null;
		TGDuration minDuration = null;
		TGBeatImpl previousBeat = null;
		TGBeatGroup group = null;
		int style = layout.getStyle();
		int minimunChordLength = 0;
		boolean beatChanged = false;
		boolean chordEnabled = ((style & (ViewLayout.DISPLAY_CHORD_DIAGRAM | ViewLayout.DISPLAY_CHORD_NAME)) != 0);
		this.widthBeats = 0;
		this.notEmptyBeats = 0;
		this.beatGroups.clear();
		
		for (int i = 0; i < countBeats(); i++) {
			TGBeatImpl beat = (TGBeatImpl)getBeat(i);
			beat.reset();
			if (minDuration == null || beat.getDuration().getTime() <= minDuration.getTime()) {
				minDuration = beat.getDuration();
			}
			beatChanged = true;
			Iterator it = beat.getNotes().iterator();
			while(it.hasNext()){
				TGNoteImpl note = (TGNoteImpl)it.next();
				
				beat.check(note);
				
				if( ( group == null ) || (beatChanged && !canJoin(layout.getSongManager(),beat,previousBeat) ) ){
					group = new TGBeatGroup();
					this.beatGroups.add(group);
				}
				group.check(note,getClef());
				note.setBeatGroup(group);
				beatChanged = false;
			}
			
			if(chordEnabled && beat.getChord() != null){
				if(previousChord != null){
					int length = (int) (beat.getStart() - previousChord.getBeat().getStart());
					minimunChordLength = (minimunChordLength > 0)?Math.min(minimunChordLength, Math.abs(length)):length;
				}
				previousChord = beat.getChord();
			}
			
			makeBeat(layout,beat,previousBeat,group,chordEnabled);
			previousBeat = beat;
		}
		
		if(!this.compactMode){
			this.quarterSpacing = (minDuration != null)?layout.getSpacingForQuarter(minDuration): Math.round(DEFAULT_QUARTER_SPACING * layout.getScale());
			if(chordEnabled && minimunChordLength > 0){
				int chordWidth = (layout.getChordFretIndexSpacing() + layout.getChordStringSpacing() + (getTrack().stringCount() * layout.getChordStringSpacing()));
				int minimunSpacing = (int)((TGDuration.QUARTER_TIME * chordWidth) / minimunChordLength);
				this.quarterSpacing = Math.max(minimunSpacing,this.quarterSpacing);
			}
			this.getHeaderImpl().notifyQuarterSpacing(this.quarterSpacing);
		}
	}
	
	public boolean canJoin(TGSongManager manager,TGBeatImpl b1,TGBeatImpl b2){
		if( b1 == null || b2 == null || b1.isRestBeat() || b2.isRestBeat() ){
			return false;
		}
		
		long divisionLength = getDivisionLength();
		long start = getStart();
		long start1 = (manager.getMeasureManager().getRealStart(this, b1.getStart()) - start);
		long start2 = (manager.getMeasureManager().getRealStart(this, b2.getStart()) - start);
		
		if(b1.getDuration().getValue() < TGDuration.EIGHTH || b2.getDuration().getValue() < TGDuration.EIGHTH ){
			return ( start1 == start2);
		}
		
		long p1 = ((divisionLength + start1) / divisionLength);
		long p2 = ((divisionLength + start2) / divisionLength);
		
		return  (   p1 == p2  );
	}
	
	private void makeBeat(ViewLayout layout,TGBeatImpl beat,TGBeatImpl previousBeat,TGBeatGroup group,boolean chordEnabled){
		beat.setWidth((int)layout.getBeatWidth(beat));
		beat.setBeatGroup( group );
		this.notEmptyBeats += (beat.isRestBeat() ? 0 : 1);
		this.widthBeats += beat.getWidth();
		
		if(previousBeat != null){
			beat.setPreviousBeat(previousBeat);
			previousBeat.setNextBeat(beat);
			
			if(chordEnabled && beat.isChordBeat() && previousBeat.isChordBeat()){
				int previousWidth = previousBeat.getWidth();
				int chordWidth = (layout.getChordFretIndexSpacing() + layout.getChordStringSpacing() + (getTrack().stringCount() * layout.getChordStringSpacing()));
				previousBeat.setWidth(Math.max(chordWidth,previousWidth));
				this.widthBeats -= previousWidth;
				this.widthBeats += previousBeat.getWidth();
			}
		}
	}
	
	/**
	 * Calcula si debe pintar el TimeSignature
	 */
	public void calculateMeasureChanges(ViewLayout layout) {
		this.paintClef = false;
		this.paintKeySignature = false;
		this.prevMeasure = (layout.isFirstMeasure(this) ? null : (TGMeasureImpl)layout.getSongManager().getTrackManager().getPrevMeasure(this));
		if((layout.getStyle() & ViewLayout.DISPLAY_SCORE) != 0 ){
			if(this.prevMeasure == null || getClef() != this.prevMeasure.getClef()){
				this.paintClef = true;
				this.getHeaderImpl().notifyClefSpacing( Math.round(DEFAULT_CLEF_SPACING * layout.getScale()) );
			}
			if(this.prevMeasure == null || getKeySignature() != this.prevMeasure.getKeySignature()){
				this.paintKeySignature = true;
				this.getHeaderImpl().notifyKeySignatureSpacing(calculateKeySignatureSpacing(layout));
			}
		}
	}
	
	/**
	 * Calcula si hay espacios libres. y crea nuevos silencios
	 */
	private void autoCompleteSilences(TGSongManager manager){
		manager.getMeasureManager().autoCompleteSilences(this);
	}
	
	/**
	 * Llama a update de todas las notas del compas
	 */
	private void updateComponents(ViewLayout layout) {
		this.maxY = 0;
		this.minY = 0;
		
		int spacing = getFirstNoteSpacing(layout);
		int tmpX = spacing;
		for (int i = 0; i < countBeats(); i++) {
			TGBeatImpl beat = (TGBeatImpl) getBeat(i);
			beat.update(layout);
			if(this.compactMode){
				beat.setPosX(tmpX);
				tmpX += beat.getWidth();
			}
			else{
				int quarterWidth = getMaxQuarterSpacing(layout);
				int x1 = (spacing + TablatureUtil.getStartPosition(this, beat.getStart(), quarterWidth));
				int x2 = (spacing + TablatureUtil.getStartPosition(this, beat.getStart() + beat.getDuration().getTime(), quarterWidth));
				beat.setPosX( x1 );
				beat.setWidth( x2 - x1 );
			}
			
			Iterator notes = beat.getNotes().iterator();
			while(notes.hasNext()){
				TGNoteImpl note = (TGNoteImpl)notes.next();
				
				checkEffects(layout,note.getEffect());
				
				note.update(layout);
			}
			beat.update(layout);
			
			if(!this.chord && beat.isChordBeat()){
				this.chord = true;
			}
			
			if(!this.text && beat.isTextBeat()){
				this.text = true;
			}
			
			if(!this.tupleto && !beat.getDuration().getTupleto().isEqual(TGTupleto.NORMAL)){
				this.tupleto = true;
			}
		}
		Iterator groups = this.beatGroups.iterator();
		while (groups.hasNext()) {
			TGBeatGroup group = (TGBeatGroup)groups.next();
			checkValue(layout,group.getMinNote(),group.getDirection());
			checkValue(layout,group.getMaxNote(),group.getDirection());
		}
	}
	
	public int getNoteAccidental(int noteValue){
		if( noteValue >= 0 && noteValue < 128 ){
			int key = getKeySignature();
			int note = (noteValue % 12);
			int octave = (noteValue / 12);
			int accidentalValue = (key <= 7 ? SHARP : FLAT );
			int [] accidentalNotes = (key <= 7 ? ACCIDENTAL_SHARP_NOTES : ACCIDENTAL_FLAT_NOTES );
			boolean isAccidentalNote = ACCIDENTAL_NOTES[ note ];
			boolean isAccidentalKey = KEY_SIGNATURES[key][accidentalNotes[ note ]] == accidentalValue;
			
			if(isAccidentalKey != isAccidentalNote && !this.registeredAccidentals[ octave ][ accidentalNotes[ note ] ]){
				this.registeredAccidentals[ octave ][ accidentalNotes[note ]  ] = true;
				return (isAccidentalNote ? accidentalValue : NATURAL);
			}
			
			if(isAccidentalKey == isAccidentalNote && this.registeredAccidentals[ octave ][ accidentalNotes[ note ] ]){
				this.registeredAccidentals[ octave ][ accidentalNotes[ note ]  ] = false;
				return (isAccidentalNote ? accidentalValue : NATURAL);
			}
		}
		return 0;
	}
	
	private void checkValue(ViewLayout layout,TGNoteImpl note,int direction){
		int y = note.getScorePosY();
		float upOffset = TGBeatGroup.getUpOffset(layout);
		float downOffset = TGBeatGroup.getDownOffset(layout);
		
		if(direction == TGBeatGroup.DIRECTION_UP && y > this.maxY ){
			this.maxY = y;
		}else if(direction == TGBeatGroup.DIRECTION_DOWN && (y + downOffset) > this.maxY ){
			this.maxY = (int)(y + downOffset + 2);
		}
		
		if(direction == TGBeatGroup.DIRECTION_UP && (y - upOffset) < this.minY ){
			this.minY = (int)(y - upOffset - 2);
		}else if(direction == TGBeatGroup.DIRECTION_DOWN && y < this.minY ){
			this.minY = y;
		}
	}
	
	private void checkEffects(ViewLayout layout,TGNoteEffect effect){
		if(effect.isAccentuatedNote() || effect.isHeavyAccentuatedNote()){
			this.accentuated = true;
		}
		if(effect.isHarmonic() && (layout.getStyle() & ViewLayout.DISPLAY_SCORE) == 0 ){
			this.harmonic = true;
		}
		if(effect.isTapping() || effect.isSlapping() || effect.isPopping()){
			this.tapping = true;
		}
		if(effect.isPalmMute()){
			this.palmMute = true;
		}
		if(effect.isFadeIn()){
			this.fadeIn = true;
		}
		if(effect.isVibrato() || effect.isTrill()){
			this.vibrato = true;
		}
	}
	
	private void resetSpacing(){
		this.text = false;
		this.chord = false;
		this.tupleto = false;
		this.accentuated = false;
		this.harmonic = false;
		this.tapping = false;
		this.palmMute = false;
		this.fadeIn = false;
		this.vibrato = false;
	}
	
	public void registerSpacing(ViewLayout layout,TrackSpacing ts){
		if(this.hasMarker()){
			ts.setSize(TrackSpacing.POSITION_MARKER,layout.getMarkerSpacing());
		}
		if(this.chord){
			ts.setSize(TrackSpacing.POSITION_CHORD,layout.getDefaultChordSpacing());
		}
		if(this.text){
			ts.setSize(TrackSpacing.POSITION_TEXT,layout.getTextSpacing());
		}
		if(this.getHeader().getRepeatAlternative() > 0){
			ts.setSize(TrackSpacing.POSITION_REPEAT_ENDING,layout.getRepeatEndingSpacing());
		}
		if(this.tupleto){
			ts.setSize(TrackSpacing.POSITION_TUPLETO,layout.getTupletoSpacing());
		}
		if(this.accentuated){
			ts.setSize(TrackSpacing.POSITION_ACCENTUATED_EFFECT,layout.getEffectSpacing());
		}
		if(this.harmonic){
			ts.setSize(TrackSpacing.POSITION_HARMONIC_EFFEC,layout.getEffectSpacing());
		}
		if(this.tapping){
			ts.setSize(TrackSpacing.POSITION_TAPPING_EFFEC,layout.getEffectSpacing());
		}
		if(this.palmMute){
			ts.setSize(TrackSpacing.POSITION_PALM_MUTE_EFFEC,layout.getEffectSpacing());
		}
		if(this.fadeIn){
			ts.setSize(TrackSpacing.POSITION_FADE_IN,layout.getEffectSpacing());
		}
		if(this.vibrato){
			ts.setSize(TrackSpacing.POSITION_VIBRATO_EFFEC,layout.getEffectSpacing());
		}
	}
	
	private void orderBeats(TGSongManager manager){
		manager.getMeasureManager().orderBeats(this);
	}
	
	public void paintMeasure(ViewLayout layout,TGPainter painter) {
		this.setOutOfBounds(false);
		
		boolean bufferEnabled = layout.isBufferEnabled();
		
		if(shouldRepaintBuffer() || !bufferEnabled ){
			TGPainter painterBuffer = painter;
			int x = (bufferEnabled ? 0 : getPosX());
			int y = (bufferEnabled ? 0 : getPosY());
			if(bufferEnabled){
				getBuffer().makeBuffer(getWidth(layout) + getSpacing(), getTs().getSize(),layout.getResources().getBackgroundColor());
				painterBuffer = getBuffer().getPainter();
			}
			layout.paintLines(getTrackImpl(),getTs(),painterBuffer,x,y, getWidth(layout) + getSpacing());
			paintTimeSignature(layout,painterBuffer,x,y);
			paintClef(layout,painterBuffer,x,y);
			paintKeySignature(layout,painterBuffer,x,y);
			paintComponents(layout,painterBuffer,x,y);
			
			setBufferCreated(true);
		}
		if(bufferEnabled){
			painter.setBackground(layout.getResources().getBackgroundColor());
			getBuffer().paintImage(painter,getPosX(),getPosY(),getTs().getPosition(TrackSpacing.POSITION_BUFFER_SEPARATOR));
		}
		this.paintMarker(layout, painter);
		this.paintTexts(layout,painter);
		this.paintTempo(layout,painter);
		this.paintTripletFeel(layout,painter);
		this.paintDivisions(layout,painter);
		this.paintRepeatEnding(layout,painter);
		this.paintPlayMode(layout,painter);
	}
	
	private boolean shouldRepaintBuffer(){
		return (isDisposed() || !isBufferCreated());
	}
	
	public void paintRepeatEnding(ViewLayout layout,TGPainter painter){
		if(getHeader().getRepeatAlternative() > 0){
			float scale = layout.getScale();
			float x1 = (getPosX() + getHeaderImpl().getLeftSpacing(layout) + getFirstNoteSpacing(layout));
			float x2 = (getPosX() + getWidth(layout) + getSpacing());
			float y1 = (getPosY() + getTs().getPosition(TrackSpacing.POSITION_REPEAT_ENDING));
			float y2 = (y1 + (layout.getRepeatEndingSpacing() * 0.75f ));
			String string = new String();
			for(int i = 0; i < 8; i ++){
				if((getHeader().getRepeatAlternative() & (1 << i)) != 0){
					string += ((string.length() > 0)?(", ") + Integer.toString(i + 1):Integer.toString(i + 1));
				}
			}
			layout.setRepeatEndingStyle(painter);
			painter.initPath();
			painter.moveTo(x1, y2);
			painter.lineTo(x1, y1);
			painter.lineTo(x2, y1);
			painter.closePath();
			painter.drawString(string,Math.round( x1 + (5.0f * scale) ),Math.round( y1 + (2.0f * scale) ));
		}
	}
	
	/**
	 * Pinta las notas
	 */
	public void paintComponents(ViewLayout layout,TGPainter painter, int fromX, int fromY) {
		Iterator it = getBeats().iterator();
		while(it.hasNext()){
			TGBeatImpl beat = (TGBeatImpl)it.next();
			beat.paint(layout, painter, fromX + getHeaderImpl().getLeftSpacing(layout) ,fromY);
		}
	}
	
	/**
	 * Pinta las divisiones del compas
	 */
	private void paintDivisions(ViewLayout layout,TGPainter painter) {
		int x1 = getPosX();
		int x2 = getPosX() + getWidth(layout);
		int y1 = 0;
		int y2 = 0;
		int offsetY = 0;
		int style = layout.getStyle();
		boolean addInfo = false;
		//-----SCORE ------------------------------------//
		if((style & ViewLayout.DISPLAY_SCORE) != 0 ){
			y1 = getPosY() + getTs().getPosition(TrackSpacing.POSITION_SCORE_MIDDLE_LINES);
			y2 = y1 + (layout.getScoreLineSpacing() * 4);
			addInfo = true;
			if( (style & ViewLayout.DISPLAY_TABLATURE) != 0 && (layout.isFirstMeasure(this) || isFirstOfLine())){
				offsetY = ( getPosY() + getTs().getPosition(TrackSpacing.POSITION_TABLATURE)) - y2;
			}
			paintDivisions(layout, painter, x1, y1, x2, y2, offsetY, addInfo );
		}
		//-----TABLATURE ------------------------------------//
		if( (style & ViewLayout.DISPLAY_TABLATURE) != 0 ){
			y1 = getPosY() + getTs().getPosition(TrackSpacing.POSITION_TABLATURE);
			y2 = y1 + ((getTrack().getStrings().size() - 1 ) * layout.getStringSpacing());
			addInfo = ( (style & ViewLayout.DISPLAY_SCORE) == 0 );
			offsetY = 0;
			paintDivisions(layout, painter, x1, y1, x2, y2, offsetY, addInfo );
		}
	}
	
	private void paintDivisions(ViewLayout layout,TGPainter painter,int x1, int y1, int x2, int y2, int offsetY, boolean addInfo) {
		float scale = layout.getScale();
		int lineWidthSmall = 1;
		int lineWidthBig = Math.max(lineWidthSmall,Math.round(3f * scale));
		
		//numero de compas
		if(addInfo){
			String number = Integer.toString(this.getNumber());
			layout.setMeasureNumberStyle(painter);
			painter.drawString(number,getPosX() + Math.round(scale),(y1 - painter.getStringExtent(number).y) - Math.round(scale));
		}
		
		layout.setDivisionsStyle(painter,true);
		
		//principio
		if(this.isRepeatOpen() || layout.isFirstMeasure(this)){
			painter.initPath(TGPainter.PATH_DRAW | TGPainter.PATH_FILL);
			painter.setLineWidth(lineWidthSmall);
			painter.addRectangle( x1, y1, lineWidthBig, (y2 + offsetY) - y1);
			painter.closePath();
			
			painter.initPath();
			painter.setLineWidth(lineWidthSmall);
			painter.moveTo(x1 + lineWidthBig + scale + lineWidthSmall, y1);
			painter.lineTo(x1 + lineWidthBig + scale + lineWidthSmall, (y2 + offsetY));
			painter.closePath();
			
			if(this.isRepeatOpen()){
				int size = Math.max(1,Math.round(4f * scale));
				float xMove = ((lineWidthBig + scale + lineWidthSmall) + (2f * scale));
				float yMove = ((lineWidthBig + scale + lineWidthSmall) + (2f * scale));
				
				painter.setLineWidth(lineWidthSmall);
				painter.initPath(TGPainter.PATH_FILL);
				painter.moveTo(x1 + xMove, y1 + ((y2 - y1) / 2) - (yMove + (size / 2)));
				painter.addOval(x1 + xMove, y1 + ((y2 - y1) / 2) - (yMove + (size / 2)), size,size);
				painter.moveTo(x1 + xMove, y1 + ((y2 - y1) / 2) + (yMove - (size / 2)));
				painter.addOval(x1 + xMove, y1 + ((y2 - y1) / 2) + (yMove - (size / 2)), size, size);
				painter.closePath();
			}
		}else{
			painter.initPath();
			painter.setLineWidth(lineWidthSmall);
			painter.moveTo(x1, y1);
			painter.lineTo(x1, (y2 + offsetY));
			painter.closePath();
		}
		
		//fin
		if(this.getRepeatClose() > 0 || layout.isLastMeasure(this)){
			painter.initPath();
			painter.setLineWidth(lineWidthSmall);
			painter.moveTo( (x2 + getSpacing()) - (lineWidthBig + scale + lineWidthSmall) , y1);
			painter.lineTo( (x2 + getSpacing()) - (lineWidthBig + scale + lineWidthSmall) , y2);
			painter.closePath();
			
			painter.initPath(TGPainter.PATH_DRAW | TGPainter.PATH_FILL);
			painter.setLineWidth(lineWidthSmall);
			painter.addRectangle( (x2 + getSpacing()) - lineWidthBig, y1, lineWidthBig, y2 - y1);
			painter.closePath();
			
			if(this.getRepeatClose() > 0){
				int size = Math.max(1,Math.round(4f * scale));
				float xMove = (((lineWidthBig + scale + lineWidthSmall) + (2f * scale)) + size);
				float yMove = ( (lineWidthBig + scale + lineWidthSmall) + (2f * scale) );
				
				painter.setLineWidth(lineWidthSmall);
				painter.initPath(TGPainter.PATH_FILL);
				painter.moveTo((x2 - xMove) + getSpacing(), y1 + ((y2 - y1) / 2) - (yMove + (size / 2)));
				painter.addOval((x2 - xMove) + getSpacing(), y1 + ((y2 - y1) / 2) - (yMove + (size / 2)), size,size);
				painter.moveTo((x2 - xMove) + getSpacing(), y1 + ((y2 - y1) / 2) + (yMove - (size / 2)));
				painter.addOval((x2 - xMove) + getSpacing(), y1 + ((y2 - y1) / 2) + (yMove - (size / 2)), size, size);
				painter.closePath();
				if(addInfo){
					layout.setDivisionsStyle(painter,false);
					String repetitions = ("x" + this.getRepeatClose());
					Point numberSize = painter.getStringExtent(repetitions);
					painter.drawString(repetitions,x2 - numberSize.x + getSpacing() - size,(y1 - numberSize.y) - Math.round(scale));
				}
			}
		}else{
			painter.initPath();
			painter.setLineWidth(lineWidthSmall);
			painter.moveTo(x2 + getSpacing(), y1);
			painter.lineTo(x2 + getSpacing(), y2);
			painter.closePath();
		}
		painter.setLineWidth(lineWidthSmall);
	}
	
	/**
	 * Pinta la Clave
	 */
	private void paintClef(ViewLayout layout,TGPainter painter,int fromX, int fromY) {
		//-----SCORE ------------------------------------//
		if((layout.getStyle() & ViewLayout.DISPLAY_SCORE) != 0 && this.paintClef){
			int x = fromX + Math.round( 14 * layout.getScale() ) ;
			int y = fromY + getTs().getPosition(TrackSpacing.POSITION_SCORE_MIDDLE_LINES);
			layout.setClefStyle(painter);
			painter.initPath(TGPainter.PATH_FILL);
			if(this.getClef() == TGMeasure.CLEF_TREBLE){
				TGClefPainter.paintTreble(painter, x, y,layout.getScoreLineSpacing());
			}
			else if(this.getClef() == TGMeasure.CLEF_BASS){
				TGClefPainter.paintBass(painter, x, y,layout.getScoreLineSpacing());
			}
			else if(this.getClef() == TGMeasure.CLEF_TENOR){
				TGClefPainter.paintTenor(painter, x, y,layout.getScoreLineSpacing());
			}
			else if(this.getClef() == TGMeasure.CLEF_ALTO){
				TGClefPainter.paintAlto(painter, x, y,layout.getScoreLineSpacing());
			}
			painter.closePath();
		}
	}
	
	/**
	 * Pinta la Armadura de Clave
	 */
	private void paintKeySignature(ViewLayout layout,TGPainter painter, int fromX, int fromY) {
		if((layout.getStyle() & ViewLayout.DISPLAY_SCORE) != 0 && this.paintKeySignature){
			float scale = layout.getScoreLineSpacing();
			int x = fromX + getClefSpacing(layout) + 10;
			int y = fromY + getTs().getPosition(TrackSpacing.POSITION_SCORE_MIDDLE_LINES);
			int currentKey = this.getKeySignature();
			int previousKey = (this.prevMeasure != null ? this.prevMeasure.getKeySignature() : 0);
			int offsetClef = 0;
			if(this.getClef() == TGMeasure.CLEF_TREBLE){
				offsetClef = 0;
			}
			else if(this.getClef() == TGMeasure.CLEF_BASS){
				offsetClef = 2;
			}
			else if(this.getClef() == TGMeasure.CLEF_TENOR){
				offsetClef = -1;
			}
			else if(this.getClef() == TGMeasure.CLEF_ALTO){
				offsetClef = 1;
			}
			
			layout.setKeySignatureStyle(painter);
			
			//sharps
			if(currentKey >= 1 && currentKey <= 7){
				for(int i = 0; i < currentKey; i ++ ){
					float offset =  ( (scale / 2) * ( ( (SCORE_KEY_SHARP_POSITIONS[i] + offsetClef) + 7) % 7)  )  - (scale / 2);
					painter.initPath(TGPainter.PATH_FILL);
					TGKeySignaturePainter.paintSharp(painter,x, (y +  offset  ), scale);
					painter.closePath();
					x += (scale - (scale / 4));
				}
			}
			//flats
			else if(currentKey >= 8 && currentKey <= 14){
				for(int i = 7; i < currentKey; i ++ ){
					float offset =  ( (scale / 2) * ( ( (SCORE_KEY_FLAT_POSITIONS[i - 7] + offsetClef) + 7) % 7)  )  - (scale / 2);
					painter.initPath(TGPainter.PATH_FILL);
					TGKeySignaturePainter.paintFlat(painter,x, (y +  offset  ), scale);
					painter.closePath();
					x += (scale - (scale / 4));
				}
			}
			//natural
			if(previousKey >= 1 && previousKey <= 7){
				int naturalFrom =  (currentKey >= 1 && currentKey <= 7) ? currentKey : 0;
				for(int i = naturalFrom; i < previousKey; i ++ ){
					float offset =  ( (scale / 2) * ( ( (SCORE_KEY_SHARP_POSITIONS[i] + offsetClef) + 7) % 7)  )  - (scale / 2);
					painter.initPath(TGPainter.PATH_FILL);
					TGKeySignaturePainter.paintNatural(painter,x, (y +  offset  ), scale);
					painter.closePath();
					x += (scale - (scale / 4));
				}
			}
			else if(previousKey >= 8 && previousKey <= 14){
				int naturalFrom =  (currentKey >= 8 && currentKey <= 14) ? currentKey : 7;
				for(int i = naturalFrom; i < previousKey; i ++ ){
					float offset =  ( (scale / 2) * ( ( (SCORE_KEY_FLAT_POSITIONS[i - 7] + offsetClef) + 7) % 7)  )  - (scale / 2);
					painter.initPath(TGPainter.PATH_FILL);
					TGKeySignaturePainter.paintNatural(painter,x, (y +  offset  ), scale);
					painter.closePath();
					x += (scale - (scale / 4));
				}
			}
		}
	}
	
	private void paintTimeSignature(ViewLayout layout,TGPainter painter, int fromX, int fromY){
		if(this.getHeaderImpl().shouldPaintTimeSignature()){
			layout.setTimeSignatureStyle(painter);
			float scale = layout.getScale();
			int style = layout.getStyle();
			int leftSpacing = Math.round( 5.0f * scale );
			int x = (getClefSpacing(layout) + getKeySignatureSpacing(layout) + getHeaderImpl().getLeftSpacing(layout) + leftSpacing);
			String numerator = Integer.toString(getTimeSignature().getNumerator());
			String denominator = Integer.toString(getTimeSignature().getDenominator().getValue());
			if( (style & ViewLayout.DISPLAY_SCORE) != 0 ){
				int y = getTs().getPosition(TrackSpacing.POSITION_SCORE_MIDDLE_LINES);
				int y1 = (int)(y - (3f * scale));
				int y2 = (int)(((y + (layout.getScoreLineSpacing() * 4)) - painter.getStringExtent(denominator).y) + (3f * scale));
				painter.drawString(numerator,fromX + x,fromY + y1,true);
				painter.drawString(denominator,fromX + x,fromY + y2,true);
			}else if( (style & ViewLayout.DISPLAY_TABLATURE) != 0 ){
				int y = getTs().getPosition(TrackSpacing.POSITION_TABLATURE);
				int move = (int)((8f - getTrack().stringCount()) * scale);
				int y1 = (y - move);
				int y2 = ((y  + getTrackImpl().getTabHeight()) - painter.getStringExtent(denominator).y) + move;
				painter.drawString(numerator,fromX + x,fromY + y1,true);
				painter.drawString(denominator,fromX + x,fromY + y2,true);
			}
		}
	}
	
	private void paintTempo(ViewLayout layout,TGPainter painter){
		if(this.getHeaderImpl().shouldPaintTempo()){
			float scale = 5f * layout.getScale(); 
			int x = (getPosX() + getHeaderImpl().getLeftSpacing(layout));
			int y = getPosY();
			int lineSpacing = (Math.max(layout.getScoreLineSpacing() , layout.getStringSpacing()));
			int style = layout.getStyle();
			if( (style & ViewLayout.DISPLAY_SCORE) != 0 ){
				y += ( getTs().getPosition(TrackSpacing.POSITION_SCORE_MIDDLE_LINES) - lineSpacing ) ;
			}else if( (style & ViewLayout.DISPLAY_TABLATURE) != 0 ){
				y += ( getTs().getPosition(TrackSpacing.POSITION_TABLATURE) - lineSpacing ) ;
			}
			
			layout.setTempoStyle(painter, false);
			int imgX = x;
			int imgY = (y - (Math.round(scale * 3.5f ) + 2));
			if( layout.isBufferEnabled() ){
				painter.drawImage(layout.getResources().getTempoImage(),imgX , imgY );
			} else {
				TGTempoPainter.paintTempo(painter, imgX, imgY, scale);
			}
			
			layout.setTempoStyle(painter, true);
			String value = (" = " + getTempo().getValue());
			int fontX = x + (Math.round( (1.33f * scale) ) + 1 );
			int fontY = Math.round(y - painter.getStringExtent( value ).y - (1.0f * layout.getScale()));
			painter.drawString(value , fontX, fontY, true);
		}
	}
	
	private void paintTripletFeel(ViewLayout layout,TGPainter painter){
		if(this.getHeaderImpl().shouldPaintTripletFeel()){
			float scale = (5f * layout.getScale());
			int x = (getPosX() + getHeaderImpl().getLeftSpacing(layout) + getHeaderImpl().getTempoSpacing(layout));
			int y = (getPosY());
			int lineSpacing = (Math.max(layout.getScoreLineSpacing() , layout.getStringSpacing()));
			int style = layout.getStyle();
			if( (style & ViewLayout.DISPLAY_SCORE) != 0 ){
				y += ( getTs().getPosition(TrackSpacing.POSITION_SCORE_MIDDLE_LINES) - lineSpacing );
			}else if( (style & ViewLayout.DISPLAY_TABLATURE) != 0 ){
				y += ( getTs().getPosition(TrackSpacing.POSITION_TABLATURE) - lineSpacing );
			}
			
			layout.setTripletFeelStyle(painter, true);
			String equal = (" = ");
			Point fontSize = painter.getStringExtent( equal );
			int fontX = x + (Math.round( (1.33f * scale) + (1.5f * scale) ));
			int fontY = Math.round(y - fontSize.y - (1.0f * layout.getScale()));
			painter.drawString(equal, fontX , fontY, true);
			
			layout.setTripletFeelStyle(painter, false);
			int x1 = x;
			int x2 = x + (Math.round( (1.33f * scale) + (1.5f * scale) ) + fontSize.x);
			int y1 = y - (Math.round( (1.0f * scale) + (2.5f * scale) ) + 2);
			int y2 = y - (Math.round( (1.0f * scale) + (2.5f * scale) + (1.0f * scale)) + 2);
			
			if(getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_NONE && this.prevMeasure != null){
				int previous = this.prevMeasure.getTripletFeel();
				if(previous == TGMeasureHeader.TRIPLET_FEEL_EIGHTH){
					if( layout.isBufferEnabled() ){
						painter.drawImage(layout.getResources().getTripletFeel8(), x1, y2 );
						painter.drawImage(layout.getResources().getTripletFeelNone8(),x2 , y1 );
					}
					else{
						TGTripletFeelPainter.paintTripletFeel8(painter, x1, y2, scale );
						TGTripletFeelPainter.paintTripletFeelNone8(painter, x2 , y1, scale );
					}
				}
				else if(previous == TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH){
					if( layout.isBufferEnabled() ){
						painter.drawImage(layout.getResources().getTripletFeel16(), x1, y2 );
						painter.drawImage(layout.getResources().getTripletFeelNone16(),x2 , y1 );
					}
					else{
						TGTripletFeelPainter.paintTripletFeel16(painter, x1, y2, scale );
						TGTripletFeelPainter.paintTripletFeelNone16(painter, x2 , y1, scale );
					}
				}
			}
			else if(getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_EIGHTH){
				if( layout.isBufferEnabled() ){
					painter.drawImage(layout.getResources().getTripletFeelNone8(), x1, y1 );
					painter.drawImage(layout.getResources().getTripletFeel8(),x2 , y2 );
				}
				else{
					TGTripletFeelPainter.paintTripletFeelNone8(painter, x1, y1, scale );
					TGTripletFeelPainter.paintTripletFeel8(painter, x2 , y2, scale );
				}
			}
			else if(getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH){
				if( layout.isBufferEnabled() ){
					painter.drawImage(layout.getResources().getTripletFeelNone16(), x1, y1 );
					painter.drawImage(layout.getResources().getTripletFeel16(),x2 , y2 );
				}
				else{
					TGTripletFeelPainter.paintTripletFeelNone16(painter, x1, y1, scale );
					TGTripletFeelPainter.paintTripletFeel16(painter, x2 , y2, scale );
				}
			}
		}
	}
	
	private void paintMarker(ViewLayout layout,TGPainter painter){
		if( this.hasMarker() ){
			int x = (getPosX() + getHeaderImpl().getLeftSpacing(layout) + getFirstNoteSpacing(layout));
			int y = (getPosY() + getTs().getPosition(TrackSpacing.POSITION_MARKER));
			
			layout.setMarkerStyle(painter);
			painter.setForeground( getMarkerColor() );
			painter.drawString(getMarker().getTitle(), x, y);
		}
	}
	
	private void paintTexts(ViewLayout layout,TGPainter painter){
		Iterator it = getBeats().iterator();
		while(it.hasNext()){
			TGBeat beat = (TGBeat)it.next();
			if( beat.isTextBeat() ){
				TGTextImpl text = (TGTextImpl)beat.getText();
				text.paint(layout, painter,(getPosX() + getHeaderImpl().getLeftSpacing(layout) ),getPosY());
			}
		}
	}
	
	private void paintPlayMode(ViewLayout layout,TGPainter painter){
		if(layout.isPlayModeEnabled() && isPlaying(layout)){
			float scale = layout.getScale();
			int width = getWidth(layout) + getSpacing();
			int y1 = getPosY();
			int y2 = getPosY();
			int style = layout.getStyle();
			if( (style & (ViewLayout.DISPLAY_SCORE | ViewLayout.DISPLAY_TABLATURE)) == (ViewLayout.DISPLAY_SCORE | ViewLayout.DISPLAY_TABLATURE) ){
				y1 += (getTs().getPosition(TrackSpacing.POSITION_SCORE_MIDDLE_LINES) - layout.getScoreLineSpacing());
				y2 += (getTs().getPosition(TrackSpacing.POSITION_TABLATURE) + getTrackImpl().getTabHeight() + layout.getStringSpacing());
			}else if( (style & ViewLayout.DISPLAY_SCORE) != 0 ){
				y1 += (getTs().getPosition(TrackSpacing.POSITION_SCORE_MIDDLE_LINES) - layout.getScoreLineSpacing());
				y2 += (getTs().getPosition(TrackSpacing.POSITION_SCORE_MIDDLE_LINES) + (layout.getScoreLineSpacing() * 5));
			} else if( (style & ViewLayout.DISPLAY_TABLATURE) != 0 ){
				y1 += (getTs().getPosition(TrackSpacing.POSITION_TABLATURE) - layout.getStringSpacing());
				y2 += (getTs().getPosition(TrackSpacing.POSITION_TABLATURE) + getTrackImpl().getTabHeight() + layout.getStringSpacing());
			}
			layout.setMeasurePlayingStyle(painter);
			// Don't uncomment "lineStyle" until be sure SWT bug has fixed.
			// See bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=225725
			//painter.setLineStyle(SWT.LINE_DASH);
			painter.setLineWidth(1);
			painter.initPath();
			painter.addRectangle(getPosX() + (5f * scale),y1,width - (10f * scale),(y2 - y1));
			painter.closePath();
			//painter.setLineStyle(SWT.LINE_SOLID);
		}
	}
	
	/**
	 * Retorna true si se esta reproduciendo y la posicion del player esta en este compas.
	 */
	public boolean isPlaying(ViewLayout layout){
		return (getTrackImpl().hasCaret(layout) && TuxGuitar.instance().getEditorCache().isPlaying(this));
	}
	
	public int getBeatSpacing(TGBeat beat){
		return  (int)((beat.getStart() - getStart())  * getSpacing() / getLength());
	}
	
	public boolean hasTrack(int number){
		return (getTrack().getNumber() == number);
	}
	
	/**
	 * Retorna el ancho del Compas
	 */
	public int getWidth(ViewLayout layout) {
		return ((layout.getStyle() & ViewLayout.DISPLAY_MULTITRACK) != 0 ?this.getHeaderImpl().getMaxWidth():this.width);
	}
	
	private int calculateKeySignatureSpacing(ViewLayout layout){
		int spacing = 0;
		if(this.paintKeySignature){
			if(this.getKeySignature() <= 7){
				spacing += Math.round( ( 6f * layout.getScale() ) * this.getKeySignature() ) ;
			}else{
				spacing += Math.round( ( 6f * layout.getScale() ) * (this.getKeySignature() - 7) ) ;
			}
			if(this.prevMeasure != null ){
				if(this.prevMeasure.getKeySignature() <= 7){
					spacing += Math.round( ( 6f * layout.getScale() ) * this.prevMeasure.getKeySignature() ) ;
				}else{
					spacing += Math.round( ( 6f * layout.getScale() ) * (this.prevMeasure.getKeySignature() - 7) ) ;
				}
			}
		}
		return spacing;
	}
	
	public int getFirstNoteSpacing(ViewLayout layout){
		return getHeaderImpl().getFirstNoteSpacing(layout,this);
	}
	
	public int getClefSpacing(ViewLayout layout){
		return getHeaderImpl().getClefSpacing(layout, this);
	}
	
	public int getKeySignatureSpacing(ViewLayout layout){
		return getHeaderImpl().getKeySignatureSpacing(layout, this);
	}
	
	public long getDivisionLength(){
		return this.divisionLength;
	}
	
	public boolean isBufferCreated() {
		return this.bufferCreated;
	}
	
	public void setBufferCreated(boolean bufferCreated) {
		this.bufferCreated = bufferCreated;
	}
	
	/**
	 * Retorna la posicion X dentro del compas
	 */
	public int getPosX() {
		return this.posX;
	}
	
	/**
	 * Asigna la posicion X dentro del compas
	 */
	public void setPosX(int posX) {
		this.posX = posX;
	}
	
	/**
	 * Retorna la posicion Y dentro del compas
	 */
	public int getPosY() {
		return this.posY;
	}
	
	/**
	 * Asigna la posicion Y dentro del compas
	 */
	public void setPosY(int posY) {
		this.posY = posY;
	}
	
	/**
	 * Retorna el spacing de negras
	 */
	private int getQuarterSpacing(){
		return this.quarterSpacing;
	}
	
	/**
	 * Retorna el spacing de negras
	 */
	private int getMaxQuarterSpacing(ViewLayout layout){
		return (((layout.getStyle() & ViewLayout.DISPLAY_MULTITRACK) != 0)?getHeaderImpl().getMaxQuarterSpacing():this.quarterSpacing);
	}
	
	public TGMeasureHeaderImpl getHeaderImpl(){
		return (TGMeasureHeaderImpl)super.getHeader();
	}
	
	public int getSpacing() {
		return this.spacing;
	}
	
	public void setSpacing(int spacing) {
		if(spacing != this.spacing){
			setBufferCreated(false);
		}
		this.spacing = spacing;
	}
	
	public boolean isOutOfBounds() {
		return this.outOfBounds;
	}
	
	public void setOutOfBounds(boolean outOfBounds) {
		this.outOfBounds = outOfBounds;
	}
	
	public boolean isFirstOfLine() {
		return this.firstOfLine;
	}
	
	public void setFirstOfLine(boolean firstOfLine) {
		this.firstOfLine = firstOfLine;
	}
	
	public TGTrackImpl getTrackImpl(){
		return (TGTrackImpl)super.getTrack();
	}
	
	public TrackSpacing getTs() {
		return this.ts;
	}
	
	public void setTs(TrackSpacing ts) {
		if(getTs() == null){
			setBufferCreated(false);
		}else if(getTs().getPosition(TrackSpacing.POSITION_SCORE_MIDDLE_LINES) != ts.getPosition(TrackSpacing.POSITION_SCORE_MIDDLE_LINES)){
			setBufferCreated(false);
		}else if(getTs().getPosition(TrackSpacing.POSITION_TABLATURE) != ts.getPosition(TrackSpacing.POSITION_TABLATURE)){
			setBufferCreated(false);
		}
		this.ts = ts;
	}
	
	public int getMaxY() {
		return this.maxY;
	}
	
	public int getMinY() {
		return this.minY;
	}
	
	public int getNotEmptyBeats(){
		return this.notEmptyBeats;
	}
	
	public int getLyricBeatIndex() {
		return this.lyricBeatIndex;
	}
	
	public void setLyricBeatIndex(int lyricBeatIndex) {
		this.lyricBeatIndex = lyricBeatIndex;
	}
	
	public boolean isPaintClef() {
		return this.paintClef;
	}
	
	public boolean isPaintKeySignature() {
		return this.paintKeySignature;
	}
	
	public boolean isDisposed(){
		return getBuffer().isDisposed();
	}
	
	public TGMeasureBuffer getBuffer(){
		if(this.buffer == null){
			this.buffer = new TGMeasureBuffer(TuxGuitar.instance().getDisplay());
		}
		return this.buffer;
	}
	
	public Color getMarkerColor(){
		TGColor color = getMarker().getColor();
		if(this.markerColor != null && !this.markerColor.isDisposed()){
			RGB rgb = this.markerColor.getRGB();
			if( rgb.red != color.getR() || rgb.green != color.getG() ||  rgb.blue != color.getB()){
				this.disposeMarkerColor();
			}
		}
		if(this.markerColor == null || this.markerColor.isDisposed()){
			this.markerColor = new Color(TuxGuitar.instance().getDisplay(), color.getR(),color.getG(),color.getB());
			System.out.println("Marker Color Created");
		}
		return this.markerColor;
	}

	public void disposeMarkerColor(){
		if(this.markerColor != null && !this.markerColor.isDisposed()){
			this.markerColor.dispose();
			this.markerColor = null;
		}
	}
	
	public void dispose(){
		new SyncThread( new Runnable() {
			public void run() {
				if(!TuxGuitar.isDisposed()){
					getBuffer().dispose();
					disposeMarkerColor();
					Iterator it = getBeats().iterator();
					while(it.hasNext()){
						TGBeatImpl beat = (TGBeatImpl)it.next();
						beat.dispose();
					}
				}
			}
		} ).start();
	}
}