package org.herac.tuxguitar.ui.menu;

import org.herac.tuxguitar.ui.event.UIMenuHideListener;
import org.herac.tuxguitar.ui.event.UIMenuShowListener;
import org.herac.tuxguitar.ui.resource.UIPosition;

public interface UIPopupMenu extends UIMenu {
	
	void open(UIPosition position);
	
	void addMenuShowListener(UIMenuShowListener listener);
	
	void removeMenuShowListener(UIMenuShowListener listener);
	
	void addMenuHideListener(UIMenuHideListener listener);
	
	void removeMenuHideListener(UIMenuHideListener listener);
}
