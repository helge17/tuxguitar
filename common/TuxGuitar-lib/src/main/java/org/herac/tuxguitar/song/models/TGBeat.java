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
	private long preciseStart;
	private TGMeasure measure;
	private TGChord chord;
	private TGText text;
	private TGVoice[] voices;
	private TGStroke stroke;
	
	public TGBeat(TGFactory factory) {
		this.preciseStart = TGDuration.getPreciseStartingPoint();
		this.start = TGDuration.getStartingPoint();
		this.stroke = factory.newStroke();
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
		if (this.preciseStart >= 0) {
			return TGDuration.toTime(this.preciseStart);
		}
		return this.start;
	}
	
	public long getPreciseStart() {
		return this.preciseStart;
	}
	
	public void setStart(long start) {
		this.start = start;
		// cannot deduce preciseStart from start (possible rounding errors)
		this.preciseStart = -1;
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

	// duration (time) of shortest voice
	public long getShortestVoiceDurationTime() {
		long time = -1;
		for (int v=0; v<this.countVoices(); v++) {
			TGVoice voice = this.getVoice(v);
			if (!voice.isEmpty()) {
				if ((time<0) || (voice.getDuration().getTime()<time)) {
					time = voice.getDuration().getTime();
				}
			}
		}
		return time;
	}
	
	// duration (time) of longest voice
	public long getDurationTime() {
		long time = 0;
		for (int v=0; v<this.countVoices(); v++) {
			TGVoice voice = this.getVoice(v);
			if (!voice.isEmpty()) {
				if (voice.getDuration().getTime() > time) {
					time = voice.getDuration().getTime();
				}
			}
		}
		return time;
	}
	
	public long getEnd() {
		return this.getStart() + this.getDurationTime();
	}
	
	public void copyFrom(TGBeat beat, TGFactory factory) {
		this.setStart(beat.getStart());
		this.getStroke().copyFrom(beat.getStroke());
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
		if ((this.preciseStart >= 0) && (beat.getPreciseStart() >= 0)) {
			return Long.valueOf(this.preciseStart).compareTo(Long.valueOf(beat.getPreciseStart()));
		}
		return (Long.valueOf(this.getStart()).compareTo(Long.valueOf(beat.getStart())));
	}
}
