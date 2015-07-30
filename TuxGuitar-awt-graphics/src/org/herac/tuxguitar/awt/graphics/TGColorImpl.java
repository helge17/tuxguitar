package org.herac.tuxguitar.awt.graphics;

import java.awt.Color;

import org.herac.tuxguitar.graphics.TGColor;

public class TGColorImpl implements TGColor{
	
	private Color handle;
	
	public TGColorImpl( Color handle ){
		this.handle = handle;
	}
	
	public TGColorImpl(int red, int green, int blue ){
		this( new Color(red,green,blue) );
	}
	
	public Color getHandle(){
		return this.handle;
	}
	
	public int getRed() {
		return this.handle.getRed();
	}
	
	public int getGreen() {
		return this.handle.getGreen();
	}
	
	public int getBlue() {
		return this.handle.getBlue();
	}
	
	public boolean isDisposed(){
		return (this.handle == null); 
	}
	
	public void dispose(){
		this.handle = null;
	}
}
