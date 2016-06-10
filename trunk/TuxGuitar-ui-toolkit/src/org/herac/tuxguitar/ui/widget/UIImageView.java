package org.herac.tuxguitar.ui.widget;

import org.herac.tuxguitar.ui.resource.UIImage;

public interface UIImageView extends UIControl {
	
	UIImage getImage();
	
	void setImage(UIImage image);
	
	String getToolTipText();
	
	void setToolTipText(String text);
}
