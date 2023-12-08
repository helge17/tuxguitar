package org.herac.tuxguitar.ui.toolbar;

import org.herac.tuxguitar.ui.widget.UIContainer;

public interface UIToolCustomItem extends UIContainer {
	
	String FILL = "fill";
	
	String PACKED_WIDTH = "packed_width";
	
	String MINIMUM_PACKED_WIDTH = "minimum_packed_width";
	
	String MAXIMUM_PACKED_WIDTH = "maximum_packed_width";
	
	<T extends Object> void setLayoutAttribute(String key, T value);
	
	<T extends Object> T getLayoutAttribute(String key);
}
