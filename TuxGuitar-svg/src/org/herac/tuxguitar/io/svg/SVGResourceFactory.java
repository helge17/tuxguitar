package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGResourceFactory;

public class SVGResourceFactory implements TGResourceFactory{
	
	public TGImage createImage(float width, float height) {
		return new SVGImage(width, height);
	}
	
	public TGColor createColor(TGColorModel colorModel) {
		return this.createColor(colorModel.getRed(), colorModel.getGreen(), colorModel.getBlue());
	}
	
	public TGColor createColor(int red, int green, int blue) {
		return new SVGColor(red, green, blue);
	}
	
	public TGFont createFont(TGFontModel fontModel) {
		return createFont(fontModel.getName(), fontModel.getHeight(), fontModel.isBold(), fontModel.isItalic() );
	}
	
	public TGFont createFont(String name, float height, boolean bold, boolean italic) {
		return new SVGFont(name, height, bold, italic);
	}
}
