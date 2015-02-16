package org.herac.tuxguitar.graphics;

public class TGDimension {
	
	private float width;
	private float height;
	
	public TGDimension(float width, float height){
		this.width = width;
		this.height = height;
	}
	
	public TGDimension(){
		this(0, 0);
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public void setWidth(float width) {
		this.width = width;
	}
	
	public float getHeight() {
		return this.height;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
}
