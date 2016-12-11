package org.herac.tuxguitar.ui.jfx.toolbar;

import org.herac.tuxguitar.ui.jfx.widget.JFXButton;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;

public class JFXToolActionItem extends JFXButton implements UIToolActionItem {
	
	public JFXToolActionItem(JFXToolBar parent) {
		super(parent);
		
		this.getControl().setFocusTraversable(false);
	}
}
