package org.herac.tuxguitar.ui.menu;

import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIKeyCombination;

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
