package org.herac.tuxguitar.awt.graphics;

import java.awt.Color;

import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;

public class AWTColor implements UIColor{
	
	private UIColorModel model;
	
	public AWTColor(UIColorModel model){
		this.model = model;
	}
	
	public AWTColor(int red, int green, int blue ){
		this(new UIColorModel(red,green,blue));
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
