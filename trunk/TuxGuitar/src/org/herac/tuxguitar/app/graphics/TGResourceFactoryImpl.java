package org.herac.tuxguitar.app.graphics;

import org.eclipse.swt.graphics.Device;
import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGResourceFactory;

public class TGResourceFactoryImpl implements TGResourceFactory {
	
	private Device device;
	
	public TGResourceFactoryImpl(){
		this(null);
	}
	
	public TGResourceFactoryImpl( Device device ){
		this.device = device;
	}
	
	public void setDevice(Device device) {
		this.device = device;
	}
	
	public Device getDevice() {
		return this.device;
	}
	
	public TGImage createImage( float width, float height ){
		return new TGImageImpl( this.getDevice() , width, height );
	}
	
	public TGColor createColor( int red, int green, int blue ){
		return new TGColorImpl( this.getDevice() , red, green , blue );
	}
	
	public TGColor createColor( TGColorModel cm ){
		return this.createColor(cm.getRed(), cm.getGreen(), cm.getBlue());
	}
	
	public TGFont createFont( String name, float height, boolean bold, boolean italic ){
		return new TGFontImpl( this.getDevice() , name , height , bold , italic );
	}
	
	public TGFont createFont( TGFontModel fm ){
		return this.createFont(fm.getName(), fm.getHeight(), fm.isBold(), fm.isItalic());
	}
}
