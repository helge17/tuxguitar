package org.herac.tuxguitar.graphics;

public interface TGResourceFactory {
	
	public TGImage createImage( float width, float height );
	
	public TGColor createColor(TGColorModel colorModel);
	
	public TGColor createColor( int red, int green, int blue);
	
	public TGFont createFont(TGFontModel fontModel);
	
	public TGFont createFont(String name, float height, boolean bold, boolean italic);
	
}
