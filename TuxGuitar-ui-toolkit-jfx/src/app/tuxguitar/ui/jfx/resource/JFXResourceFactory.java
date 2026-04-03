package app.tuxguitar.ui.jfx.resource;

import java.io.InputStream;

import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIFontModel;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIResourceFactory;

public class JFXResourceFactory implements UIResourceFactory {

	public JFXResourceFactory(){
		super();
	}

	public UIColor createColor(int red, int green, int blue) {
		return new JFXColor(red, green , blue);
	}

	public UIColor createColor(UIColorModel model) {
		return this.createColor(model.getRed(), model.getGreen(), model.getBlue());
	}

	public UIFont createFont(String name, float height, boolean bold, boolean italic) {
		return new JFXFont(name, height, bold, italic);
	}

	public UIFont createFont(UIFontModel model) {
		return this.createFont(model.getName(), model.getHeight(), model.isBold(), model.isItalic());
	}

	public UIImage createImage(float width, float height) {
		return new JFXImage(width, height);
	}

	public UIImage createImage(InputStream inputStream) {
		return new JFXImage(inputStream);
	}
}
