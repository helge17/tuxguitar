package org.herac.tuxguitar.ui.menu;

import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIKeyConvination;

public interface UIMenuItem extends UIComponent {
	
	String getText();
	
	void setText(String text);
	
	UIKeyConvination getKeyConvination();
	
	void setKeyConvination(UIKeyConvination keyConvination);
	
	UIImage getImage();
	
	void setImage(UIImage image);
	
	boolean isEnabled();

	void setEnabled(boolean enabled);
}
