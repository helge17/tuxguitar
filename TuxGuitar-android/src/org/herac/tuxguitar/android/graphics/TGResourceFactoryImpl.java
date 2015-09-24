package org.herac.tuxguitar.android.graphics;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGResourceFactory;

public class TGResourceFactoryImpl implements TGResourceFactory{
	
	public TGImage createImage(float width, float height) {
		return new TGImageImpl(width, height);
	}
	
	public TGColor createColor(TGColorModel colorModel) {
		return new TGColorImpl(colorModel);
	}
	
	public TGColor createColor(int red, int green, int blue) {
		return createColor(new TGColorModel(red, green, blue));
	}
	
	public TGFont createFont(TGFontModel fontModel) {
		return new TGFontImpl(fontModel);
	}
	
	public TGFont createFont(String name, float height, boolean bold, boolean italic) {
		return createFont(new TGFontModel(name, height, bold, italic));
	}

}
