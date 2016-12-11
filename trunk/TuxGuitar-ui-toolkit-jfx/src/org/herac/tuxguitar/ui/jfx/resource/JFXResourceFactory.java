package org.herac.tuxguitar.ui.jfx.resource;

import java.io.InputStream;

import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;

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
		return new JFXAWTImage(width, height);
	}
	
	public UIImage createImage(InputStream inputStream) {
		return new JFXSnapshotImage(inputStream);
	}
}
