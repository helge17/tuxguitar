package org.herac.tuxguitar.graphics;

public class TGRectangle {
	
	private TGPoint location;
	private TGDimension size;
	
	public TGRectangle(float x, float y, float width, float height){
		this.location = new TGPoint(x, y);
		this.size = new TGDimension(width,height);
	}
	
	public TGRectangle(){
		this(0, 0, 0, 0);
	}
	
	public float getX() {
		return this.location.getX();
	}
	
	public void setX(float x) {
		this.location.setX(x);
	}
	
	public float getY() {
		return this.location.getY();
	}
	
	public void setY(float y) {
		this.location.setY(y);
	}
	
	public float getWidth() {
		return this.size.getWidth();
	}
	
	public void setWidth(float width) {
		this.size.setWidth(width);
	}
	
	public float getHeight() {
		return this.size.getHeight();
	}
	
	public void setHeight(float height) {
		this.size.setHeight(height);
	}
}
