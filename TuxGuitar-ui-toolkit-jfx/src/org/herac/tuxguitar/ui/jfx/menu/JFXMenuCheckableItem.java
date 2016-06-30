package org.herac.tuxguitar.ui.jfx.menu;

import javafx.scene.control.CheckMenuItem;

import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;

public class JFXMenuCheckableItem extends JFXMenuActionItem implements UIMenuCheckableItem {
	
	public JFXMenuCheckableItem(JFXMenuItemContainer parent) {
		super(new CheckMenuItem(), parent);
	}

	public boolean isChecked() {
		return ((CheckMenuItem) this.getControl()).isSelected();
	}

	public void setChecked(boolean checked) {
		((CheckMenuItem) this.getControl()).setSelected(checked);
	}
}
