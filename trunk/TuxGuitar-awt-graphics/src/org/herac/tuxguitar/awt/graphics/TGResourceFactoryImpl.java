package org.herac.tuxguitar.awt.graphics;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGResourceFactory;

public class TGResourceFactoryImpl implements TGResourceFactory {
	
	public TGResourceFactoryImpl(){
		super();
	}
	
	public TGImage createImage( float width, float height ){
		return new TGImageImpl( width, height );
	}
	
	public TGColor createColor( int red, int green, int blue ){
		return new TGColorImpl( red, green , blue );
	}
	
	public TGColor createColor( TGColorModel cm ){
		return this.createColor(cm.getRed(), cm.getGreen(), cm.getBlue());
	}
	
	public TGFont createFont( String name, float height, boolean bold, boolean italic ){
		return new TGFontImpl( name , height , bold , italic );
	}
	
	public TGFont createFont( TGFontModel fm ){
		return this.createFont(fm.getName(), fm.getHeight(), fm.isBold(), fm.isItalic());
	}
}
