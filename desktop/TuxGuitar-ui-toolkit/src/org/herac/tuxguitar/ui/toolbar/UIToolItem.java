package org.herac.tuxguitar.ui.toolbar;

import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.resource.UIImage;

public interface UIToolItem extends UIComponent {
	
	String getText();

	void setText(String text);
	
	String getToolTipText();
	
	void setToolTipText(String text);
	
	UIImage getImage();
	
	void setImage(UIImage image);
	
	boolean isEnabled();

	void setEnabled(boolean enabled);
}
