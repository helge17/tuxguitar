package org.herac.tuxguitar.ui.layout;

import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;

public interface UILayout {
	
	<T extends Object> void set(String key, T value);
	
	<T extends Object> void set(UIControl control, String key, T value);
	
	<T extends Object> T get(String key);
	
	<T extends Object> T get(UIControl control, String key);
	
	UISize computePackedSize(UILayoutContainer container);
	
	void setBounds(UILayoutContainer container, UIRectangle bounds);
}
