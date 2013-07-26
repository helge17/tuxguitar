package org.herac.tuxguitar.graphics;

public interface TGImage extends TGResource,TGPainterFactory {
	
	public int getWidth();
	
	public int getHeight();
	
	public void applyTransparency( TGColor background );
}
