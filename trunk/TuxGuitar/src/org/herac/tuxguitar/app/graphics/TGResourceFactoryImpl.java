package org.herac.tuxguitar.app.graphics;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;

public class TGResourceFactoryImpl implements TGResourceFactory {
	
	private UIResourceFactory factory;
	
	public TGResourceFactoryImpl(UIResourceFactory factory){
		this.factory = factory;
	}
	
	public UIResourceFactory getFactory() {
		return this.factory;
	}
	
	public TGImage createImage(float width, float height){
		return new TGImageImpl(this.getFactory(), this.getFactory().createImage(width, height));
	}
	
	public TGColor createColor(int red, int green, int blue){
		return new TGColorImpl(this.getFactory().createColor(red, green, blue));
	}
	
	public TGColor createColor(TGColorModel cm){
		return this.createColor(cm.getRed(), cm.getGreen(), cm.getBlue());
	}
	
	public TGFont createFont(String name, float height, boolean bold, boolean italic ){
		return new TGFontImpl(this.getFactory().createFont(name, height, bold, italic));
	}
	
	public TGFont createFont(TGFontModel fm){
		return this.createFont(fm.getName(), fm.getHeight(), fm.isBold(), fm.isItalic());
	}
}
