package org.herac.tuxguitar.app.graphics;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.ui.resource.UIColor;

public class TGColorImpl implements TGColor{
	
	private UIColor handle;
	
	public TGColorImpl(UIColor handle){
		this.handle = handle;
	}
	
	public UIColor getHandle(){
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
