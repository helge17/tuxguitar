package org.herac.tuxguitar.graphics;

public interface TGResourceFactory {
	
	public TGImage createImage( int width, int height );
	
	public TGColor createColor(TGColorModel colorModel);
	
	public TGColor createColor( int red, int green, int blue);
	
	public TGFont createFont(TGFontModel fontModel);
	
	public TGFont createFont(String name, int height, boolean bold, boolean italic);
	
}
