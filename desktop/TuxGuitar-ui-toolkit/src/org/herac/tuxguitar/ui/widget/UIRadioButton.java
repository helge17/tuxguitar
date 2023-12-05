package org.herac.tuxguitar.ui.widget;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.resource.UIImage;

public interface UIRadioButton extends UIControl {
	
	String getText();

	void setText(String text);
	
	UIImage getImage();
	
	void setImage(UIImage image);
	
	boolean isSelected();
	
	void setSelected(boolean selected);
	
	void addSelectionListener(UISelectionListener listener);
	
	void removeSelectionListener(UISelectionListener listener);
}
