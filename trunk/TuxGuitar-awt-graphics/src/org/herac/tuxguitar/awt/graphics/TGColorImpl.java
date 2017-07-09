package org.herac.tuxguitar.awt.graphics;

import java.awt.Color;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGColorModel;

public class TGColorImpl implements TGColor{
	
	private TGColorModel model;
	
	public TGColorImpl(TGColorModel model){
		this.model = model;
	}
	
	public TGColorImpl(int red, int green, int blue ){
		this(new TGColorModel(red,green,blue));
	}
	
	public Color getHandle(int alpha) {
		return new Color(this.model.getRed(), this.model.getGreen(), this.model.getBlue(), alpha);
	}
	
	public Color getHandle(){
		return this.getHandle(255);
	}
	
	public int getRed() {
		return this.model.getRed();
	}
	
	public int getGreen() {
		return this.model.getGreen();
	}
	
	public int getBlue() {
		return this.model.getBlue();
	}
	
	public boolean isDisposed(){
		return (this.model == null); 
	}
	
	public void dispose(){
		this.model = null;
	}
}
