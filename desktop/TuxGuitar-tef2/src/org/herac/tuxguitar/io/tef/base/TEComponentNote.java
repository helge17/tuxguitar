package org.herac.tuxguitar.io.tef2.base;

public class TEComponentNote extends TEComponent{
	
	private int fret;
	
	private int duration;
	
	private int dynamic;
	
	private int effect1;
	private int effect2;
	
	public TEComponentNote(int position,int measure, int string, int fret, int duration, int dynamic, int effect1, int effect2) {
		super(position, measure, string);
		this.fret = fret;
		this.duration = duration;
		this.dynamic = dynamic;
		this.effect1 = effect1;
		this.effect2 = effect2;
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	public int getDynamic() {
		return this.dynamic;
	}
	
	public int getEffect1() {
		return this.effect1;
	}

	public int getEffect2() {
		return this.effect2;
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