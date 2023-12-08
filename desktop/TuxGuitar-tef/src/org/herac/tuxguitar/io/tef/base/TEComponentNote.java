package org.herac.tuxguitar.io.tef.base;

public class TEComponentNote extends TEComponent{
	
	private int fret;
	
	private int duration;
	
	private int dynamic;
	
	private int effect;
	
	public TEComponentNote(int position,int measure, int string, int fret, int duration, int dynamic, int effect) {
		super(position, measure, string);
		this.fret = fret;
		this.duration = duration;
		this.dynamic = dynamic;
		this.effect = effect;
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	public int getDynamic() {
		return this.dynamic;
	}
	
	public int getEffect() {
		return this.effect;
	}
	
	public int getFret() {
		return this.fret;
	}
	
	public String toString(){
		String string = new String("[NOTE]");
		string += "\n     Measure:   " + getMeasure();
		string += "\n     Position:  " + getPosition();
		string += "\n     String:    " + getString();
		string += "\n     Fret:      " + getFret();
		
		return string;
	}
}
