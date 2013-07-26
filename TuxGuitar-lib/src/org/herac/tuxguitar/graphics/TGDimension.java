package org.herac.tuxguitar.graphics;

public class TGDimension {
	
	private int width;
	private int height;
	
	public TGDimension(int width,int height){
		this.width = width;
		this.height = height;
	}
	
	public TGDimension(){
		this(0,0);
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
}
