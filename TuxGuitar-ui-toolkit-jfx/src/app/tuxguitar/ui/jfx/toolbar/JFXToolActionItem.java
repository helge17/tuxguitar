package app.tuxguitar.ui.jfx.toolbar;

import app.tuxguitar.ui.jfx.widget.JFXButton;
import app.tuxguitar.ui.toolbar.UIToolActionItem;

public class JFXToolActionItem extends JFXButton implements UIToolActionItem {

	public JFXToolActionItem(JFXToolBar parent) {
		super(parent);

		this.getControl().setFocusTraversable(false);
	}
}
