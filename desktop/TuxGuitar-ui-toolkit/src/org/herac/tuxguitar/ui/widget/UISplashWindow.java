package org.herac.tuxguitar.ui.widget;

import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.resource.UIImage;

public interface UISplashWindow extends UIComponent {
	
	String getText();

	void setText(String text);
	
	UIImage getImage();
	
	void setImage(UIImage image);
	
	UIImage getSplashImage();
	
	void setSplashImage(UIImage splashImage);
	
	void open();
}
