package org.herac.tuxguitar.ui.menu;

public interface UIMenuCheckableItem extends UIMenuActionItem {
	
	boolean isChecked();

	void setChecked(boolean checked);
}
