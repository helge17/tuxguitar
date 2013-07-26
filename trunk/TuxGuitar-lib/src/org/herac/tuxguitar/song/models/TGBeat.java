/*
 * Created on 23-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGBeat {
	
	public static final int MAX_VOICES = 2;
	
	private long start;
	private TGMeasure measure;
	private TGChord chord;
	private TGText text;
	private TGVoice[] voices;
	private TGStroke stroke;
	
	public TGBeat(TGFactory factory) {
		this.start = TGDuration.QUARTER_TIME;
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
		return this.start;
	}
	
	public void setStart(long start) {
		this.start = start;
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
	
	public TGBeat clone(TGFactory factory){
		TGBeat beat = factory.newBeat();
		beat.setStart(getStart());
		getStroke().copy( beat.getStroke() );
		for( int i = 0 ; i < this.voices.length ; i ++ ){
			beat.setVoice(i, this.voices[i].clone(factory));
		}
		if(this.chord != null){
			beat.setChord( this.chord.clone(factory));
		}
		if(this.text != null){
			beat.setText( this.text.clone(factory));
		}
		return beat;
	}
}