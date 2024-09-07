package org.herac.tuxguitar.ui.resource;

import java.io.InputStream;

public interface UIResourceFactory {
	
	UIColor createColor(int red, int green, int blue);
	
	UIColor createColor(UIColorModel model);
	
	UIFont createFont(String name, float height, boolean bold, boolean italic);
	
	UIFont createFont(UIFontModel model);
	
	UIImage createImage(float width, float height);
	
	UIImage createImage(InputStream inputStream);
}
