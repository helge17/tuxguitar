package app.tuxguitar.ui.menu;

import app.tuxguitar.ui.event.UISelectionListener;

public interface UIMenuActionItem extends UIMenuItem {

	void addSelectionListener(UISelectionListener listener);

	void removeSelectionListener(UISelectionListener listener);
}
