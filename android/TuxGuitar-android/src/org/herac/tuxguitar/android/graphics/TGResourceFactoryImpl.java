package org.herac.tuxguitar.android.graphics;

import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;

import java.io.InputStream;

public class TGResourceFactoryImpl implements UIResourceFactory {

	public UIColor createColor(UIColorModel colorModel) {
		return new TGColorImpl(colorModel);
	}
	
	public UIColor createColor(int red, int green, int blue) {
		return createColor(new UIColorModel(red, green, blue));
	}
	
	public UIFont createFont(UIFontModel fontModel) {
		return new TGFontImpl(fontModel);
	}
	
	public UIFont createFont(String name, float height, boolean bold, boolean italic) {
		return createFont(new UIFontModel(name, height, bold, italic));
	}

	public UIImage createImage(float width, float height) {
		return new TGImageImpl(width, height);
	}

	public UIImage createImage(InputStream inputStream) {
		return new TGImageImpl(inputStream);
	}
}
