/*
 * Created on 23-nov-2005
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 */

// Comparable: to be able to sort beats per start value
public abstract class TGBeat implements Comparable<TGBeat> {

	public static final int MAX_VOICES = 2;

	/*
	 * beat start can be defined under 2 different formats:
	 * - start: legacy, can lead to rounding errors
	 * - preciseStart: with absolute precision. Warning, may not always be defined (negative if not defined)
	 * None of these values consider repeats. They only consider previous measures (i.e. measures with lower measure numbers) and beats
	 */
	private long start;
	private Long preciseStart;
	private TGMeasure measure;
	private TGChord chord;
	private TGText text;
	private TGVoice[] voices;
	private TGStroke stroke;
	private TGPickStroke pickStroke;

	public TGBeat(TGFactory factory) {
		this.preciseStart = TGDuration.getPreciseStartingPoint();
		this.start = TGDuration.getStartingPoint();
		this.stroke = factory.newStroke();
		this.pickStroke = factory.newPickStroke();
		this.voices = new TGVoice[ MAX_VOICES ];
		for( int i = 0 ; i < MAX_VOICES ; i ++ ){
			this.setVoice(i, factory.newVoice(i));
		}
	}

	public TGMeasure getMeasure() {
		return this.measure;
	}

	public void setMeasure(TGMeasure measure) {
		this.measure = measure;
	}

	public long getStart() {
		// prefer precise start if available
		if (this.preciseStart != null) {
			return TGDuration.toTime(this.preciseStart);
		}
		return this.start;
	}

	public Long getPreciseStart() {
		return this.preciseStart;
	}
	
	/**
	 * Deprecated: define beat "approximative" start
	 * 
	 * Resolution of beat start = one midi tick, not precise enough to handle all possible tuplets
	 * Only use for legacy purposes (e.g. import old/foreign file formats)
	 * When this method needs to be called (for historical reasons), make sure to update preciseStart attribute afterwards
	 * Dedicated methods are available for that in TGMeasureManager
	 * 
	 * Replace by .setPreciseStart (refer to TGDuration)
	 * 
	 * @param start
	 */
	@Deprecated
	public void setStart(long start) {
		this.start = start;
		// cannot deduce preciseStart from start (possible rounding errors)
		this.preciseStart = null;
	}

	public void setPreciseStart(long pStart) {
		this.preciseStart = pStart;
		this.start = TGDuration.toTime(pStart);
	}

	public void setVoice(int index, TGVoice voice){
		if( index >= 0 && index < this.voices.length ){
			this.voices[index] = voice;
			this.voices[index].setBeat( this );
		}
	}

	public TGVoice getVoice(int index){
		if( index >= 0 && index < this.voices.length ){
			return this.voices[index];
		}
		return null;
	}

	public int getHighestFret() {
		int highestFret = -1;
		for (int i=0; i<this.countVoices(); i++) {
			int voiceHighestFret = this.getVoice(i).getHighestFret();
			highestFret = (voiceHighestFret > highestFret ? voiceHighestFret : highestFret);
		}
		return highestFret;
	}

	public int countVoices(){
		return this.voices.length;
	}

	public void setChord(TGChord chord) {
		this.chord = chord;
		this.chord.setBeat(this);
	}

	public TGChord getChord() {
		return this.chord;
	}

	public void removeChord() {
		this.chord = null;
	}

	public TGText getText() {
		return this.text;
	}

	public void setText(TGText text) {
		this.text = text;
		this.text.setBeat(this);
	}

	public void removeText(){
		this.text = null;
	}

	public boolean isChordBeat(){
		return ( this.chord != null );
	}

	public boolean isTextBeat(){
		return ( this.text != null );
	}

	public TGStroke getStroke() {
		return this.stroke;
	}

	public TGPickStroke getPickStroke() {
		return this.pickStroke;
	}

	public boolean isPickStroke() {
		return (this.pickStroke.getDirection() != TGPickStroke.PICK_STROKE_NONE);
	}

	public boolean isRestBeat(){
		for(int v = 0; v < this.countVoices() ; v ++ ){
			TGVoice voice = this.getVoice( v );
			if( !voice.isEmpty() && !voice.isRestVoice() ){
				return false;
			}
		}
		return true;
	}

	public void resetAltEnharmonic() {
		for(int v = 0; v < this.countVoices() ; v ++ ){
			TGVoice voice = this.getVoice( v );
				voice.resetAltEnharmonic();
		}
	}

	public void copyFrom(TGBeat beat, TGFactory factory) {
		if (beat.getPreciseStart() != null) {
			this.setPreciseStart(beat.getPreciseStart());
		} else {
			this.setStart(beat.getStart());
		}
		this.getStroke().copyFrom(beat.getStroke());
		this.getPickStroke().copyFrom(beat.getPickStroke());
		for( int i = 0 ; i < beat.voices.length ; i ++ ){
			this.setVoice(i, beat.voices[i].clone(factory));
		}
		if(beat.chord != null){
			this.setChord( beat.chord.clone(factory));
		}
		if(beat.text != null){
			this.setText( beat.text.clone(factory));
		}
	}

	public TGBeat clone(TGFactory factory){
		TGBeat beat = factory.newBeat();
		beat.copyFrom(this, factory);
		return beat;
	}

	@Override
	public int compareTo(TGBeat beat) {
		if (beat == null) return 1;
		if ((this.preciseStart != null) && (beat.getPreciseStart() >= 0)) {
			return Long.valueOf(this.preciseStart).compareTo(Long.valueOf(beat.getPreciseStart()));
		}
		return (Long.valueOf(this.getStart()).compareTo(Long.valueOf(beat.getStart())));
	}
}
