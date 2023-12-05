package org.herac.tuxguitar.ui.jfx.menu;

import javafx.scene.control.SeparatorMenuItem;

import org.herac.tuxguitar.ui.UIComponent;

public class JFXMenuSeparatorItem extends JFXMenuItem<SeparatorMenuItem> implements UIComponent {
	
	public JFXMenuSeparatorItem(JFXMenuItemContainer parent) {
		super(new SeparatorMenuItem(), parent);
	}
}
