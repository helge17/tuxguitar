package org.herac.tuxguitar.ui.jfx.toolbar;

import org.herac.tuxguitar.ui.jfx.widget.JFXToggleButton;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;

public class JFXToolCheckableItem extends JFXToggleButton implements UIToolCheckableItem {
	
	public JFXToolCheckableItem(JFXToolBar parent) {
		super(parent);
		
		this.getControl().setFocusTraversable(false);
	}

	public boolean isChecked() {
		return this.getControl().isSelected();
	}

	public void setChecked(boolean checked) {
		this.getControl().setSelected(checked);
	}
}
