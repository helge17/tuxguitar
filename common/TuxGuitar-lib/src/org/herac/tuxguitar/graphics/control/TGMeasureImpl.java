/*
 * Created on 26-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.graphics.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.graphics.control.painters.TGClefPainter;
import org.herac.tuxguitar.graphics.control.painters.TGKeySignaturePainter;
import org.herac.tuxguitar.graphics.control.painters.TGNumberPainter;
import org.herac.tuxguitar.graphics.control.painters.TGTempoPainter;
import org.herac.tuxguitar.graphics.control.painters.TGTripletFeelPainter;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;
import org.herac.tuxguitar.util.TGMusicKeyUtils;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TGMeasureImpl extends TGMeasure{
	
	public static final int SCORE_KEY_SHARP_POSITIONS[][] = new int[][]{ 
		new int[] { 1 , 4, 0, 3, 6, 2 , 5 } ,
		new int[] { 3 , 6, 2, 5, 8, 4 , 7 } ,
		new int[] { 7 , 3, 6, 2, 5, 1 , 4 } ,
		new int[] { 2 , 5, 1, 4, 7, 3 , 6 } ,
	};
	
	public static final int SCORE_KEY_FLAT_POSITIONS[][] = new int[][]{ 
		new int[] { 5, 2, 6, 3, 7, 4, 8 } ,
		new int[] { 7, 4, 8, 5, 9, 6, 10 } ,
		new int[] { 4, 1, 5, 2, 6, 3, 7 } ,
		new int[] { 6, 3, 7, 4, 8, 5, 9 } ,
	};
	
	private static final int SELECTION_ALPHA = 18;
	
	/**
	 * Espacio por defecto de la clave
	 */
	private static final int DEFAULT_CLEF_WIDTH = 16;
	
	/**
	 * Posicion X
	 */
	private float posX;
	/** 
	 * Posicion Y
	 */
	private float posY;
	/**
	 * Espacio entre negras
	 */
	private float quarterSpacing;
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
	private TGMeasureImpl previousMeasure;
	/**
	 * Boolean para saber si el compas esta en el area de pantalla
	 */
	private boolean outOfBounds;
	/**
	 * Boolean para saber si el compas es el primero de la linea
	 */
	private boolean firstOfLine;
	
	private boolean compactMode;
	
	private float spacing;
	
	private TGTrackSpacing ts;
	
	private float maxY;
	
	private float minY;
	
	private int notEmptyVoices;
	
	private int notEmptyBeats;
	
	private float widthBeats = 0;
	
	private List<TGBeatGroup>[] voiceGroups;
	
	private TGMeasureBuffer buffer;
	
	private boolean bufferCreated;
	
	private int lyricBeatIndex;
	private float width;
	
	private float beatEffectVerticalSpacing;
	private float effectWidth;
	private boolean text;
	private boolean chord;
	private boolean division1;
	private boolean division2;
	
	private boolean[][] registeredAccidentals;
	
	private boolean readyToPaint;
	
	@SuppressWarnings("unchecked")
	public TGMeasureImpl(TGMeasureHeader header) {
		super(header);
		this.readyToPaint = false;
		this.registeredAccidentals = new boolean[11][7];
		this.voiceGroups = (List<TGBeatGroup>[]) new List<?>[TGBeat.MAX_VOICES];
		for(int v = 0 ; v < TGBeat.MAX_VOICES; v ++){
			this.voiceGroups[v] = new ArrayList<TGBeatGroup>();
		}
	}
	
	/**
	 * Crea los valores necesarios
	 */
	public void create(TGLayout layout) {
		this.readyToPaint = false;
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
	public void update(TGLayout layout) {
		this.registerBuffer(layout);
		this.updateComponents(layout);
		this.setOutOfBounds(true);
		this.setBufferCreated(false);
		this.readyToPaint = true;
	}
	public boolean getCompactMode() {
		return this.compactMode;
	}
	private void checkCompactMode(TGLayout layout){
		boolean compactMode = ( (layout.getStyle() & TGLayout.DISPLAY_COMPACT) != 0 );
		if(compactMode && (layout.getStyle() & TGLayout.DISPLAY_MULTITRACK) != 0){
			compactMode = (layout.getSong().countTracks() == 1);
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
	
	public void calculateWidth(TGLayout layout) {
		if( this.compactMode ){
			this.width = this.widthBeats;
		}
		else{
			this.width = this.getMaxQuarterSpacing(layout) * this.getTimeSignature().getQuartersInSignature();
			this.width += this.getMaxEffectWidth(layout);
		}
		
		this.width += getFirstNoteSpacing(layout);
		this.width += (this.getRepeatClose() > 0) ? (20f * layout.getScale()) : 0;
		this.width += getHeaderImpl().getLeftSpacing(layout);
		this.width += getHeaderImpl().getRightSpacing(layout);
		
		this.getHeaderImpl().notifyWidth(this.width);
	}
	
	private void calculateBeats(TGLayout layout) {
		TGChord previousChord = null;
		TGDuration minDuration = null;
		TGBeatImpl previousBeat = null;
		TGVoiceImpl[] previousVoices = new TGVoiceImpl[TGBeat.MAX_VOICES];
		TGBeatGroup[] groups = new TGBeatGroup[TGBeat.MAX_VOICES];
		
		int style = layout.getStyle();
		long minimumChordLength = 0;
		
		boolean[] notEmptyVoicesChecked = new boolean[TGBeat.MAX_VOICES];
		boolean chordEnabled = ((style & (TGLayout.DISPLAY_CHORD_DIAGRAM | TGLayout.DISPLAY_CHORD_NAME)) != 0);
		this.widthBeats = 0;
		this.notEmptyBeats = 0;
		this.notEmptyVoices = 0;
		this.effectWidth = 0.0f;
		for(int v = 0 ; v < TGBeat.MAX_VOICES; v ++){
			this.voiceGroups[v].clear();
		}
		
		for (int i = 0; i < countBeats(); i++) {
			TGBeatImpl beat = (TGBeatImpl)getBeat(i);
			beat.reset();
			
			if(chordEnabled && beat.getChord() != null){
				if(previousChord != null){
					long length = (beat.getStart() - previousChord.getBeat().getStart());
					minimumChordLength = (minimumChordLength > 0) ? Math.min(minimumChordLength, Math.abs(length)) : length;
				}
				previousChord = beat.getChord();
			}
			boolean emptyBeat = true;
			float beatEffectWidth = 0.0f;
			for( int v = 0; v < TGBeat.MAX_VOICES; v ++){
				TGVoiceImpl voice = (TGVoiceImpl)beat.getVoice(v);
				if(!voice.isEmpty()){
					emptyBeat = false;
					
					voice.reset();
					if (minDuration == null || voice.getDuration().getTime() <= minDuration.getTime()) {
						minDuration = voice.getDuration();
					}
					if( !notEmptyVoicesChecked[v] ){
						notEmptyVoicesChecked[v] = true;
						this.notEmptyVoices ++;
					}
					
					Iterator<TGNote> it = voice.getNotes().iterator();
					while(it.hasNext()){
						TGNoteImpl note = (TGNoteImpl)it.next();
						voice.check(layout, note);
					}
					float voiceEffectWidth = voice.getEffectWidth(layout);
					if (voiceEffectWidth > beatEffectWidth) beatEffectWidth = voiceEffectWidth;
					
					if(!voice.isRestVoice()){
						beat.check( layout, voice.getMinNote() );
						beat.check( layout, voice.getMaxNote() );
						if( ( groups[v] == null ) || !canJoin(layout.getSongManager(),voice,previousVoices[v]) ){
							groups[v] = new TGBeatGroup(v);
							this.voiceGroups[v].add(groups[v]);
						}
						groups[v].check(layout, voice);
					}else{
						for( int v2 = 0; v2 < TGBeat.MAX_VOICES; v2 ++){
							if(v2 != voice.getIndex()){
								TGVoiceImpl voice2 = beat.getVoiceImpl(v2);
								if( !voice2.isEmpty() && voice2.getDuration().isEqual(voice.getDuration())){
									if(!voice2.isRestVoice() || !voice2.isHiddenSilence()){
										voice.setHiddenSilence(true);
										break;
									}
								}
							}
						}
					}
					makeVoice(layout, voice, previousVoices[v], groups[v], voiceEffectWidth);
					previousVoices[v] = voice;
				}
			}
			if (emptyBeat){
				System.out.println( "Empty Beat !!!!!! " + beat.getStart() + "  " + i);
			}
			this.effectWidth += beatEffectWidth;
			makeBeat(layout,beat,previousBeat,chordEnabled);
			previousBeat = beat;
		}
		this.getHeaderImpl().notifyEffectWidth(this.effectWidth);
		
		for(int v = 0; v < this.voiceGroups.length; v ++){
			Iterator<TGBeatGroup> voiceGroups = this.voiceGroups[v].iterator();
			while (voiceGroups.hasNext()) {
				TGBeatGroup group = (TGBeatGroup)voiceGroups.next();
				group.finish(layout,this);
			}
		}
		
		if(!this.compactMode) {
			this.quarterSpacing = (minDuration != null ? layout.getSpacingForQuarter(minDuration): layout.getMinimumDurationWidth());
			if(chordEnabled && minimumChordLength > 0){
				float chordWidth = (layout.getChordFretIndexSpacing() + layout.getChordStringSpacing() + (((float)getTrack().stringCount()) * layout.getChordStringSpacing()));
				float minimumSpacing = ((((float)TGDuration.QUARTER_TIME) * chordWidth) / ((float)minimumChordLength));
				this.quarterSpacing = Math.max(minimumSpacing,this.quarterSpacing);
			}
			this.getHeaderImpl().notifyQuarterSpacing(this.quarterSpacing);
		}
	}
	
	public boolean canJoin(TGSongManager manager,TGVoiceImpl b1,TGVoiceImpl b2){
		if( b1 == null || b2 == null || b1.isRestVoice() || b2.isRestVoice() ){
			return false;
		}
		
		long divisionLength = getDivisionLength();
		long start = getStart();
		long start1 = (manager.getMeasureManager().getRealStart(this, b1.getBeat().getStart()) - start);
		long start2 = (manager.getMeasureManager().getRealStart(this, b2.getBeat().getStart()) - start);
		
		if(b1.getDuration().getValue() < TGDuration.EIGHTH || b2.getDuration().getValue() < TGDuration.EIGHTH ){
			return ( start1 == start2);
		}
		
		long p1 = ((divisionLength + start1) / divisionLength);
		long p2 = ((divisionLength + start2) / divisionLength);
		
		return  (   p1 == p2  );
	}
	
	private void makeVoice(TGLayout layout,TGVoiceImpl voice,TGVoiceImpl previousVoice,TGBeatGroup group, float voiceEffectWidth){
		voice.setWidth(layout.getDurationWidth(voice.getDuration()) + voiceEffectWidth);
		voice.setBeatGroup( group );
		
		if(previousVoice != null){
			voice.setPreviousBeat(previousVoice);
			previousVoice.setNextBeat(voice);
		}
	}
	
	private void makeBeat(TGLayout layout,TGBeatImpl beat,TGBeatImpl previousBeat, boolean chordEnabled){
		float minimumWidth = -1;
		boolean restBeat = true;
		for(int v = 0 ; v < TGBeat.MAX_VOICES; v ++){
			TGVoiceImpl voice = beat.getVoiceImpl(v);
			if(!voice.isEmpty()){
				if( minimumWidth < 0 || voice.getWidth() < minimumWidth ){
					minimumWidth = voice.getWidth();
				}
				if( !voice.isRestVoice() ){
					restBeat = false;
				}
			}
		}
		
		beat.setWidth( minimumWidth );
		this.notEmptyBeats += (restBeat ? 0 : 1);
		this.widthBeats += beat.getMinimumWidth();
		
		if(previousBeat != null){
			beat.setPreviousBeat(previousBeat);
			previousBeat.setNextBeat(beat);
			
			if(chordEnabled && beat.isChordBeat() && previousBeat.isChordBeat()){
				float previousWidth = previousBeat.getMinimumWidth();
				float chordWidth = (layout.getChordFretIndexSpacing() + layout.getChordStringSpacing() + (getTrack().stringCount() * layout.getChordStringSpacing()));
				previousBeat.setWidth(Math.max(chordWidth,previousWidth));
				this.widthBeats -= previousWidth;
				this.widthBeats += previousBeat.getMinimumWidth();
			}
		}
	}
	
	/**
	 * Calcula si debe pintar el TimeSignature
	 */
	public void calculateMeasureChanges(TGLayout layout) {
		this.paintClef = false;
		this.paintKeySignature = false;
		this.previousMeasure = (layout.isFirstMeasure(this) ? null : (TGMeasureImpl) layout.getSongManager().getTrackManager().getPrevMeasure(this));
		if((layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0 ){
			if(this.previousMeasure == null || getClef() != this.previousMeasure.getClef()){
				this.paintClef = true;
				this.getHeaderImpl().notifyClefSpacing(calculateClefSpacing(layout));
			}
			if(this.previousMeasure == null || getKeySignature() != this.previousMeasure.getKeySignature()){
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
	
	public void registerBuffer(TGLayout layout) {
		TGResourceBuffer buffer = layout.getResourceBuffer();
		
		// Measure Buffer
		this.getBuffer().register(buffer);
		
		// Marker Color
		String markerKey = this.getMarkerRegistryKey();
		if( this.hasMarker() ) {
			buffer.register(markerKey);
		} else if (buffer.isRegistered(markerKey)) {
			buffer.unregister(markerKey);
		}
	}
	
	/**
	 * Llama a update de todas las notas del compas
	 */
	private void updateComponents(TGLayout layout) {
		this.maxY = 0;
		this.minY = 0;
		
		float spacing = getFirstNoteSpacing(layout);
		float tmpX = spacing;
		this.effectWidth = 0.0f;
		for (TGBeat beat : getBeats()) {
			float beatEffectWidth = 0.0f;
			for (int i=0; i< beat.countVoices();i++) {
				float voiceEffectWidth = ((TGVoiceImpl)beat.getVoice(i)).getEffectWidth(layout);
				beatEffectWidth = (voiceEffectWidth > beatEffectWidth ? voiceEffectWidth : beatEffectWidth);
			}
			this.effectWidth += beatEffectWidth;
		}
		
		float noteEffectOffset = 0.0f;
		for (int i = 0; i < countBeats(); i++) {
			TGBeatImpl beat = (TGBeatImpl) getBeat(i);
			beat.registerBuffer(layout);
			beat.resetEffectsSpacing(layout);
			
			if(this.compactMode){
				beat.setPosX(tmpX);
				tmpX += beat.getMinimumWidth();
			}
			else{
				float quarterWidth = getMaxQuarterSpacing(layout) + (this.getMaxEffectWidth(layout)-this.effectWidth)/this.getTimeSignature().getQuartersInSignature();
				float x1 = (spacing + getDisplayPosition(beat.getStart(), quarterWidth));
				float minimumWidth = -1;
				for(int v = 0 ; v < beat.countVoices(); v ++){
					TGVoiceImpl voice = beat.getVoiceImpl(v);
					if(!voice.isEmpty()){
						float x2 = (spacing + getDisplayPosition(beat.getStart() + voice.getDuration().getTime(), quarterWidth));
						float width = ( x2 - x1 );
						if( minimumWidth < 0 || width < minimumWidth ){
							minimumWidth = width;
						}
						voice.setWidth( width );
					}
				}
				beat.setPosX( x1 + noteEffectOffset);
				beat.setWidth( minimumWidth );
			}
			
			float beatEffectWidth = 0.0f;
			for(int v = 0 ; v < beat.countVoices(); v ++){
				TGVoiceImpl voice = beat.getVoiceImpl(v);
				if(!voice.isEmpty()){
					Iterator<TGNote> notes = voice.getNotes().iterator();
					while(notes.hasNext()){
						TGNoteImpl note = (TGNoteImpl)notes.next();
						beat.updateEffectsSpacing(layout, note.getEffect());
						note.update(layout);
					}
					float voiceEffectWidth = voice.getEffectWidth(layout);
					if (!this.compactMode) {
						voice.setWidth(voice.getWidth() + voiceEffectWidth);
						if (voiceEffectWidth > beatEffectWidth) beatEffectWidth = voiceEffectWidth;
					}
					voice.update(layout);
					
					if(!this.division1 && (v % 2 == 0) && !voice.getDuration().getDivision().isEqual(TGDivisionType.NORMAL)){
						this.division1 = true;
					}
					if(!this.division2 && (v % 2 == 1) && !voice.getDuration().getDivision().isEqual(TGDivisionType.NORMAL)){
						this.division2 = true;
					}
					if( (layout.getStyle() & TGLayout.DISPLAY_SCORE) == 0 || (voice.isRestVoice() && !voice.isHiddenSilence()) ){
						if( voice.getMaxY() > this.maxY ){
							this.maxY = voice.getMaxY();
						}
						if( voice.getMinY() < this.minY ){
							this.minY = voice.getMinY();
						}
					}
				}
			}
			if (!this.compactMode) {
				beat.setWidth(beat.getMinimumWidth() + beatEffectWidth);
				noteEffectOffset += beatEffectWidth;
			}
			float bsSize = beat.getEffectsSpacing(layout);
			if( bsSize > this.beatEffectVerticalSpacing ){
				this.beatEffectVerticalSpacing = bsSize;
			}
			
			if(!this.chord && beat.isChordBeat()){
				this.chord = true;
			}
			
			if(!this.text && beat.isTextBeat()){
				this.text = true;
			}
		}
		
		if( (layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0){
			for(int i = 0; i < this.voiceGroups.length; i ++){
				Iterator<TGBeatGroup> groups = this.voiceGroups[i].iterator();
				while (groups.hasNext()) {
					TGBeatGroup group = (TGBeatGroup)groups.next();
					checkValue(layout,group.getMinNote(),group.getDirection());
					checkValue(layout,group.getMaxNote(),group.getDirection());
				}
			}
		}
	}
	
	public float getDisplayPosition(long start, float quarterWidth){
		long newStart = (start - this.getStart());
		float displayPosition = 0f;
		if( newStart > 0 ){
			float position = ((float)newStart / (float)TGDuration.QUARTER_TIME);
			displayPosition = (position * quarterWidth);
		}
		return displayPosition;
	}
	
	// returns note accidental to display, considering key signature and preceding notes in measure
	public int getNoteAccidental(int noteValue){
		if( noteValue >= 0 && noteValue < 128 ){
			int keySignature = getKeySignature();
			int noteIndex = TGMusicKeyUtils.noteIndex(noteValue, keySignature);
			int octave = TGMusicKeyUtils.noteOctave(noteValue, keySignature);
			int accidentalValue = TGMusicKeyUtils.noteAccidental(noteValue, keySignature);
			if (accidentalValue!=TGMusicKeyUtils.NONE && !this.registeredAccidentals[octave][noteIndex]) {
				this.registeredAccidentals[octave][noteIndex] = true;
				return accidentalValue;
			}
			if (accidentalValue==TGMusicKeyUtils.NONE && this.registeredAccidentals[octave][noteIndex]) {
				this.registeredAccidentals[octave][noteIndex] = false;
				return TGMusicKeyUtils.noteAlteration(noteValue, keySignature);
			}
		}
		return TGMusicKeyUtils.NONE;
	}
	
	private void checkValue(TGLayout layout, TGNoteImpl note, int direction){
		float y = note.getScorePosY();
		float upOffset = TGBeatGroup.getUpOffset(layout);
		float downOffset = TGBeatGroup.getDownOffset(layout);
		
		if(direction == TGBeatGroup.DIRECTION_UP && y > this.maxY ){
			this.maxY = y;
		}else if(direction == TGBeatGroup.DIRECTION_DOWN && (y + downOffset) > this.maxY ){
			this.maxY = (y + downOffset + 2f);
		}
		
		if(direction == TGBeatGroup.DIRECTION_UP && (y - upOffset) < this.minY ){
			this.minY = (y - upOffset - 2f);
		}else if(direction == TGBeatGroup.DIRECTION_DOWN && y < this.minY ){
			this.minY = y;
		}
	}
	
	private void resetSpacing(){
		this.text = false;
		this.chord = false;
		this.division1 = false;
		this.division2 = false;
		this.beatEffectVerticalSpacing = 0.0f;
		this.effectWidth = 0.0f;
	}
	
	public void registerSpacing(TGLayout layout,TGTrackSpacing ts){
		if(layout.hasLoopMarker( this.getHeader() )){
			ts.setSize(TGTrackSpacing.POSITION_LOOP_MARKER,layout.getLoopMarkerSpacing());
		}
		if(this.hasMarker()){
			ts.setSize(TGTrackSpacing.POSITION_MARKER,layout.getMarkerSpacing());
		}
		if(this.chord){
			ts.setSize(TGTrackSpacing.POSITION_CHORD,layout.getDefaultChordSpacing());
		}
		if(this.text){
			ts.setSize(TGTrackSpacing.POSITION_TEXT,layout.getTextSpacing());
		}
		if(this.getHeader().getRepeatAlternative() > 0){
			ts.setSize(TGTrackSpacing.POSITION_REPEAT_ENDING,layout.getRepeatEndingSpacing());
		}
		if(this.division1){
			ts.setSize(TGTrackSpacing.POSITION_DIVISION_TYPE_1,layout.getDivisionTypeSpacing());
		}
		if(this.division2){
			ts.setSize(TGTrackSpacing.POSITION_DIVISION_TYPE_2,layout.getDivisionTypeSpacing());
		}
		if( this.beatEffectVerticalSpacing > 0 ){
			ts.setSize(TGTrackSpacing.POSITION_EFFECTS, this.beatEffectVerticalSpacing );
		}
	}
	
	private void orderBeats(TGSongManager manager){
		manager.getMeasureManager().orderBeats(this);
	}
	
	public void paintMeasure(TGLayout layout,UIPainter painter) {
		if( this.readyToPaint ) {
			this.setOutOfBounds(false);
			
			boolean bufferEnabled = layout.isBufferEnabled();
			TGResourceBuffer resourceBuffer = layout.getResourceBuffer();
			
			if(!bufferEnabled || shouldRepaintBuffer(resourceBuffer)){
				UIPainter bufferPainter = painter;
				float x = (bufferEnabled ? 0 : getPosX());
				float y = (bufferEnabled ? 0 : getPosY());
				if( bufferEnabled ){
					UIResourceFactory factory = layout.getComponent().getResourceFactory();
					bufferPainter = getBuffer().createBuffer(resourceBuffer, factory, getWidth(layout) + getSpacing(), getTs().getSize(), layout.getResources().getBackgroundColor());
				}
				layout.paintLines(getTrackImpl(), getTs(), bufferPainter, x, y, getWidth(layout) + getSpacing());
				paintTimeSignature(layout, bufferPainter, x, y);
				paintClef(layout, bufferPainter, x, y);
				paintKeySignature(layout, bufferPainter, x, y);
				paintComponents(layout, bufferPainter, x, y);
				if( bufferEnabled ){
					bufferPainter.dispose(); 
				}
				setBufferCreated(true);
			}
			if( bufferEnabled ){
				painter.setBackground(layout.getResources().getBackgroundColor());
				getBuffer().paintBuffer(resourceBuffer, painter, getPosX(), getPosY());
			}
			
			this.paintMarker(layout, painter);
			this.paintTexts(layout,painter);
			this.paintTempo(layout,painter);
			this.paintTripletFeel(layout,painter);
			this.paintDivisions(layout,painter);
			this.paintRepeatEnding(layout,painter);
			this.paintPlayMode(layout,painter);
			this.paintLoopMarker(layout, painter);
		}
	}
	
	private boolean shouldRepaintBuffer(TGResourceBuffer resourceBuffer){
		return (!isBufferCreated() || getBuffer().isDisposed(resourceBuffer));
	}
	
	public void paintRepeatEnding(TGLayout layout,UIPainter painter){
		if(getHeader().getRepeatAlternative() > 0){
			float scale = layout.getScale();
			float x1 = (getPosX() + getHeaderImpl().getLeftSpacing(layout) + getFirstNoteSpacing(layout));
			float x2 = (getPosX() + getWidth(layout) + getSpacing());
			float y1 = (getPosY() + getTs().getPosition(TGTrackSpacing.POSITION_REPEAT_ENDING));
			float y2 = (y1 + (layout.getRepeatEndingSpacing() * 0.75f ));
			String string = new String();
			for(int i = 0; i < 8; i ++){
				if((getHeader().getRepeatAlternative() & (1 << i)) != 0){
					string += ((string.length() > 0)?(", ") + Integer.toString(i + 1):Integer.toString(i + 1));
				}
			}
			layout.setRepeatEndingStyle(painter);
			painter.initPath();
			painter.setAntialias(false);
			painter.moveTo(x1, y2);
			painter.lineTo(x1, y1);
			painter.moveTo(x1, y1);
			painter.lineTo(x2, y1);
			painter.closePath();
			painter.drawString(string, (x1 + (5.0f * scale)), (y1 + painter.getFMTopLine() + (4f * scale)));
		}
	}
	
	/**
	 * Pinta las notas
	 */
	public void paintComponents(TGLayout layout,UIPainter painter, float fromX, float fromY) {
		float x = (fromX + getHeaderImpl().getLeftSpacing(layout));
		
		Iterator<TGBeat> it = getBeats().iterator();
		while(it.hasNext()) {
			TGBeatImpl beat = (TGBeatImpl)it.next();
			beat.paint(layout, painter, x, fromY);
		}
		
		this.paintDivisionTypes(layout, painter, x, fromY);
	}
	
	public void paintDivisionTypes(TGLayout layout, UIPainter painter, float fromX, float fromY){
		for(int v = 0; v < TGBeat.MAX_VOICES; v ++) {
			if((this.division1 && v % 2 == 0) || (this.division2 && v % 2 == 1)) {
				this.paintDivisionTypes(layout, painter, fromX, fromY, v);
			}
		}
	}
	
	public void paintDivisionTypes(TGLayout layout, UIPainter painter, float fromX, float fromY, int voiceIndex){
		float x1 = 0;
		float x2 = 0;
		
		int tupletDuration = 0;	// sum of all notes durations within current tuplet, unit = fractions of the shortest possible note (to keep all data integers)
		int tupletShortest=0;
		
		TGDivisionType divisionType = null;
		Iterator<TGBeat> it = getBeats().iterator();
		while(it.hasNext()) {
			TGBeatImpl beat = (TGBeatImpl)it.next();
			TGVoiceImpl voice = beat.getVoiceImpl(voiceIndex);
			if( !voice.isEmpty() ) {
				// end of tuplet?
				if( divisionType != null &&
						((!voice.getDuration().getDivision().isEqual(divisionType))
						|| (tupletDuration!=0 && (tupletDuration == divisionType.getEnters()*divisionType.getTimes()*TGDuration.SHORTEST/tupletShortest)))) {
					this.paintDivisionType(layout, painter, divisionType, x1, x2, fromY, voiceIndex);
					divisionType = null;
					tupletDuration = 0;
					tupletShortest = 0;
				}
				// beginning of tuplet?
				if(!voice.getDuration().getDivision().isEqual(TGDivisionType.NORMAL)) {
					x2 = (fromX + beat.getPosX() + beat.getSpacing(layout));
					if( divisionType == null ) {
						divisionType = voice.getDuration().getDivision();
						x1 = x2;
					}
				}
			}
			// update current tuplet if any
			if (divisionType!=null) {
				int voiceDuration = voice.getDuration().getValue();
				tupletDuration += (TGDuration.SHORTEST * divisionType.getTimes() / voiceDuration);
				tupletShortest = voiceDuration > tupletShortest ? voiceDuration : tupletShortest;
			}
		}
		// end of measure, with a tuplet already started?
		if( divisionType != null ) {
			this.paintDivisionType(layout, painter, divisionType, x1, x2, fromY, voiceIndex);
		}
	}
	
	public void paintDivisionType(TGLayout layout, UIPainter painter, TGDivisionType divisionType, float beatX1, float beatX2, float fromY, int voice) {
		layout.setDivisionTypeStyle(painter);
		
		String label = Integer.toString(divisionType.getEnters());
		float scale = layout.getScale();
		float labelWidth = painter.getFMWidth(label);
		float y = (fromY + getTs().getPosition(voice % 2 == 0 ? TGTrackSpacing.POSITION_DIVISION_TYPE_1 : TGTrackSpacing.POSITION_DIVISION_TYPE_2));
		float yMove = ((layout.getDivisionTypeSpacing() / 2f) - scale);
		float xMove = ((((layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0 ? layout.getScoreNoteWidth() : layout.getStringSpacing()) / 2f) + scale);
		float y1 = (y + yMove + (yMove * (voice % 2 == 0 ? 1 : -1)));
		float y2 = (y + yMove);
		float x1 = (beatX1 - xMove);
		float x2 = (beatX2 + (xMove * 2f));
		float xCenter = (x1 + ((x2 - x1) / 2f));
		
		if( beatX2 > beatX1 ) {
			painter.setLineWidth(layout.getLineWidth(1));
			painter.initPath(UIPainter.PATH_DRAW);
			painter.moveTo(x1, y1);
			painter.lineTo(x1, y2);
			painter.lineTo(xCenter - (labelWidth / 2f) - scale, y2);
			
			painter.moveTo(x2, y1);
			painter.lineTo(x2, y2);
			painter.lineTo(xCenter + (labelWidth / 2f) + scale, y2);
			
			painter.closePath();
		}
		
		painter.drawString(label, (xCenter - (labelWidth / 2f)), (y2 + painter.getFMMiddleLine()));
	}
	
	/**
	 * Pinta las divisiones del compas
	 */
	private void paintDivisions(TGLayout layout,UIPainter painter) {
		float x1 = getPosX();
		float x2 = getPosX() + getWidth(layout);
		float y1 = 0;
		float y2 = 0;
		float offsetY = 0;
		int style = layout.getStyle();
		boolean addInfo = false;
		//-----SCORE ------------------------------------//
		if((style & TGLayout.DISPLAY_SCORE) != 0 ){
			y1 = getPosY() + getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES);
			y2 = y1 + (layout.getScoreLineSpacing() * 4);
			addInfo = true;
			if( (style & TGLayout.DISPLAY_TABLATURE) != 0 && (layout.isFirstMeasure(this) || isFirstOfLine())){
				offsetY = ( getPosY() + getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE)) - y2;
			}
			paintDivisions(layout, painter, x1, y1, x2, y2, offsetY, addInfo );
		}
		//-----TABLATURE ------------------------------------//
		if( (style & TGLayout.DISPLAY_TABLATURE) != 0 ){
			y1 = getPosY() + getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE);
			y2 = y1 + (getTrack().stringCount() > 1 ? ((getTrack().stringCount() - 1 ) * layout.getStringSpacing()) : 0f);
			if( y1 == y2 ) {
				y1 -= layout.getStringSpacing();
				y2 += layout.getStringSpacing();
			}
			addInfo = ( (style & TGLayout.DISPLAY_SCORE) == 0 );
			offsetY = 0;
			paintDivisions(layout, painter, x1, y1, x2, y2, offsetY, addInfo );
		}
	}
	
	private void paintDivisions(TGLayout layout,UIPainter painter,float x1, float y1, float x2, float y2, float offsetY, boolean addInfo) {
		float scale = layout.getScale();
		float lineWidthSmall = layout.getLineWidth(1);
		float lineWidthBig = layout.getLineWidth(3);
		
		//numero de compas
		if( addInfo ){ 
			String number = Integer.toString(this.getNumber());
			layout.setMeasureNumberStyle(painter);
			painter.drawString(number, getPosX() + scale, y1 + painter.getFMBaseLine() - (2f * scale));
		}
		
		layout.setDivisionsStyle(painter, true);
		
		//principio
		if( this.isRepeatOpen() || layout.isFirstMeasure(this) ){
			painter.setLineWidth(layout.getLineWidth(0));
			painter.initPath(UIPainter.PATH_DRAW | UIPainter.PATH_FILL);
			painter.setAntialias(false);
			painter.addRectangle(x1, y1, lineWidthBig, (y2 + offsetY) - y1);
			painter.closePath();
			
			painter.setLineWidth(layout.getLineWidth(0));
			painter.initPath(UIPainter.PATH_DRAW | UIPainter.PATH_FILL);
			painter.setAntialias(false);
			painter.addRectangle(x1 + lineWidthBig + (2f * scale), y1, lineWidthSmall, (y2 + offsetY) - y1);
			painter.closePath();
			
			if( this.isRepeatOpen() ){
				float size = layout.getLineWidth(4);
				float xMove = (((lineWidthBig + scale + lineWidthSmall) + (2f * scale)) + size);
				float yMove = size;
				
				painter.setLineWidth(lineWidthSmall);
				painter.initPath(UIPainter.PATH_FILL);
				painter.moveTo(x1 + xMove, y1 + ((y2 - y1) / 2f) - yMove);
				painter.addCircle(x1 + xMove, y1 + ((y2 - y1) / 2f) - yMove, size);
				painter.moveTo(x1 + xMove, y1 + ((y2 - y1) / 2f) + yMove);
				painter.addCircle(x1 + xMove, y1 + ((y2 - y1) / 2f) + yMove, size);
				painter.closePath();
			}
		} else {
			painter.setLineWidth(layout.getLineWidth(0));
			painter.initPath(UIPainter.PATH_DRAW | UIPainter.PATH_FILL);
			painter.setAntialias(false);
			painter.moveTo(x1, y1);
			painter.lineTo(x1, (y2 + offsetY));
			painter.closePath();
		}
		
		//fin
		if( this.getRepeatClose() > 0 || layout.isLastMeasure(this) ){
			painter.setLineWidth(layout.getLineWidth(0));
			painter.initPath(UIPainter.PATH_DRAW | UIPainter.PATH_FILL);
			painter.setAntialias(false);
			painter.addRectangle((x2 + getSpacing()) - (lineWidthBig + lineWidthSmall + (2f * scale)), y1, lineWidthSmall, (y2 - y1));
			painter.closePath();
			
			painter.setLineWidth(layout.getLineWidth(0));
			painter.initPath(UIPainter.PATH_DRAW | UIPainter.PATH_FILL);
			painter.setAntialias(false);
			painter.addRectangle((x2 + getSpacing()) - lineWidthBig, y1, lineWidthBig, (y2 - y1));
			painter.closePath();
			
			if( this.getRepeatClose() > 0 ){
				float size = layout.getLineWidth(4);
				float xMove = (((lineWidthBig + scale + lineWidthSmall) + (2f * scale)) + size);
				float yMove = size;
				
				painter.setLineWidth(lineWidthSmall);
				painter.initPath(UIPainter.PATH_FILL);
				painter.moveTo((x2 - xMove) + getSpacing(), y1 + ((y2 - y1) / 2f) - yMove);
				painter.addCircle((x2 - xMove) + getSpacing(), y1 + ((y2 - y1) / 2f) - yMove, size);
				painter.moveTo((x2 - xMove) + getSpacing(), y1 + ((y2 - y1) / 2f) + yMove);
				painter.addCircle((x2 - xMove) + getSpacing(), y1 + ((y2 - y1) / 2f) + yMove, size);
				painter.closePath();
				if( addInfo ){
					layout.setDivisionsStyle(painter,false);
					
					String repetitions = ("x" + this.getRepeatClose());
					painter.drawString(repetitions, x2 - painter.getFMWidth(repetitions) + getSpacing() - size, y1 + painter.getFMBaseLine() - (2f * scale));
				}
			}
		} else {
			painter.setLineWidth(layout.getLineWidth(0));
			painter.initPath(UIPainter.PATH_DRAW | UIPainter.PATH_FILL);
			painter.setAntialias(false);
			painter.moveTo((x2 + getSpacing()), y1);
			painter.lineTo((x2 + getSpacing()), y2);
			painter.closePath();
		}
		
		painter.setLineWidth(lineWidthSmall);
	}
	
	/**
	 * Pinta la Clave
	 */
	private void paintClef(TGLayout layout, UIPainter painter,float fromX, float fromY) {
		//-----SCORE ------------------------------------//
		if((layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0 && this.paintClef){
			float x = (fromX + getHeaderImpl().getLeftSpacing(layout));
			float y = (fromY + getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES));
			layout.setClefStyle(painter);
			painter.initPath(UIPainter.PATH_FILL);
			
			if(this.getTrack().isPercussion()) {
				TGClefPainter.paintNeutral(painter, x, y, layout.getScoreLineSpacing());
			}
			else if(this.getClef() == TGMeasure.CLEF_TREBLE){
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
	private void paintKeySignature(TGLayout layout,UIPainter painter, float fromX, float fromY) {
		if((layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0 && this.paintKeySignature){
			float scale = layout.getScoreLineSpacing();
			float x = (fromX + getHeaderImpl().getLeftSpacing(layout) + getClefSpacing(layout));
			float y = (fromY + getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES));
			int clefIndex = (this.getClef() - 1);
			int currentKey = this.getKeySignature();
			int previousKey = (this.previousMeasure != null ? this.previousMeasure.getKeySignature() : 0);
			
			layout.setKeySignatureStyle(painter);
			
			//natural
			if(previousKey >= 1 && previousKey <= 7){
				int naturalFrom =  (currentKey >= 1 && currentKey <= 7) ? currentKey : 0;
				for(int i = naturalFrom; i < previousKey; i ++ ){
					float offset =  ( ( (scale / 2) * SCORE_KEY_SHARP_POSITIONS[clefIndex][i] )  - (scale / 2) );
					painter.initPath(UIPainter.PATH_FILL);
					TGKeySignaturePainter.paintNatural(painter,x, (y +  offset  ), scale);
					painter.closePath();
					x += (scale - (scale / 4));
				}
			}
			else if(previousKey >= 8 && previousKey <= 14){
				int naturalFrom =  (currentKey >= 8 && currentKey <= 14) ? currentKey : 7;
				for(int i = naturalFrom; i < previousKey; i ++ ){
					float offset =  ( ( (scale / 2) * SCORE_KEY_FLAT_POSITIONS[clefIndex][i - 7] )  - (scale / 2) );
					painter.initPath(UIPainter.PATH_FILL);
					TGKeySignaturePainter.paintNatural(painter,x, (y +  offset  ), scale);
					painter.closePath();
					x += (scale - (scale / 4));
				}
			}
			
			//sharps
			if(currentKey >= 1 && currentKey <= 7){
				for(int i = 0; i < currentKey; i ++ ){
					float offset =  ( ( (scale / 2) * SCORE_KEY_SHARP_POSITIONS[clefIndex][i] )  - (scale / 2) );
					painter.initPath(UIPainter.PATH_FILL);
					TGKeySignaturePainter.paintSharp(painter,x, (y +  offset  ), scale);
					painter.closePath();
					x += (scale - (scale / 4));
				}
			}
			//flats
			else if(currentKey >= 8 && currentKey <= 14){
				for(int i = 7; i < currentKey; i ++ ){
					float offset =  ( ( (scale / 2) * SCORE_KEY_FLAT_POSITIONS[clefIndex][i - 7] )  - (scale / 2) );
					painter.initPath(UIPainter.PATH_FILL);
					TGKeySignaturePainter.paintFlat(painter,x, (y +  offset  ), scale);
					painter.closePath();
					x += (scale - (scale / 4));
				}
			}
		}
	}
	
	private void paintTimeSignature(TGLayout layout,UIPainter painter, float fromX, float fromY){
		if(this.getHeaderImpl().shouldPaintTimeSignature()){
			int style = layout.getStyle();
			float x = (fromX + getHeaderImpl().getLeftSpacing(layout) + getClefSpacing(layout) + getKeySignatureSpacing(layout));
			float y = 0f;
			float y1 = 0f;
			float y2 = 0f;
			float height = 0;
			if((style & TGLayout.DISPLAY_SCORE) != 0 ) {
				y = getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES);
				y1 = y;
				y2 = (y + getTrackImpl().getScoreHeight());
				height = ((layout.getScoreLineSpacing() * 2f) - layout.getScale());
			} else if((style & TGLayout.DISPLAY_TABLATURE) != 0 ) {
				y = getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE);
				y1 = y;
				y2 = (y + getTrackImpl().getTabHeight());
				height = ((layout.getStringSpacing() * 3f) / 2f);
			}
			
			if((y2 - y1) < (height * 2f)) {
				float yMove = ((((height * 2f) - (y2 - y1)) / 2f));
				
				y1 -= yMove;
				y2 += yMove;
			}
			
			float w1 = TGNumberPainter.getDigitsWidth(getTimeSignature().getNumerator(), height);
			float w2 = TGNumberPainter.getDigitsWidth(getTimeSignature().getDenominator().getValue(), height);
			float wMove1 = (w2 > w1 ? (w2 - w1) / 2f : 0f);
			float wMove2 = (w1 > w2 ? (w1 - w2) / 2f : 0f);
			
			layout.setTimeSignatureStyle(painter);
			TGNumberPainter.paintDigits(getTimeSignature().getNumerator(), painter, x + wMove1, fromY + y1, height);
			TGNumberPainter.paintDigits(getTimeSignature().getDenominator().getValue(), painter, x + wMove2, fromY + y2 - height, height);
		}
	}
	
	private void paintTempo(TGLayout layout,UIPainter painter){
		if(this.getHeaderImpl().shouldPaintTempo()){
			float scale = 5f * layout.getScale(); 
			float x = (getPosX() + getHeaderImpl().getLeftSpacing(layout));
			float y = getPosY();
			float lineSpacing = (Math.max(layout.getScoreLineSpacing() , layout.getStringSpacing()));
			int style = layout.getStyle();
			if( (style & TGLayout.DISPLAY_SCORE) != 0 ){
				y += ( getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES) - lineSpacing );
			}else if( (style & TGLayout.DISPLAY_TABLATURE) != 0 ){
				y += ( getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) - (getTrack().stringCount() > 1 ? lineSpacing : (lineSpacing * 2f)));
			}
			
			layout.setTempoStyle(painter, false);
			float imgX = x;
			float imgY = (y - (Math.round(scale * 3.5f ) + 2));
			
			TGTempoPainter.paintTempo(painter, imgX, imgY, scale);
			
			layout.setTempoStyle(painter, true);
			String value = (" = " + getTempo().getValue());
			float fontX = x + (Math.round( (1.33f * scale) ) + 1 );
			float fontY = (y - (0.75f * scale) + painter.getFMBaseLine());
			painter.drawString(value , fontX, fontY);
		}
	}
	
	private void paintTripletFeel(TGLayout layout,UIPainter painter){
		if(this.getHeaderImpl().shouldPaintTripletFeel()){
			float scale = (5f * layout.getScale());
			float x = (getPosX() + getHeaderImpl().getLeftSpacing(layout) + getHeaderImpl().getTempoSpacing(layout));
			float y = (getPosY());
			float lineSpacing = (Math.max(layout.getScoreLineSpacing() , layout.getStringSpacing()));
			int style = layout.getStyle();
			if( (style & TGLayout.DISPLAY_SCORE) != 0 ){
				y += ( getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES) - lineSpacing );
			}else if( (style & TGLayout.DISPLAY_TABLATURE) != 0 ){
				y += ( getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) - (getTrack().stringCount() > 1 ? lineSpacing : (lineSpacing * 2f)));
			}
			
			layout.setTripletFeelStyle(painter, true);
			String equal = (" = ");
			float fontX = (x + (3.2f * scale));
			float fontY = (y - (0.75f * scale) + painter.getFMBaseLine());
			painter.drawString(equal, fontX , fontY);
			
			layout.setTripletFeelStyle(painter, false);
			float x1 = x;
			float x2 = x + ( (3.2f * scale) + painter.getFMWidth(equal));
			float y1 = y - (( (1.0f * scale) + (2.5f * scale) ) + 2);
			float y2 = y - (( (1.0f * scale) + (2.5f * scale) + (1.0f * scale)) + 2);
			
			if(getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_NONE && this.previousMeasure != null){
				int previous = this.previousMeasure.getTripletFeel();
				if(previous == TGMeasureHeader.TRIPLET_FEEL_EIGHTH){
					TGTripletFeelPainter.paintTripletFeel8(painter, x1, y2, scale );
					TGTripletFeelPainter.paintTripletFeelNone8(painter, x2 , y1, scale );
				}
				else if(previous == TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH){
					TGTripletFeelPainter.paintTripletFeel16(painter, x1, y2, scale );
					TGTripletFeelPainter.paintTripletFeelNone16(painter, x2 , y1, scale );
				}
			}
			else if(getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_EIGHTH){
				TGTripletFeelPainter.paintTripletFeelNone8(painter, x1, y1, scale );
				TGTripletFeelPainter.paintTripletFeel8(painter, x2 , y2, scale );
			}
			else if(getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH){
				TGTripletFeelPainter.paintTripletFeelNone16(painter, x1, y1, scale );
				TGTripletFeelPainter.paintTripletFeel16(painter, x2 , y2, scale );
			}
		}
	}
	
	private void paintLoopMarker(TGLayout layout,UIPainter painter){
		if( layout.hasLoopMarker( this ) ){
			int size = Math.round( layout.getLoopMarkerSpacing() - (1f * layout.getScale()));
			if( layout.getComponent().isLoopSHeader(this.getHeader())){
				float x = (getPosX() + Math.round(size / 2f) );
				float y = (getPosY() + getTs().getPosition(TGTrackSpacing.POSITION_LOOP_MARKER));
				layout.setLoopSMarkerStyle(painter);
				paintLoopMarker(painter, x, y, size);
			}
			if( layout.getComponent().isLoopEHeader(this.getHeader())){
				float x = (getPosX() + getWidth(layout) + getSpacing() - size );
				float y = (getPosY() + getTs().getPosition(TGTrackSpacing.POSITION_LOOP_MARKER));
				layout.setLoopEMarkerStyle(painter);
				paintLoopMarker(painter, x, y, size);
			}
		}
	}
	
	private void paintLoopMarker(UIPainter painter, float x, float y, float size){
		painter.initPath( UIPainter.PATH_FILL );
		painter.addRectangle(x, y, size, size);
		painter.closePath();
	}
	
	private void paintMarker(TGLayout layout, UIPainter painter){
		if( this.hasMarker() ){
			float x = (getPosX() + getHeaderImpl().getLeftSpacing(layout) + getFirstNoteSpacing(layout));
			float y = (getPosY() + getTs().getPosition(TGTrackSpacing.POSITION_MARKER));
			
			layout.setMarkerStyle(painter, getMarkerColor(layout.getResourceBuffer(), layout.getComponent().getResourceFactory()));
			painter.drawString(getMarker().getTitle(), x, y);
		}
	}
	
	private void paintTexts(TGLayout layout,UIPainter painter){
		Iterator<TGBeat> it = getBeats().iterator();
		while(it.hasNext()){
			TGBeat beat = (TGBeat)it.next();
			if( beat.isTextBeat() ){
				TGTextImpl text = (TGTextImpl)beat.getText();
				text.paint(layout, painter,(getPosX() + getHeaderImpl().getLeftSpacing(layout) ),getPosY());
			}
		}
	}
	
	public void paintPlayMode(TGLayout layout,UIPainter painter){
		if(layout.isPlayModeEnabled() && isPlaying(layout)){
			float scale = layout.getScale();
			float width = getWidth(layout) + getSpacing();
			float y1 = getPosY();
			float y2 = getPosY();
			int style = layout.getStyle();
			if( (style & (TGLayout.DISPLAY_SCORE | TGLayout.DISPLAY_TABLATURE)) == (TGLayout.DISPLAY_SCORE | TGLayout.DISPLAY_TABLATURE) ){
				y1 += (getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES) - layout.getScoreLineSpacing());
				y2 += (getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) + getTrackImpl().getTabHeight() + layout.getStringSpacing());
			}else if( (style & TGLayout.DISPLAY_SCORE) != 0 ){
				y1 += (getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES) - layout.getScoreLineSpacing());
				y2 += (getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES) + (layout.getScoreLineSpacing() * 5));
			} else if( (style & TGLayout.DISPLAY_TABLATURE) != 0 ){
				y1 += (getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) - layout.getStringSpacing());
				y2 += (getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) + getTrackImpl().getTabHeight() + layout.getStringSpacing());
			}
			layout.setMeasurePlayingStyle(painter);
			// Don't uncomment "lineStyle" until be sure SWT bug has fixed.
			// See bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=225725
			//painter.setLineStyle(SWT.LINE_DASH);
			painter.setLineWidth(layout.getLineWidth(1));
			painter.initPath();
			painter.setAntialias(false);
			painter.addRectangle(getPosX() + (5f * scale),y1,width - (10f * scale),(y2 - y1));
			painter.closePath();
			//painter.setLineStyle(SWT.LINE_SOLID);
		}
	}
	
	/**
	 * Retorna true si se esta reproduciendo y la posicion del player esta en este compas.
	 */
	public boolean isPlaying(TGLayout layout){
		return layout.getComponent().isRunning(this);
	}
	
	public float getBeatSpacing(TGLayout layout, TGBeatImpl beat){
		float bMargin = getFirstNoteSpacing(layout);
		float mPadding = (bMargin + getHeaderImpl().getLeftSpacing(layout) + getHeaderImpl().getRightSpacing(layout));
		float mWidth = (this.getWidth(layout) - mPadding);
		float mWidthWithSpacing = (mWidth + getSpacing());
		float minimumDurationWidth = layout.getMinimumDurationWidth();
		if( mWidthWithSpacing < minimumDurationWidth ) {
			mWidthWithSpacing = minimumDurationWidth;
		}
		
		float beatX = (beat.getPosX() - bMargin);
		float moveX = (mWidthWithSpacing * beatX / mWidth);
		
		return (moveX - beatX);
	}
	
	public boolean hasTrack(int number){
		return (getTrack().getNumber() == number);
	}
	
	/**
	 * Retorna el ancho del Compas
	 */
	public float getWidth(TGLayout layout) {
		return ((layout.getStyle() & TGLayout.DISPLAY_MULTITRACK) != 0 ?this.getHeaderImpl().getMaxWidth():this.width);
	}
	
	private float calculateClefSpacing(TGLayout layout) {
		return (Math.round(DEFAULT_CLEF_WIDTH * layout.getScale()) + Math.round(layout.getClefSpacing()));
	}
	
	private float calculateKeySignatureSpacing(TGLayout layout){
		float spacing = 0;
		if( this.paintKeySignature) {
			if( this.getKeySignature() <= 7){
				spacing += Math.round( ( 6f * layout.getScale() ) * this.getKeySignature() ) ;
			}else{
				spacing += Math.round( ( 6f * layout.getScale() ) * (this.getKeySignature() - 7) ) ;
			}
			if( this.previousMeasure != null ){
				if(this.previousMeasure.getKeySignature() <= 7){
					spacing += Math.round( ( 6f * layout.getScale() ) * this.previousMeasure.getKeySignature() ) ;
				}else{
					spacing += Math.round( ( 6f * layout.getScale() ) * (this.previousMeasure.getKeySignature() - 7) ) ;
				}
			}
			if( spacing > 0 ) {
				spacing += layout.getKeySignatureSpacing();
			}
		}
		return spacing;
	}
	
	public float getFirstNoteSpacing(TGLayout layout){
		return getHeaderImpl().getFirstNoteSpacing(layout,this);
	}
	
	public float getClefSpacing(TGLayout layout){
		return getHeaderImpl().getClefSpacing(layout, this);
	}
	
	public float getKeySignatureSpacing(TGLayout layout){
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
	public float getPosX() {
		return this.posX;
	}
	
	/**
	 * Asigna la posicion X dentro del compas
	 */
	public void setPosX(float posX) {
		this.posX = posX;
	}
	
	/**
	 * Retorna la posicion Y dentro del compas
	 */
	public float getPosY() {
		return this.posY;
	}
	
	/**
	 * Asigna la posicion Y dentro del compas
	 */
	public void setPosY(float posY) {
		this.posY = posY;
	}
	
	/**
	 * Retorna el spacing de negras
	 */
	private float getMaxQuarterSpacing(TGLayout layout){
		return (((layout.getStyle() & TGLayout.DISPLAY_MULTITRACK) != 0)?getHeaderImpl().getMaxQuarterSpacing():this.quarterSpacing);
	}
	
	private float getMaxEffectWidth(TGLayout layout){
		return (((layout.getStyle() & TGLayout.DISPLAY_MULTITRACK) != 0)?getHeaderImpl().getMaxEffectWidth():this.effectWidth);
	}
	
	public TGMeasureHeaderImpl getHeaderImpl(){
		return (TGMeasureHeaderImpl)super.getHeader();
	}
	
	public float getSpacing() {
		return this.spacing;
	}
	
	public void setSpacing(float spacing) {
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
	
	public TGTrackSpacing getTs() {
		return this.ts;
	}
	
	public void setTs(TGTrackSpacing ts) {
		if(getTs() == null){
			setBufferCreated(false);
		}else if(getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES) != ts.getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES)){
			setBufferCreated(false);
		}else if(getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) != ts.getPosition(TGTrackSpacing.POSITION_TABLATURE)){
			setBufferCreated(false);
		}else if(getTs().getPosition(TGTrackSpacing.POSITION_EFFECTS) != ts.getPosition(TGTrackSpacing.POSITION_EFFECTS)){
			setBufferCreated(false);
		}
		this.ts = ts;
	}
	
	public float getMaxY() {
		return this.maxY;
	}
	
	public float getMinY() {
		return this.minY;
	}
	
	public int getNotEmptyBeats(){
		return this.notEmptyBeats;
	}
	
	public int getNotEmptyVoices(){
		return this.notEmptyVoices;
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
	
	public TGMeasureBuffer getBuffer(){
		if( this.buffer == null ){
			this.buffer = new TGMeasureBuffer();
		}
		return this.buffer;
	}
	
	public TGMeasureImpl getPreviousMeasure() {
		return this.previousMeasure;
	}
	
	public String getMarkerRegistryKey() {
		return (TGMarker.class.getName() + "-" + this.getHeader().getNumber());
	}
	
	public UIColor getMarkerColor(TGResourceBuffer buffer, UIResourceFactory factory){
		String resourceKey = this.getMarkerRegistryKey();
		TGMarker m = getMarker();
		UIColor markerColor = buffer.getResource(resourceKey);
		if( markerColor != null && !markerColor.isDisposed() ){
			if( markerColor.getRed() != m.getColor().getR() || markerColor.getGreen() != m.getColor().getG() || markerColor.getBlue() != m.getColor().getB() ){
				buffer.disposeResource(resourceKey);
			}
		}
		if( markerColor == null || markerColor.isDisposed() ){
			markerColor = factory.createColor(m.getColor().getR(), m.getColor().getG(), m.getColor().getB());
			
			buffer.setResource(resourceKey, markerColor);
		}
		return markerColor;
	}
	public void paintInternalSelection(TGLayout layout, UIPainter painter, TGBeat from, TGBeat to) {
		float x = getBeatLeftEdge(layout, (TGBeatImpl) from);
		float y = getTopEdge(layout);
		float width = getBeatRightEdge(layout, (TGBeatImpl) to) - x;
		float height = getBottomEdge(layout) - y;

		for (int style : new int[] {UIPainter.PATH_FILL, UIPainter.PATH_DRAW}) {
			initSelectionPath(layout, painter, style);
			painter.setAlpha(style == UIPainter.PATH_FILL ? SELECTION_ALPHA : 255);
			painter.addRectangle(x, y, width, height);
			painter.closePath();
		}
	}

	public void paintSelectionStart(TGLayout layout, UIPainter painter, TGBeat from) {
		float x1 = getBeatLeftEdge(layout, (TGBeatImpl) from);
		float x2 = getRightEdge(layout);
		float y1 = getTopEdge(layout);
		float y2 = getBottomEdge(layout);

		initSelectionPath(layout, painter, UIPainter.PATH_FILL);
		painter.setAlpha(SELECTION_ALPHA);
		painter.addRectangle(x1, y1, x2 - x1, y2 - y1);
		painter.closePath();

		initSelectionPath(layout, painter, UIPainter.PATH_DRAW);
		painter.setAlpha(255);
		painter.moveTo(x2, y2);
		painter.lineTo(x1, y2);
		painter.lineTo(x1, y1);
		painter.lineTo(x2, y1);
		painter.closePath();
	}

	private float getBeatLeftEdge(TGLayout layout, TGBeatImpl beat) {
		return getPosX() + beat.getPosX() + beat.getSpacing(layout) + getHeaderImpl().getLeftSpacing(layout) * .5f;
	}

	public void paintSelectionEnd(TGLayout layout, UIPainter painter, TGBeat to) {
		float x1 = getPosX();
		float x2 = getBeatRightEdge(layout, (TGBeatImpl) to);
		float y1 = getTopEdge(layout);
		float y2 = getBottomEdge(layout);

		initSelectionPath(layout, painter, UIPainter.PATH_FILL);
		painter.setAlpha(SELECTION_ALPHA);
		painter.addRectangle(x1, y1, x2 - x1, y2 - y1);
		painter.closePath();

		initSelectionPath(layout, painter, UIPainter.PATH_DRAW);
		painter.setAlpha(255);
		painter.moveTo(x1, y2);
		painter.lineTo(x2, y2);
		painter.lineTo(x2, y1);
		painter.lineTo(x1, y1);
		painter.closePath();
	}

	private float getBeatRightEdge(TGLayout layout, TGBeatImpl beat) {
		return getPosX() + beat.getPosX() + beat.getSpacing(layout) + beat.getMinimumWidth() + getHeaderImpl().getLeftSpacing(layout) * .5f;
	}

	public void paintFullSelection(TGLayout layout, UIPainter painter) {
		float x1 = getPosX();
		float x2 = getRightEdge(layout);
		float y1 = getTopEdge(layout);
		float y2 = getBottomEdge(layout);

		initSelectionPath(layout, painter, UIPainter.PATH_FILL);
		painter.setAlpha(SELECTION_ALPHA);
		painter.addRectangle(x1, y1, x2 - x1, y2 - y1);
		painter.closePath();

		initSelectionPath(layout, painter, UIPainter.PATH_DRAW);
		painter.setAlpha(255);
		painter.moveTo(x1, y1);
		painter.lineTo(x2, y1);
		painter.moveTo(x1, y2);
		painter.lineTo(x2, y2);
		painter.closePath();
	}

	private void initSelectionPath(TGLayout layout, UIPainter painter, int style) {
		float scale = layout.getScale();
		painter.setLineWidth(1f * scale);
		painter.setForeground(layout.getResources().getSelectionColor());
		painter.setBackground(layout.getResources().getSelectionColor());
		painter.initPath(style);
		painter.setAntialias(false);
	}
	
	private float getRightEdge(TGLayout layout) {
		return getPosX() + getWidth(layout) + getSpacing();
	}

	private float getTopEdge(TGLayout layout) {
		int style = layout.getStyle();
		if ((style & (TGLayout.DISPLAY_SCORE | TGLayout.DISPLAY_TABLATURE)) == (TGLayout.DISPLAY_SCORE | TGLayout.DISPLAY_TABLATURE)) {
			return getPosY() + (getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES) - layout.getScoreLineSpacing());
		} else if ((style & TGLayout.DISPLAY_SCORE) != 0) {
			return getPosY() + (getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES) - layout.getScoreLineSpacing());
		} else if ((style & TGLayout.DISPLAY_TABLATURE) != 0) {
			return getPosY() + (getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) - layout.getStringSpacing());
		}
		return getPosY();
	}

	private float getBottomEdge(TGLayout layout) {
		int style = layout.getStyle();
		if ((style & (TGLayout.DISPLAY_SCORE | TGLayout.DISPLAY_TABLATURE)) == (TGLayout.DISPLAY_SCORE | TGLayout.DISPLAY_TABLATURE)) {
			return getPosY() + (getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) + getTrackImpl().getTabHeight() + layout.getStringSpacing());
		} else if ((style & TGLayout.DISPLAY_SCORE) != 0) {
			return getPosY() + (getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES) + (layout.getScoreLineSpacing() * 5));
		} else if ((style & TGLayout.DISPLAY_TABLATURE) != 0) {
			return getPosY() + (getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) + getTrackImpl().getTabHeight() + layout.getStringSpacing());
		}
		return getPosY();
	}
	
	public boolean isLineBreak() {
		return getHeaderImpl().isLineBreak();
	}
}
