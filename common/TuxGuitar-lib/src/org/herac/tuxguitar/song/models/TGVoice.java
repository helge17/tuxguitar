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
public abstract class TGVoice {
	
	public static final int DIRECTION_NONE = 0;
	public static final int DIRECTION_UP = 1;
	public static final int DIRECTION_DOWN = 2;
	
	private TGBeat beat;
	private TGDuration duration;
	private List<TGNote> notes;
	private int index;
	private int direction;
	private boolean empty;
	
	public TGVoice(TGFactory factory, int index) {
		this.duration = factory.newDuration();
		this.notes = new ArrayList<TGNote>();
		this.index = index;
		this.empty = true;
		this.direction = DIRECTION_NONE;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public boolean isEmpty() {
		return this.empty;
	}
	
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	
	public int getDirection() {
		return this.direction;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public TGDuration getDuration() {
		return this.duration;
	}
	
	public void setDuration(TGDuration duration) {
		this.duration = duration;
	}
	
	public TGBeat getBeat() {
		return this.beat;
	}

	public void setBeat(TGBeat beat) {
		this.beat = beat;
	}

	public List<TGNote> getNotes() {
		return this.notes;
	}
	
	public void addNote(TGNote note){
		note.setVoice(this);
		this.notes.add(note);
		this.setEmpty(false);
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
			return this.notes.get(index);
		}
		return null;
	}
	
	public int countNotes(){
		return this.notes.size();
	}
	
	public boolean isRestVoice(){
		return this.notes.isEmpty();
	}
	
	public TGVoice clone(TGFactory factory){
		TGVoice voice = factory.newVoice(getIndex());
		voice.setEmpty(isEmpty());
		voice.setDirection( getDirection() );
		voice.getDuration().copyFrom(getDuration());
		for(int i = 0;i < countNotes();i++){
			TGNote note = this.notes.get(i);
			voice.addNote(note.clone(factory));
		}
		return voice;
	}
	
}