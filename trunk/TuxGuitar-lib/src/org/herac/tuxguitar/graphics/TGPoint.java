package org.herac.tuxguitar.graphics;

public class TGPoint {
	
	private float x;
	private float y;
	
	public TGPoint(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public TGPoint(){
		this(0, 0);
	}
	
	public float getX() {
		return this.x;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public void setY(float y) {
		this.y = y;
	}
}
