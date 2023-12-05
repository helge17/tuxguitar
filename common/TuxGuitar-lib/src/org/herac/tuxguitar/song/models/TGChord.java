/*
 * Created on 29-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGChord {
	private int firstFret;
	private int[] strings;
	private String name;
	private TGBeat beat;
	
	public TGChord(int length){
		this.strings = new int[length];
		for(int i = 0;i < this.strings.length;i++){
			this.strings[i] = -1;
		}
	}
	
	public TGBeat getBeat() {
		return this.beat;
	}
	
	public void setBeat(TGBeat beat) {
		this.beat = beat;
	}
	
	public void addFretValue(int string,int fret){
		if(string >= 0 && string < this.strings.length){
			this.strings[string] = fret;
		}
	}
	
	public int getFretValue(int string){
		if(string >= 0 && string < this.strings.length){
			return this.strings[string];
		}
		return -1;
	}
	
	public int getFirstFret() {
		return this.firstFret;
	}
	
	public void setFirstFret(int firstFret) {
		this.firstFret = firstFret;
	}
	
	public int[] getStrings() {
		return this.strings;
	}
	
	public int countStrings(){
		return this.strings.length;
	}
	
	public int countNotes(){
		int count = 0;
		for(int i = 0;i < this.strings.length;i++){
			if(this.strings[i] >= 0){
				count ++;
			}
		}
		return count;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public TGChord clone(TGFactory factory){
		TGChord chord = factory.newChord(this.strings.length);
		chord.setName(getName());
		chord.setFirstFret(getFirstFret());
		for(int i = 0;i < chord.strings.length;i++){
			chord.strings[i] = this.strings[i];
		}
		return chord;
	}
	
}
