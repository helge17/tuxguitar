/*
 * Created on 23-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.song.models;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGBeat {
	private long start;
	private TGDuration duration;
	private TGMeasure measure;
	
	private List notes;
	private TGChord chord;
	private TGText text;
	
	public TGBeat(TGFactory factory) {
		this.start = TGDuration.QUARTER_TIME;
		this.duration = factory.newDuration();
		this.notes = new ArrayList();
	}
	
	public long getStart() {
		return this.start;
	}
	
	public void setStart(long start) {
		this.start = start;
	}
	
	public TGDuration getDuration() {
		return this.duration;
	}
	
	public void setDuration(TGDuration duration) {
		this.duration = duration;
	}
	
	public TGMeasure getMeasure() {
		return this.measure;
	}
	
	public void setMeasure(TGMeasure measure) {
		this.measure = measure;
	}
	
	public List getNotes() {
		return this.notes;
	}
	
	public void addNote(TGNote note){
		note.setBeat(this);
		this.notes.add(note);
	}
	
	public void moveNote(int index,TGNote note){
		getNotes().remove(note);
		getNotes().add(index,note);
	}
	
	public void removeNote(TGNote note){
		this.notes.remove(note);
	}
	
	public TGNote getNote(int index){
		if(index >= 0 && index < countNotes()){
			return (TGNote)this.notes.get(index);
		}
		return null;
	}
	
	public int countNotes(){
		return this.notes.size();
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
	
	public boolean isRestBeat(){
		return this.notes.isEmpty();
	}
	
	public boolean isChordBeat(){
		return ( this.chord != null );
	}
	
	public boolean isTextBeat(){
		return ( this.text != null );
	}
	
	public TGBeat clone(TGFactory factory){
		TGBeat beat = factory.newBeat();
		beat.setStart(getStart());
		getDuration().copy(beat.getDuration());
		
		for(int i = 0;i < countNotes();i++){
			TGNote note = (TGNote)this.notes.get(i);
			beat.addNote(note.clone(factory));
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