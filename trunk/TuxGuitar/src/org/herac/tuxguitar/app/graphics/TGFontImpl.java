package org.herac.tuxguitar.app.graphics;

import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.ui.resource.UIFont;

public class TGFontImpl implements TGFont {
	
	private UIFont handle;
	
	public TGFontImpl(UIFont handle) {
		this.handle = handle;
	}
	
	public void dispose(){
		this.handle.dispose();
	}
	
	public boolean isDisposed(){
		return this.handle.isDisposed();
	}
	
	public UIFont getHandle(){
		return this.handle;
	}
	
	public String getName() {
		return this.handle.getName();
	}
	
	public float getHeight() {
		return this.handle.getHeight();
	}
	
	public boolean isBold() {
		return this.handle.isBold();
	}
	
	public boolean isItalic() {
		return this.handle.isItalic();
	}
}
