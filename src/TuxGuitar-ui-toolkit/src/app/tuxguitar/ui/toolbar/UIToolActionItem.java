package app.tuxguitar.ui.toolbar;

import app.tuxguitar.ui.event.UISelectionListener;

public interface UIToolActionItem extends UIToolItem {

	void addSelectionListener(UISelectionListener listener);

	void removeSelectionListener(UISelectionListener listener);
}
