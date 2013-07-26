package org.herac.tuxguitar.graphics;

public class TGPoint {
	
	private int x;
	private int y;
	
	public TGPoint(int x,int y){
		this.x = x;
		this.y = y;
	}
	
	public TGPoint(){
		this(0,0);
	}
	
	public int getX() {
		return this.x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
