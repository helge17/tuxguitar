package org.herac.tuxguitar.io.abc.base;

public class ABCComponentChord extends ABCComponent{
	
	private int chord;
	
	public ABCComponentChord(int position,int measure, int string, int chord) {
		super(position, measure, string);
		this.chord = chord;
	}
	
	public int getChord() {
		return this.chord;
	}
	
	public String toString(){
		String string = new String("[NOTE]");
		string += "\n     Measure:   " + getMeasure();
		string += "\n     Position:  " + getPosition();
		string += "\n     String:    " + getString();
		string += "\n     Chord:     " + getChord();
		return string;
	}
}
