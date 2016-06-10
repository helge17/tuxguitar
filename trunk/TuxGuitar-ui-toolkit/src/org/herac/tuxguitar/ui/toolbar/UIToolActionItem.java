package org.herac.tuxguitar.ui.toolbar;

import org.herac.tuxguitar.ui.event.UISelectionListener;

public interface UIToolActionItem extends UIToolItem {
	
	void addSelectionListener(UISelectionListener listener);
	
	void removeSelectionListener(UISelectionListener listener);
}
