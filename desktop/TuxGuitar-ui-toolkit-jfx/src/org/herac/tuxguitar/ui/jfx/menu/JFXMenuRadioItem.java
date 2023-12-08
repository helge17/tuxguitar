package org.herac.tuxguitar.ui.jfx.menu;

import javafx.scene.control.RadioMenuItem;

import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;

public class JFXMenuRadioItem extends JFXMenuActionItem implements UIMenuCheckableItem {
	
	public JFXMenuRadioItem(JFXMenuItemContainer parent) {
		super(new RadioMenuItem(), parent);
	}

	public boolean isChecked() {
		return ((RadioMenuItem) this.getControl()).isSelected();
	}

	public void setChecked(boolean checked) {
		((RadioMenuItem) this.getControl()).setSelected(checked);
	}
}
