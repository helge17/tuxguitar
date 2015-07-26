package org.herac.tuxguitar.app.graphics;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.herac.tuxguitar.graphics.TGColor;

public class TGColorImpl implements TGColor{
	
	private Color handle;
	
	public TGColorImpl( Color handle ){
		this.handle = handle;
	}
	
	public TGColorImpl( Device device , int red, int green, int blue ){
		this( new Color(device,red,green,blue) );
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
		return this.handle.isDisposed();
	}
	
	public void dispose(){
		this.handle.dispose();
	}
}
