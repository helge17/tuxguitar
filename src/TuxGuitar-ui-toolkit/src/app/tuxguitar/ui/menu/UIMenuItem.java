package app.tuxguitar.ui.menu;

import app.tuxguitar.ui.UIComponent;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIKeyCombination;

public interface UIMenuItem extends UIComponent {

	String getText();

	void setText(String text);

	UIKeyCombination getKeyCombination();

	void setKeyCombination(UIKeyCombination keyCombination);

	UIImage getImage();

	void setImage(UIImage image);

	boolean isEnabled();

	void setEnabled(boolean enabled);
}
