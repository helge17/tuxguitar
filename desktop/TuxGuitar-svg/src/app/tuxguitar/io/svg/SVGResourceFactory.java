package app.tuxguitar.io.svg;

import java.io.InputStream;

import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIFontModel;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIResourceFactory;

public class SVGResourceFactory implements UIResourceFactory{

	public UIImage createImage(float width, float height) {
		return new SVGImage(width, height);
	}

	public UIColor createColor(UIColorModel colorModel) {
		return this.createColor(colorModel.getRed(), colorModel.getGreen(), colorModel.getBlue());
	}

	public UIColor createColor(int red, int green, int blue) {
		return new SVGColor(red, green, blue);
	}

	public UIFont createFont(UIFontModel fontModel) {
		return createFont(fontModel.getName(), fontModel.getHeight(), fontModel.isBold(), fontModel.isItalic() );
	}

	public UIFont createFont(String name, float height, boolean bold, boolean italic) {
		return new SVGFont(name, height, bold, italic);
	}

	public UIImage createImage(InputStream inputStream) {
		return null;
	}
}
