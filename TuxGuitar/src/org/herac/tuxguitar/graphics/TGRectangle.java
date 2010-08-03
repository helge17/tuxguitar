package org.herac.tuxguitar.graphics;

public class TGRectangle {
	
	private TGPoint location;
	private TGDimension size;
	
	public TGRectangle(int x, int y, int width, int height){
		this.location = new TGPoint(x,y);
		this.size = new TGDimension(width,height);
	}
	
	public TGRectangle(){
		this(0,0,0,0);
	}
	
	public int getX() {
		return this.location.getX();
	}
	
	public void setX(int x) {
		this.location.setX(x);
	}
	
	public int getY() {
		return this.location.getY();
	}
	
	public void setY(int y) {
		this.location.setY(y);
	}
	
	public int getWidth() {
		return this.size.getWidth();
	}
	
	public void setWidth(int width) {
		this.size.setWidth(width);
	}
	
	public int getHeight() {
		return this.size.getHeight();
	}
	
	public void setHeight(int height) {
		this.size.setHeight(height);
	}
}
