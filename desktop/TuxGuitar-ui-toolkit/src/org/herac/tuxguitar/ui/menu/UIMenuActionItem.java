package org.herac.tuxguitar.ui.menu;

import org.herac.tuxguitar.ui.event.UISelectionListener;

public interface UIMenuActionItem extends UIMenuItem {

	void addSelectionListener(UISelectionListener listener);
	
	void removeSelectionListener(UISelectionListener listener);
}
