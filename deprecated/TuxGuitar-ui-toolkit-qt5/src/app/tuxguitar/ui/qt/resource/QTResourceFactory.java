package app.tuxguitar.ui.qt.resource;

import java.io.InputStream;

import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIFontModel;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIResourceFactory;

public class QTResourceFactory implements UIResourceFactory {

	public QTResourceFactory(){
		super();
	}

	public UIColor createColor(int red, int green, int blue) {
		return new QTColor(red, green , blue);
	}

	public UIColor createColor(UIColorModel model) {
		return this.createColor(model.getRed(), model.getGreen(), model.getBlue());
	}

	public UIFont createFont(String name, float height, boolean bold, boolean italic) {
		return this.createFont(new UIFontModel(name, height, bold, italic));
	}

	public UIFont createFont(UIFontModel model) {
		return new QTFont(model);
	}

	public UIImage createImage(float width, float height) {
		return new QTImage(width, height);
	}

	public UIImage createImage(InputStream inputStream) {
		return new QTImage(inputStream);
	}
}
