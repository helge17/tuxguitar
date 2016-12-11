package org.herac.tuxguitar.ui.jfx.toolbar;

import javafx.geometry.Orientation;

import org.herac.tuxguitar.ui.jfx.widget.JFXSeparator;

public class JFXToolSeparatorItem extends JFXSeparator {
	
	public JFXToolSeparatorItem(JFXToolBar parent) {
		super(parent, (Orientation.HORIZONTAL.equals(parent.getControl().getOrientation()) ? Orientation.VERTICAL : Orientation.HORIZONTAL));
		
		this.getControl().setFocusTraversable(false);
	}
}
