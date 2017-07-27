package org.herac.tuxguitar.ui.resource;

public class UIColorModel {
	
	private int red;
	private int green;
	private int blue;
	
	public UIColorModel(){
		this(0, 0, 0);
	}
	
	public UIColorModel(int red, int green, int blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public int getRed() {
		return this.red;
	}
	
	public void setRed(int red) {
		this.red = red;
	}
	
	public int getGreen() {
		return this.green;
	}
	
	public void setGreen(int green) {
		this.green = green;
	}
	
	public int getBlue() {
		return this.blue;
	}
	
	public void setBlue(int blue) {
		this.blue = blue;
	}
}
