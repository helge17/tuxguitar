package org.herac.tuxguitar.app.graphics;

import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;

public class TGImageImpl implements TGImage {
	
	private UIResourceFactory factory;
	private UIImage handle;
	
	public TGImageImpl(UIResourceFactory factory, UIImage handle){
		this.factory = factory;
		this.handle = handle;
	}
	
	public TGPainter createPainter() {
		return new TGPainterImpl(this.factory, this.handle.createPainter());
	}
	
	public float getWidth() {
		return this.handle.getWidth();
	}
	
	public float getHeight() {
		return this.handle.getHeight();
	}
	
	public UIImage getHandle(){
		return this.handle;
	}
	
	public boolean isDisposed(){
		return this.handle.isDisposed();
	}
	
	public void dispose(){
		this.handle.dispose();
	}
}
