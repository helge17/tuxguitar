package org.herac.tuxguitar.ui.toolbar;

import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.widget.UIControl;

public interface UIToolBar extends UIControl {
	
	UIToolActionItem createActionItem();
	
	UIToolCheckableItem createCheckItem();
	
	UIToolMenuItem createMenuItem();
	
	UIToolActionMenuItem createActionMenuItem();
	
	UIToolCustomItem createCustomItem();
	
	UIComponent createSeparator();
}
