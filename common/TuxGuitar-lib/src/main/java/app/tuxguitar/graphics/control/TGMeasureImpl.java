/*
 * Created on 26-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package app.tuxguitar.graphics.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.graphics.control.painters.TGClefPainter;
import app.tuxguitar.graphics.control.painters.TGKeySignaturePainter;
import app.tuxguitar.graphics.control.painters.TGNumberPainter;
import app.tuxguitar.graphics.control.painters.TGTempoPainter;
import app.tuxguitar.graphics.control.painters.TGTripletFeelPainter;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGColor;
import app.tuxguitar.song.models.TGDivisionType;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UIResourceFactory;
import app.tuxguitar.util.TGMusicKeyUtils;

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
	
	private boolean firstOfLine;
	private boolean lastOfLine;

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
	private boolean pickStroke;
	private boolean bend;

	private boolean[][] registeredAccidentals;

	private boolean readyToPaint;

	private boolean wasPlayingWhenLastPainted;
	private boolean wasLastWhenLastPainted;

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
		this.setLastOfLine(false);
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
		else {
			// nb of quarters in measure may vary (e.g. if measure is invalid)
			TGBeat lastBeat = getBeats().get(countBeats()-1);
			long lastBeatPreciseEnd = lastBeat.getPreciseStart() + layout.getSongManager().getMeasureManager().getMaximumDuration(lastBeat).getPreciseTime();
			long measurePreciseDuration = lastBeatPreciseEnd - this.getPreciseStart();
			int nbQuarters = (int) Math.ceil(((float)measurePreciseDuration) / (float)TGDuration.toPreciseTime(TGDuration.QUARTER_TIME));
			this.width = this.getMaxQuarterSpacing(layout) * nbQuarters;
			this.width += this.getMaxEffectWidth(layout);
		}

		this.width += getFirstNoteSpacing(layout);
		this.width += (this.getRepeatClose() > 0) ? (20f * layout.getScale()) : 0;
		this.width += getHeaderImpl().getLeftSpacing(layout);
		this.width += getHeaderImpl().getRightSpacing(layout);

		this.getHeaderImpl().notifyWidth(this.width);
	}

	private void calculateBeats(TGLayout layout) {
		boolean chordEnabled = ((layout.getStyle() & (TGLayout.DISPLAY_CHORD_DIAGRAM | TGLayout.DISPLAY_CHORD_NAME)) != 0);
		BeatsCalculation calculation = new BeatsCalculation();
		TGBeatImpl previousBeat = null;
		this.resetBeatsCalculation();

		for (int i = 0; i < countBeats(); i++) {
			TGBeatImpl beat = (TGBeatImpl)getBeat(i);
			beat.reset();
			if(chordEnabled){
				calculation.checkChord(beat);
			}
			float beatEffectWidth = calculateVoices(layout, beat, calculation);
			beat.setEffectWidth(beatEffectWidth);
			if (!hasVoices(beat)){
				System.out.println( "Empty Beat !!!!!! " + beat.getStart() + "  " + i);
			}
			this.effectWidth += beatEffectWidth;
			makeBeat(layout,beat,previousBeat,chordEnabled);
			previousBeat = beat;
		}
		this.getHeaderImpl().notifyEffectWidth(this.effectWidth);

		this.finishVoiceGroups(layout);

		if(!this.compactMode) {
			this.updateQuarterSpacing(layout, calculation, chordEnabled);
		}
	}

	private void resetBeatsCalculation() {
		this.widthBeats = 0;
		this.notEmptyBeats = 0;
		this.notEmptyVoices = 0;
		this.effectWidth = 0.0f;
		for(int v = 0 ; v < TGBeat.MAX_VOICES; v ++){
			this.voiceGroups[v].clear();
		}
	}

	private boolean hasVoices(TGBeat beat) {
		for( int v = 0; v < TGBeat.MAX_VOICES; v ++){
			if(!beat.getVoice(v).isEmpty()){
				return true;
			}
		}
		return false;
	}

	// returns the beat effect width: the widest effect among its non-empty voices
	private float calculateVoices(TGLayout layout, TGBeatImpl beat, BeatsCalculation calculation) {
		float beatEffectWidth = 0.0f;
		for( int v = 0; v < TGBeat.MAX_VOICES; v ++){
			TGVoiceImpl voice = (TGVoiceImpl)beat.getVoice(v);
			if(!voice.isEmpty()){
				float voiceEffectWidth = calculateVoice(layout, beat, voice, v, calculation);
				if (voiceEffectWidth > beatEffectWidth) beatEffectWidth = voiceEffectWidth;
			}
		}
		return beatEffectWidth;
	}

	// returns the voice effect width
	private float calculateVoice(TGLayout layout, TGBeatImpl beat, TGVoiceImpl voice, int v, BeatsCalculation calculation) {
		voice.reset();
		calculation.checkDuration(voice);
		if( calculation.markNotEmpty(v) ){
			this.notEmptyVoices ++;
		}
		for (TGNote note : voice.getNotes()) {
			voice.check(layout, (TGNoteImpl)note);
		}
		float voiceEffectWidth = voice.getEffectWidth(layout);

		if(!voice.isRestVoice()){
			groupVoice(layout, beat, voice, v, calculation);
		}else{
			checkHiddenSilence(beat, voice);
		}
		makeVoice(layout, voice, calculation.previousVoices[v], calculation.groups[v], voiceEffectWidth);
		calculation.previousVoices[v] = voice;
		return voiceEffectWidth;
	}

	// joins the voice to the beam group of the previous one when possible, else starts a new group
	private void groupVoice(TGLayout layout, TGBeatImpl beat, TGVoiceImpl voice, int v, BeatsCalculation calculation) {
		beat.check( layout, voice.getMinNote() );
		beat.check( layout, voice.getMaxNote() );
		if( ( calculation.groups[v] == null ) || !canJoin(layout.getSongManager(),voice,calculation.previousVoices[v]) ){
			calculation.groups[v] = new TGBeatGroup(v);
			this.voiceGroups[v].add(calculation.groups[v]);
		}
		calculation.groups[v].check(layout, voice);
	}

	// a rest is hidden by another voice of the same duration, unless that voice is itself a hidden rest
	private void checkHiddenSilence(TGBeatImpl beat, TGVoiceImpl rest) {
		for( int v = 0; v < TGBeat.MAX_VOICES; v ++){
			if( v != rest.getIndex() && hidesSilence(beat.getVoiceImpl(v), rest) ){
				rest.setHiddenSilence(true);
				return;
			}
		}
	}

	private boolean hidesSilence(TGVoiceImpl voice, TGVoiceImpl rest) {
		return ( !voice.isEmpty() && voice.getDuration().isEqual(rest.getDuration()) && (!voice.isRestVoice() || !voice.isHiddenSilence()) );
	}

	private void finishVoiceGroups(TGLayout layout) {
		for(int v = 0; v < this.voiceGroups.length; v ++){
			for (TGBeatGroup group : this.voiceGroups[v]) {
				group.finish(layout,this);
			}
		}
	}

	// the shortest duration sets the quarter spacing, widened so that close chord diagrams do not overlap
	private void updateQuarterSpacing(TGLayout layout, BeatsCalculation calculation, boolean chordEnabled) {
		this.quarterSpacing = (calculation.minDuration != null ? layout.getSpacingForQuarter(calculation.minDuration): layout.getMinimumDurationWidth());
		if(chordEnabled && calculation.minimumChordLength > 0){
			float minimumSpacing = ((((float)TGDuration.QUARTER_TIME) * getChordWidth(layout)) / ((float)calculation.minimumChordLength));
			this.quarterSpacing = Math.max(minimumSpacing,this.quarterSpacing);
		}
		this.getHeaderImpl().notifyQuarterSpacing(this.quarterSpacing);
	}

	private float getChordWidth(TGLayout layout) {
		return (layout.getChordFretIndexSpacing() + layout.getChordStringSpacing() + (getTrack().stringCount() * layout.getChordStringSpacing()));
	}

	/** What calculateBeats carries from one beat to the next. */
	private static class BeatsCalculation {

		private TGChord previousChord;
		private long minimumChordLength;
		private TGDuration minDuration;
		private final TGVoiceImpl[] previousVoices = new TGVoiceImpl[TGBeat.MAX_VOICES];
		private final TGBeatGroup[] groups = new TGBeatGroup[TGBeat.MAX_VOICES];
		private final boolean[] notEmptyVoicesChecked = new boolean[TGBeat.MAX_VOICES];

		// keeps the shortest distance between two successive chords
		void checkChord(TGBeat beat) {
			if( beat.getChord() == null ){
				return;
			}
			if(this.previousChord != null){
				long length = (beat.getStart() - this.previousChord.getBeat().getStart());
				this.minimumChordLength = (this.minimumChordLength > 0) ? Math.min(this.minimumChordLength, Math.abs(length)) : length;
			}
			this.previousChord = beat.getChord();
		}

		void checkDuration(TGVoiceImpl voice) {
			if (this.minDuration == null || voice.getDuration().getTime() <= this.minDuration.getTime()) {
				this.minDuration = voice.getDuration();
			}
		}

		// true the first time a voice index is found not empty
		boolean markNotEmpty(int v) {
			if( this.notEmptyVoicesChecked[v] ){
				return false;
			}
			this.notEmptyVoicesChecked[v] = true;
			return true;
		}
	}

	public boolean canJoin(TGSongManager manager,TGVoiceImpl b1,TGVoiceImpl b2){
		if( b1 == null || b2 == null || b1.isRestVoice() || b2.isRestVoice() ){
			return false;
		}

		long divisionLength = getDivisionLength();
		long preciseStart = getPreciseStart();
		long start1 = TGDuration.toTime(b1.getBeat().getPreciseStart() - preciseStart);
		long start2 = TGDuration.toTime(b2.getBeat().getPreciseStart() - preciseStart);

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
		beat.setWidth( getNarrowestVoiceWidth(beat) );
		this.notEmptyBeats += (beat.isRestBeat() ? 0 : 1);
		this.widthBeats += beat.getMinimumWidth();

		if(previousBeat != null){
			beat.setPreviousBeat(previousBeat);
			previousBeat.setNextBeat(beat);

			if(chordEnabled && beat.isChordBeat() && previousBeat.isChordBeat()){
				widenForChord(layout, previousBeat);
			}
		}
	}

	// the width of the narrowest voice with something in it; -1 when every voice is empty
	private static float getNarrowestVoiceWidth(TGBeatImpl beat){
		float minimumWidth = -1;
		for(int v = 0 ; v < TGBeat.MAX_VOICES; v ++){
			TGVoiceImpl voice = beat.getVoiceImpl(v);
			if(!voice.isEmpty() && ( minimumWidth < 0 || voice.getWidth() < minimumWidth )){
				minimumWidth = voice.getWidth();
			}
		}
		return minimumWidth;
	}

	// a chord beat followed by another one is widened to make room for its chord
	private void widenForChord(TGLayout layout, TGBeatImpl beat){
		float previousWidth = beat.getMinimumWidth();
		beat.setWidth(Math.max(getChordWidth(layout),previousWidth));
		this.widthBeats -= previousWidth;
		this.widthBeats += beat.getMinimumWidth();
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
		updateEffectWidth(layout);

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
				updateBeatPosition(layout, beat, spacing, noteEffectOffset);
			}

			float beatEffectWidth = updateVoices(layout, beat);
			if (!this.compactMode) {
				beat.setWidth(beat.getMinimumWidth() + beatEffectWidth);
				noteEffectOffset += beatEffectWidth;
			}
			updateBeatEffectVerticalSpacing(layout, beat);
			updateBeatFlags(beat);
		}

		if( isScoreEnabled(layout) ){
			checkVoiceGroupValues(layout);
		}
	}

	private boolean isScoreEnabled(TGLayout layout) {
		return ( (layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0 );
	}

	private void updateEffectWidth(TGLayout layout) {
		this.effectWidth = 0.0f;
		for (TGBeat beat : getBeats()) {
			this.effectWidth += getMaxVoiceEffectWidth(layout, beat);
		}
	}

	private float getMaxVoiceEffectWidth(TGLayout layout, TGBeat beat) {
		float beatEffectWidth = 0.0f;
		for (int i=0; i< beat.countVoices();i++) {
			float voiceEffectWidth = ((TGVoiceImpl)beat.getVoice(i)).getEffectWidth(layout);
			beatEffectWidth = (voiceEffectWidth > beatEffectWidth ? voiceEffectWidth : beatEffectWidth);
		}
		return beatEffectWidth;
	}

	// non-compact mode: position the beat proportionally to its start, and size each voice by its duration
	private void updateBeatPosition(TGLayout layout, TGBeatImpl beat, float spacing, float noteEffectOffset) {
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

	// returns the beat effect width: the widest effect among its non-empty voices (0 in compact mode)
	private float updateVoices(TGLayout layout, TGBeatImpl beat) {
		float beatEffectWidth = 0.0f;
		for(int v = 0 ; v < beat.countVoices(); v ++){
			TGVoiceImpl voice = beat.getVoiceImpl(v);
			if(!voice.isEmpty()){
				beatEffectWidth = updateVoice(layout, beat, voice, v, beatEffectWidth);
			}
		}
		return beatEffectWidth;
	}

	private float updateVoice(TGLayout layout, TGBeatImpl beat, TGVoiceImpl voice, int v, float beatEffectWidth) {
		float maxEffectWidth = beatEffectWidth;
		updateNotes(layout, beat, voice);
		float voiceEffectWidth = voice.getEffectWidth(layout);
		if (!this.compactMode) {
			voice.setWidth(voice.getWidth() + voiceEffectWidth);
			if (voiceEffectWidth > maxEffectWidth) maxEffectWidth = voiceEffectWidth;
		}
		voice.update(layout);

		updateDivisionFlags(voice, v);
		updateVerticalBounds(layout, voice);
		return maxEffectWidth;
	}

	private void updateNotes(TGLayout layout, TGBeatImpl beat, TGVoiceImpl voice) {
		Iterator<TGNote> notes = voice.getNotes().iterator();
		while(notes.hasNext()){
			TGNoteImpl note = (TGNoteImpl)notes.next();
			beat.updateEffectsSpacing(layout, note.getEffect());
			note.update(layout);
		}
	}

	private void updateDivisionFlags(TGVoiceImpl voice, int v) {
		if (v % 2 == 0) {
			this.division1 = (this.division1 || hasIrregularDivision(voice));
		} else {
			this.division2 = (this.division2 || hasIrregularDivision(voice));
		}
	}

	private boolean hasIrregularDivision(TGVoiceImpl voice) {
		return !voice.getDuration().getDivision().isEqual(TGDivisionType.NORMAL);
	}

	private void updateVerticalBounds(TGLayout layout, TGVoiceImpl voice) {
		if( isScoreEnabled(layout) && !isVisibleRest(voice) ){
			return;
		}
		if( voice.getMaxY() > this.maxY ){
			this.maxY = voice.getMaxY();
		}
		if( voice.getMinY() < this.minY ){
			this.minY = voice.getMinY();
		}
	}

	private boolean isVisibleRest(TGVoiceImpl voice) {
		return ( voice.isRestVoice() && !voice.isHiddenSilence() );
	}

	private void updateBeatEffectVerticalSpacing(TGLayout layout, TGBeatImpl beat) {
		float bsSize = beat.getEffectsSpacing(layout);
		if( bsSize > this.beatEffectVerticalSpacing ){
			this.beatEffectVerticalSpacing = bsSize;
		}
	}

	private void updateBeatFlags(TGBeatImpl beat) {
		this.chord = (this.chord || beat.isChordBeat());
		this.text = (this.text || beat.isTextBeat());
		this.pickStroke = (this.pickStroke || beat.isPickStroke());
		this.bend = (this.bend || beat.isBend());
	}

	private void checkVoiceGroupValues(TGLayout layout) {
		for(int i = 0; i < this.voiceGroups.length; i ++){
			Iterator<TGBeatGroup> groups = this.voiceGroups[i].iterator();
			while (groups.hasNext()) {
				TGBeatGroup group = groups.next();
				checkValue(layout,group.getMinNote(),group.getDirection());
				checkValue(layout,group.getMaxNote(),group.getDirection());
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
	public int getNoteAccidental(int noteValue, boolean altEnharmonic){
		if( noteValue >= 0 && noteValue < 128 ){
			int keySignature = getKeySignature();
			int noteIndex = TGMusicKeyUtils.noteIndex(noteValue, keySignature, altEnharmonic);
			int octave = TGMusicKeyUtils.noteOctave(noteValue, keySignature, altEnharmonic);
			int accidentalValue = TGMusicKeyUtils.noteAccidental(noteValue, keySignature, altEnharmonic);
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
		this.pickStroke = false;
		this.bend = false;
		this.beatEffectVerticalSpacing = 0.0f;
		this.effectWidth = 0.0f;
	}

	public void registerSpacing(TGLayout layout,TGTrackSpacing ts){
		registerHeaderSpacing(layout, ts);
		registerBeatSpacing(layout, ts);
		registerEffectSpacing(layout, ts);
	}

	// room for what the measure header shows: loop markers, a marker, a repeat ending
	private void registerHeaderSpacing(TGLayout layout,TGTrackSpacing ts){
		if(layout.hasLoopMarker( this.getHeader() )){
			ts.setSize(TGTrackSpacing.POSITION_LOOP_MARKER,layout.getLoopMarkerSpacing());
		}
		if(this.hasMarker()){
			ts.setSize(TGTrackSpacing.POSITION_MARKER,layout.getMarkerSpacing());
		}
		if(this.getHeader().getRepeatAlternative() > 0){
			ts.setSize(TGTrackSpacing.POSITION_REPEAT_ENDING,layout.getRepeatEndingSpacing());
		}
	}

	// room for what the beats show above the staves: chords, texts, tuplets
	private void registerBeatSpacing(TGLayout layout,TGTrackSpacing ts){
		if(this.chord){
			ts.setSize(TGTrackSpacing.POSITION_CHORD,layout.getDefaultChordSpacing());
		}
		if(this.text){
			ts.setSize(TGTrackSpacing.POSITION_TEXT,layout.getTextSpacing());
		}
		if(this.division1){
			ts.setSize(TGTrackSpacing.POSITION_DIVISION_TYPE_1,layout.getDivisionTypeSpacing());
		}
		if(this.division2){
			ts.setSize(TGTrackSpacing.POSITION_DIVISION_TYPE_2,layout.getDivisionTypeSpacing());
		}
	}

	// room for the pick strokes, the effects and the bends of the beats
	private void registerEffectSpacing(TGLayout layout,TGTrackSpacing ts){
		if(this.pickStroke){
			ts.setSize(TGTrackSpacing.POSITION_PICK_STROKES,layout.getPickStrokeSpacing());
		}
		if( this.beatEffectVerticalSpacing > 0 ){
			ts.setSize(TGTrackSpacing.POSITION_EFFECTS, this.beatEffectVerticalSpacing );
		}
		// bends are only displayed if tab is shown
		if (this.bend && (layout.getStyle() & TGLayout.DISPLAY_TABLATURE) != 0 ) {
			ts.setSize(TGTrackSpacing.POSITION_BEND, layout.getBendSpacing());
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

			UIColor background = this.isPlaying(layout) ? layout.getResources().getBackgroundColorPlaying() : layout.getResources().getBackgroundColor();
			painter.setBackground(background);

			if(!bufferEnabled || shouldRepaintBuffer(resourceBuffer, this.isPlaying(layout), layout.isLastMeasure(this))){
				if( bufferEnabled ){
					UIResourceFactory factory = layout.getComponent().getResourceFactory();
					UIPainter bufferPainter = getBuffer().createBuffer(resourceBuffer, factory, getWidth(layout) + getSpacing(), getTs().getSize(), background);
					paintContents(layout, bufferPainter, 0, 0);
					bufferPainter.dispose();
				}else{
					paintContents(layout, painter, getPosX(), getPosY());
				}
				setBufferCreated(true);
			}
			if( bufferEnabled ){
				painter.setBackground(background);
				getBuffer().paintBuffer(resourceBuffer, painter, getPosX(), getPosY());
			}
		}
		this.wasPlayingWhenLastPainted = this.isPlaying(layout);
		this.wasLastWhenLastPainted = layout.isLastMeasure(this);
	}

	// the staff lines (in the invalid color when the measure's durations do not add up), then all the measure shows
	private void paintContents(TGLayout layout, UIPainter painter, float x, float y){
		boolean measureIsValid = (layout.getSongManager().getMeasureManager().isMeasureDurationValid(this));
		layout.paintLines(getTrackImpl(), getTs(), painter, x, y, getWidth(layout) + getSpacing(), measureIsValid);
		paintTimeSignature(layout, painter, x, y);
		paintClef(layout, painter, x, y);
		paintKeySignature(layout, painter, x, y);
		paintComponents(layout, painter, x, y);
		paintDivisions(layout,painter, x, y);
		paintMarker(layout, painter, x, y);
		paintTexts(layout, painter, x, y);
		paintTempo(layout, painter, x, y);
		paintTripletFeel(layout, painter, x, y);
		paintRepeatEnding(layout, painter, x, y);
		paintLoopMarker(layout, painter, x, y);
	}

	private boolean shouldRepaintBuffer(TGResourceBuffer resourceBuffer, boolean isPlaying, boolean isLast){
		return (!isBufferCreated() || getBuffer().isDisposed(resourceBuffer) || (isPlaying != this.wasPlayingWhenLastPainted)
				|| (isLast != this.wasLastWhenLastPainted));
	}

	private void paintRepeatEnding(TGLayout layout,UIPainter painter, float fromX, float fromY){
		if(getHeader().getRepeatAlternative() > 0){
			float scale = layout.getScale();
			float x1 = (fromX + getHeaderImpl().getLeftSpacing(layout) + getFirstNoteSpacing(layout));
			float x2 = (fromX + getWidth(layout) + getSpacing());
			float y1 = (fromY + getTs().getPosition(TGTrackSpacing.POSITION_REPEAT_ENDING));
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
			beat.paint(layout, painter, x, fromY, false);
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
		for(Tuplet tuplet : this.findTuplets(layout, fromX, voiceIndex)) {
			this.paintDivisionType(layout, painter, tuplet.divisionType, tuplet.x1, tuplet.x2, fromY, voiceIndex);
		}
	}

	// the voice's tuplets, in order
	private List<Tuplet> findTuplets(TGLayout layout, float fromX, int voiceIndex){
		List<Tuplet> tuplets = new ArrayList<Tuplet>();
		Tuplet tuplet = null;
		for(TGBeat tgBeat : getBeats()) {
			TGBeatImpl beat = (TGBeatImpl)tgBeat;
			TGVoiceImpl voice = beat.getVoiceImpl(voiceIndex);
			if( !voice.isEmpty() ) {
				tuplet = this.nextTuplet(tuplet, tuplets, voice.getDuration(), (fromX + beat.getPosX() + beat.getSpacing(layout)));
			}
			// update current tuplet if any
			if( tuplet != null ) {
				tuplet.add(voice.getDuration());
			}
		}
		// end of measure, with a tuplet already started?
		if( tuplet != null ) {
			tuplets.add(tuplet);
		}
		return tuplets;
	}

	// the tuplet a note of this duration at x belongs to: the current one is closed first if it ends there
	private Tuplet nextTuplet(Tuplet current, List<Tuplet> tuplets, TGDuration duration, float x){
		Tuplet tuplet = current;
		// end of tuplet?
		if( tuplet != null && tuplet.endsBefore(duration) ) {
			tuplets.add(tuplet);
			tuplet = null;
		}
		// beginning of tuplet?
		if( !duration.getDivision().isEqual(TGDivisionType.NORMAL) ) {
			if( tuplet == null ) {
				tuplet = new Tuplet(duration.getDivision(), x);
			}
			tuplet.x2 = x;
		}
		return tuplet;
	}

	/** A run of beats in one tuplet division, bracketed from x1 to x2. */
	private static class Tuplet {
		private final TGDivisionType divisionType;
		private final float x1;
		private float x2;
		private long preciseDuration;	// sum of all notes precise duration inside the tuplet
		private long shortestPreciseDuration;	// the shortest note duration inside the tuplet

		Tuplet(TGDivisionType divisionType, float x) {
			this.divisionType = divisionType;
			this.x1 = x;
			this.x2 = x;
		}

		// change of time division OR tuplet effective duration is a multiple of shortest note * Enters
		// e.g. for a triolet of eighth, shortest in triolet is (2/3)*(1/8), so duration shall reach a multiple of (2/3) * (1/8) * 3 = 2*1/8 = 2 eighth
		boolean endsBefore(TGDuration duration) {
			return ((!duration.getDivision().isEqual(this.divisionType)) || (this.preciseDuration % (this.divisionType.getEnters() * this.shortestPreciseDuration) == 0));
		}

		void add(TGDuration duration) {
			long preciseTime = duration.getPreciseTime();
			this.preciseDuration += preciseTime;
			if ((this.shortestPreciseDuration == 0) || (preciseTime < this.shortestPreciseDuration)) {
				this.shortestPreciseDuration = preciseTime;
			}
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
	private void paintDivisions(TGLayout layout,UIPainter painter, float fromX, float fromY) {
		int style = layout.getStyle();
		if((style & TGLayout.DISPLAY_SCORE) != 0 ){
			paintScoreDivisions(layout, painter, fromX, fromY);
		}
		if( (style & TGLayout.DISPLAY_TABLATURE) != 0 ){
			paintTablatureDivisions(layout, painter, fromX, fromY);
		}
	}

	// on the score, with the measure number; at the start of a line, the start bar runs down to the tablature
	private void paintScoreDivisions(TGLayout layout,UIPainter painter, float fromX, float fromY) {
		float y1 = fromY + getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES);
		float y2 = y1 + (layout.getScoreLineSpacing() * 4);
		float offsetY = 0;
		if( (layout.getStyle() & TGLayout.DISPLAY_TABLATURE) != 0 && (layout.isFirstMeasure(this) || isFirstOfLine())){
			offsetY = ( fromY + getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE)) - y2;
		}
		paintDivisions(layout, painter, fromX, y1, fromX + getWidth(layout), y2, offsetY, true );
	}

	// on the tablature, from its first to its last string (a string spacing around a single string), with the
	// measure number when the score is hidden
	private void paintTablatureDivisions(TGLayout layout,UIPainter painter, float fromX, float fromY) {
		float y1 = fromY + getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE);
		float y2 = y1 + (getTrack().stringCount() > 1 ? ((getTrack().stringCount() - 1 ) * layout.getStringSpacing()) : 0f);
		if( y1 == y2 ) {
			y1 -= layout.getStringSpacing();
			y2 += layout.getStringSpacing();
		}
		boolean addInfo = ( (layout.getStyle() & TGLayout.DISPLAY_SCORE) == 0 );
		paintDivisions(layout, painter, fromX, y1, fromX + getWidth(layout), y2, 0, addInfo );
	}

	private void paintDivisions(TGLayout layout,UIPainter painter,float x1, float y1, float x2, float y2, float offsetY, boolean addInfo) {
		float scale = layout.getScale();

		//numero de compas
		if( addInfo ){
			String number = Integer.toString(this.getNumber());
			layout.setMeasureNumberStyle(painter);
			painter.drawString(number, x1 + scale, y1 + painter.getFMBaseLine() - (2f * scale));
		}

		layout.setDivisionsStyle(painter, true);
		paintStartDivision(layout, painter, x1, y1, y2, offsetY);
		paintEndDivision(layout, painter, x2, y1, y2, addInfo);
		painter.setLineWidth(layout.getLineWidth(1));
	}

	// thick at the start of the song or of a repeat (with the repeat's dots), else thin
	private void paintStartDivision(TGLayout layout,UIPainter painter,float x1, float y1, float y2, float offsetY) {
		float scale = layout.getScale();
		float lineWidthSmall = layout.getLineWidth(1);
		float lineWidthBig = layout.getLineWidth(3);
		if( this.isRepeatOpen() || layout.isFirstMeasure(this) ){
			paintBar(layout, painter, x1, y1, lineWidthBig, (y2 + offsetY) - y1);
			paintBar(layout, painter, x1 + lineWidthBig + (2f * scale), y1, lineWidthSmall, (y2 + offsetY) - y1);

			if( this.isRepeatOpen() ){
				float size = layout.getLineWidth(4);
				float xMove = (((lineWidthBig + scale + lineWidthSmall) + (2f * scale)) + size);
				paintRepeatDots(layout, painter, x1 + xMove, y1, y2, size);
			}
		} else {
			paintBarLine(layout, painter, x1, y1, (y2 + offsetY));
		}
	}

	// thick at the end of the song or of a repeat (with the repeat's dots and count), thin at the end of a line
	private void paintEndDivision(TGLayout layout,UIPainter painter,float x2, float y1, float y2, boolean addInfo) {
		float scale = layout.getScale();
		float lineWidthSmall = layout.getLineWidth(1);
		float lineWidthBig = layout.getLineWidth(3);
		if( this.getRepeatClose() > 0 || layout.isLastMeasure(this) ){
			paintBar(layout, painter, (x2 + getSpacing()) - (lineWidthBig + lineWidthSmall + (2f * scale)), y1, lineWidthSmall, (y2 - y1));
			paintBar(layout, painter, (x2 + getSpacing()) - lineWidthBig, y1, lineWidthBig, (y2 - y1));

			if( this.getRepeatClose() > 0 ){
				float size = layout.getLineWidth(4);
				float xMove = (((lineWidthBig + scale + lineWidthSmall) + (2f * scale)) + size);
				paintRepeatDots(layout, painter, (x2 - xMove) + getSpacing(), y1, y2, size);
				if( addInfo ){
					layout.setDivisionsStyle(painter,false);

					String repetitions = ("x" + this.getRepeatClose());
					painter.drawString(repetitions, x2 - painter.getFMWidth(repetitions) + getSpacing() - size, y1 + painter.getFMBaseLine() - (2f * scale));
				}
			}
		} else if (islastOfLine()) {
			paintBarLine(layout, painter, (x2 + getSpacing())-1, y1, y2);
		}
	}

	private static void paintBar(TGLayout layout,UIPainter painter,float x, float y, float width, float height) {
		painter.setLineWidth(layout.getLineWidth(0));
		painter.initPath(UIPainter.PATH_DRAW | UIPainter.PATH_FILL);
		painter.setAntialias(false);
		painter.addRectangle(x, y, width, height);
		painter.closePath();
	}

	private static void paintBarLine(TGLayout layout,UIPainter painter,float x, float y1, float y2) {
		painter.setLineWidth(layout.getLineWidth(0));
		painter.initPath(UIPainter.PATH_DRAW | UIPainter.PATH_FILL);
		painter.setAntialias(false);
		painter.moveTo(x, y1);
		painter.lineTo(x, y2);
		painter.closePath();
	}

	// two dots, above and below the middle of the staff
	private static void paintRepeatDots(TGLayout layout,UIPainter painter,float x, float y1, float y2, float size) {
		painter.setLineWidth(layout.getLineWidth(1));
		painter.initPath(UIPainter.PATH_FILL);
		painter.moveTo(x, y1 + ((y2 - y1) / 2f) - size);
		painter.addCircle(x, y1 + ((y2 - y1) / 2f) - size, size);
		painter.moveTo(x, y1 + ((y2 - y1) / 2f) + size);
		painter.addCircle(x, y1 + ((y2 - y1) / 2f) + size, size);
		painter.closePath();
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

			// naturals cancel the previous key's accidentals the current key does not keep
			int kept = (isFlatKey(previousKey) == isFlatKey(currentKey) ? countAccidentals(currentKey) : 0);
			x = paintKeyAccidentals(painter, TGKeySignaturePainter::paintNatural, keyPositions(previousKey, clefIndex), kept, countAccidentals(previousKey), x, y, scale);
			paintKeyAccidentals(painter, keySymbol(currentKey), keyPositions(currentKey, clefIndex), 0, countAccidentals(currentKey), x, y, scale);
		}
	}

	private interface TGKeySymbol {
		void paint(UIPainter painter, float x, float y, float scale);
	}

	// key 1 to 7 is that many sharps, key 8 to 14 that many flats (minus 7), anything else none
	private static boolean isSharpKey(int key) {
		return (key >= 1 && key <= 7);
	}

	private static boolean isFlatKey(int key) {
		return (key >= 8 && key <= 14);
	}

	private static int countAccidentals(int key) {
		if( isSharpKey(key) ) {
			return key;
		}
		return (isFlatKey(key) ? key - 7 : 0);
	}

	private static TGKeySymbol keySymbol(int key) {
		return (isFlatKey(key) ? TGKeySignaturePainter::paintFlat : TGKeySignaturePainter::paintSharp);
	}

	// the score positions of the key's accidentals, in order, for the clef
	private static int[] keyPositions(int key, int clefIndex) {
		return (isFlatKey(key) ? SCORE_KEY_FLAT_POSITIONS[clefIndex] : SCORE_KEY_SHARP_POSITIONS[clefIndex]);
	}

	// paints the symbol at positions [from, to), left to right from x; returns the x after the last one
	private static float paintKeyAccidentals(UIPainter painter, TGKeySymbol symbol, int[] positions, int from, int to, float x, float y, float scale) {
		float nextX = x;
		for(int i = from; i < to; i ++ ){
			float offset =  ( ( (scale / 2) * positions[i] )  - (scale / 2) );
			painter.initPath(UIPainter.PATH_FILL);
			symbol.paint(painter, nextX, (y +  offset  ), scale);
			painter.closePath();
			nextX += (scale - (scale / 4));
		}
		return nextX;
	}

	private void paintTimeSignature(TGLayout layout,UIPainter painter, float fromX, float fromY){
		if(this.getHeaderImpl().shouldPaintTimeSignature()){
			int style = layout.getStyle();
			float x = (fromX + getHeaderImpl().getLeftSpacing(layout) + getClefSpacing(layout) + getKeySignatureSpacing(layout));
			float yMiddle = 0f;
			float height = 0;
			if((style & TGLayout.DISPLAY_SCORE) != 0 ) {
				yMiddle = getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES) + (getTrackImpl().getScoreHeight() / 2.0f);
				height = ((layout.getScoreLineSpacing() * 2f) - layout.getScale());
			} else if((style & TGLayout.DISPLAY_TABLATURE) != 0 ) {
				yMiddle = getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) + (getTrackImpl().getTabHeight() / 2.0f);
				height = ((layout.getStringSpacing() * 3f) / 2f);
			}

			float w1 = TGNumberPainter.getDigitsWidth(getTimeSignature().getNumerator(), height);
			float w2 = TGNumberPainter.getDigitsWidth(getTimeSignature().getDenominator().getValue(), height);
			float wMove1 = (w2 > w1 ? (w2 - w1) / 2f : 0f);
			float wMove2 = (w1 > w2 ? (w1 - w2) / 2f : 0f);

			layout.setTimeSignatureStyle(painter);
			TGNumberPainter.paintDigits(getTimeSignature().getNumerator(), painter, x + wMove1, fromY + yMiddle - height, height);
			TGNumberPainter.paintDigits(getTimeSignature().getDenominator().getValue(), painter, x + wMove2, fromY + yMiddle, height);
		}
	}

	private void paintTempo(TGLayout layout,UIPainter painter, float fromX, float fromY){
		if(this.getHeaderImpl().shouldPaintTempo()){
			float scale = 5f * layout.getScale();
			float x = (fromX + getHeaderImpl().getLeftSpacing(layout));
			float y = fromY;
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

			TGTempoPainter.paintTempo(painter, imgX, imgY, scale, getTempo().getBase(), getTempo().isDotted());

			layout.setTempoStyle(painter, true);
			String value = (" = " + getTempo().getRawValue());
			float fontX = x + (Math.round( (1.75f * scale) ) + 1 );
			float fontY = (y - (0.75f * scale) + painter.getFMBaseLine());
			painter.drawString(value , fontX, fontY);
		}
	}

	private void paintTripletFeel(TGLayout layout,UIPainter painter, float fromX, float fromY){
		if(!this.getHeaderImpl().shouldPaintTripletFeel()){
			return;
		}
		float scale = (5f * layout.getScale());
		float x = (fromX + getHeaderImpl().getLeftSpacing(layout) + getHeaderImpl().getTempoSpacing(layout));
		float y = (fromY + getTripletFeelOffsetY(layout));

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

		// written = played: straight then swung when a triplet feel starts, swung then straight when it ends
		boolean ends = (getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_NONE && this.previousMeasure != null);
		int feel = (ends ? this.previousMeasure.getTripletFeel() : getTripletFeel());
		paintTripletFeelPair(painter, feel, ends, x1, y1, y2, scale);
		paintTripletFeelPair(painter, feel, !ends, x2, y1, y2, scale);
	}

	// above the score middle lines, else above the tablature (leaving room for the line of a single string)
	private float getTripletFeelOffsetY(TGLayout layout){
		float lineSpacing = (Math.max(layout.getScoreLineSpacing() , layout.getStringSpacing()));
		if( (layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0 ){
			return ( getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES) - lineSpacing );
		}
		if( (layout.getStyle() & TGLayout.DISPLAY_TABLATURE) != 0 ){
			return ( getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) - (getTrack().stringCount() > 1 ? lineSpacing : (lineSpacing * 2f)));
		}
		return 0;
	}

	// a pair of eighths or sixteenths of the triplet feel, swung (painted higher, at y2) or straight (at y1)
	private static void paintTripletFeelPair(UIPainter painter, int feel, boolean swung, float x, float y1, float y2, float scale){
		if(feel == TGMeasureHeader.TRIPLET_FEEL_EIGHTH){
			if(swung){
				TGTripletFeelPainter.paintTripletFeel8(painter, x, y2, scale );
			}else{
				TGTripletFeelPainter.paintTripletFeelNone8(painter, x, y1, scale );
			}
		}else if(feel == TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH){
			if(swung){
				TGTripletFeelPainter.paintTripletFeel16(painter, x, y2, scale );
			}else{
				TGTripletFeelPainter.paintTripletFeelNone16(painter, x, y1, scale );
			}
		}
	}

	private void paintLoopMarker(TGLayout layout,UIPainter painter, float fromX, float fromY){
		if( layout.hasLoopMarker( this ) ){
			int size = Math.round( layout.getLoopMarkerSpacing() - (1f * layout.getScale()));
			if( layout.getComponent().isLoopSHeader(this.getHeader())){
				float x = (fromX + Math.round(size / 2f) );
				float y = (fromY + getTs().getPosition(TGTrackSpacing.POSITION_LOOP_MARKER));
				layout.setLoopSMarkerStyle(painter);
				paintLoopMarker(painter, x, y, size);
			}
			if( layout.getComponent().isLoopEHeader(this.getHeader())){
				float x = (fromX + getWidth(layout) + getSpacing() - size );
				float y = (fromY + getTs().getPosition(TGTrackSpacing.POSITION_LOOP_MARKER));
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

	private void paintMarker(TGLayout layout, UIPainter painter, float fromX, float fromY){
		if( this.hasMarker() ){
			float x = (fromX + getHeaderImpl().getLeftSpacing(layout) + getFirstNoteSpacing(layout));
			float y = (fromY + getTs().getPosition(TGTrackSpacing.POSITION_MARKER));

			layout.setMarkerStyle(painter, getMarkerColor(layout.getResourceBuffer(), layout.getComponent().getResourceFactory()));
			painter.drawString(getMarker().getTitle(), x, y);
		}
	}

	private void paintTexts(TGLayout layout,UIPainter painter, float fromX, float fromY){
		Iterator<TGBeat> it = getBeats().iterator();
		while(it.hasNext()){
			TGBeat beat = it.next();
			if( beat.isTextBeat() ){
				TGTextImpl text = (TGTextImpl)beat.getText();
				text.paint(layout, painter,(fromX + getHeaderImpl().getLeftSpacing(layout) ),fromY);
			}
		}
	}

	// returns vertical position of measure, only y data is relevant
	public UIRectangle getVerticalPosition(TGLayout layout){
		float y1 = getPosY();
		float y2 = y1;
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
		return new UIRectangle(0,y1,0,y2-y1);
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

	public boolean islastOfLine() {
		return this.lastOfLine;
	}

	public void setLastOfLine(boolean lastOfLine) {
		this.lastOfLine = lastOfLine;
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
		TGColor color = getMarker().getColor();
		UIColor markerColor = buffer.getResource(resourceKey);
		if( markerColor == null || markerColor.isDisposed() || !isSameColor(markerColor, color) ){
			markerColor = factory.createColor(color.getR(), color.getG(), color.getB());

			// replacing the kept color disposes it
			buffer.setResource(resourceKey, markerColor);
		}
		return markerColor;
	}

	private static boolean isSameColor(UIColor uiColor, TGColor color){
		return (uiColor.getRed() == color.getR() && uiColor.getGreen() == color.getG() && uiColor.getBlue() == color.getB());
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

	// a line spacing above the top staff: the score when shown, else the tablature
	private float getTopEdge(TGLayout layout) {
		int style = layout.getStyle();
		if ((style & TGLayout.DISPLAY_SCORE) != 0) {
			return getPosY() + (getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES) - layout.getScoreLineSpacing());
		} else if ((style & TGLayout.DISPLAY_TABLATURE) != 0) {
			return getPosY() + (getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) - layout.getStringSpacing());
		}
		return getPosY();
	}

	// a line spacing below the bottom staff: the tablature when shown, else the score
	private float getBottomEdge(TGLayout layout) {
		int style = layout.getStyle();
		if ((style & TGLayout.DISPLAY_TABLATURE) != 0) {
			return getPosY() + (getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) + getTrackImpl().getTabHeight() + layout.getStringSpacing());
		} else if ((style & TGLayout.DISPLAY_SCORE) != 0) {
			return getPosY() + (getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES) + (layout.getScoreLineSpacing() * 5));
		}
		return getPosY();
	}

	public boolean isLineBreak() {
		return getHeaderImpl().isLineBreak();
	}
}
