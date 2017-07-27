package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;

public class SVGImage implements UIImage{
	
	private StringBuffer buffer;
	
	private float width;
	private float height;
	
	private boolean disposed;
	
	public SVGImage(float width, float height){
		this.width = width;
		this.height = height;
		this.disposed = false;
		this.buffer = new StringBuffer();
	}
	
	public void dispose() {
		this.disposed = true;
	}
	
	public boolean isDisposed() {
		return this.disposed;
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public float getHeight() {
		return this.height;
	}
	
	public StringBuffer getBuffer(){
		return this.buffer;
	}
	
	public UIPainter createPainter() {
		return new SVGPainter( this.buffer );
	}
	
	public void applyTransparency(UIColor background) {
		// Not implemented
	}
}
