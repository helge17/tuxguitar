package org.herac.tuxguitar.io.tef.base;

public abstract class TEComponent {
	
	private int position;
	
	private int measure;
	
	private int string;
	
	public TEComponent(int position,int measure, int string) {
		this.position = position;
		this.measure = measure;
		this.string = string;
	}
	
	public int getPosition() {
		return this.position;
	}
	
	public int getMeasure() {
		return this.measure;
	}
	
	public int getString() {
		return this.string;
	}
}
