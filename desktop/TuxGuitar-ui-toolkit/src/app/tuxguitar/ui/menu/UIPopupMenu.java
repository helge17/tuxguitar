package app.tuxguitar.ui.menu;

import app.tuxguitar.ui.event.UIMenuHideListener;
import app.tuxguitar.ui.event.UIMenuShowListener;
import app.tuxguitar.ui.resource.UIPosition;

public interface UIPopupMenu extends UIMenu {

	void open(UIPosition position);

	void addMenuShowListener(UIMenuShowListener listener);

	void removeMenuShowListener(UIMenuShowListener listener);

	void addMenuHideListener(UIMenuHideListener listener);

	void removeMenuHideListener(UIMenuHideListener listener);
}
