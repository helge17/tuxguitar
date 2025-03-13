package app.tuxguitar.ui.widget;

import app.tuxguitar.ui.UIComponent;
import app.tuxguitar.ui.resource.UIImage;

public interface UISplashWindow extends UIComponent {

	String getText();

	void setText(String text);

	UIImage getImage();

	void setImage(UIImage image);

	UIImage getSplashImage();

	void setSplashImage(UIImage splashImage);

	void open();
}
