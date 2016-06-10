package org.herac.tuxguitar.ui.swt.resource;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.swt.SWTComponent;

public class SWTColor extends SWTComponent<Color> implements UIColor{
	
	public SWTColor( Color handle ){
		super(handle);
	}
	
	public SWTColor( Device device , int red, int green, int blue ){
		this(new Color(device,red,green,blue));
	}
	
	public Color getHandle(){
		return this.getControl();
	}
	
	public int getRed() {
		return this.getControl().getRed();
	}
	
	public int getGreen() {
		return this.getControl().getGreen();
	}
	
	public int getBlue() {
		return this.getControl().getBlue();
	}
	
	public boolean isDisposed(){
		return this.getControl().isDisposed();
	}
	
	public void dispose(){
		this.getControl().dispose();
	}
}
